package mj.provisioning.profilecertificate.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import mj.provisioning.certificate.application.port.out.CertificateRepositoryPort;
import mj.provisioning.certificate.domain.Certificate;
import mj.provisioning.certificate.domain.CertificateType;
import mj.provisioning.common.exception.CustomException;
import mj.provisioning.common.exception.ErrorCode;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateShowDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateShowListDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import mj.provisioning.profilecertificate.application.port.out.ProfileCertificateRepositoryPort;
import mj.provisioning.profilecertificate.domain.ProfileCertificate;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static mj.provisioning.util.RegexParsing.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileCertificateService implements ProfileCertificateUseCase {

    private final AppleApi appleApi;
    private final ProfileCertificateRepositoryPort profileCertificateRepositoryPort;
    private final ProfileRepositoryPort profileRepositoryPort;
    private final CertificateRepositoryPort certificateRepositoryPort;

    /**
     * profile과 매핑된 인증서 업데이트
     * @param profileId
     */
    @Override
    public void saveProfileCertificate(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new RuntimeException("존재하지 않는 프로비저닝입니다."));
        // 기존 삭제
        profileCertificateRepositoryPort.deleteByProfileId(profile);
        List<ProfileCertificate> profileCertificates = new ArrayList<>();
        String response = appleApi.getProfileCertificate(profileId);

        JsonParser parser = new JsonParser();
        JsonObject parse = parser.parse(response).getAsJsonObject();
        JsonArray data = parse.getAsJsonArray("data");
        if (data!=null) {
            for (JsonElement datum : data) {
                JsonObject dd = datum.getAsJsonObject();
                String certificateId = parseQuotations(dd.get("id").toString());
                String type = parseQuotations(dd.get("type").toString());
                JsonObject attributes = dd.getAsJsonObject("attributes");
                String displayName = parseQuotations(attributes.get("displayName").toString());
                String certificateType = parseQuotations(attributes.get("certificateType").toString());
                String expirationDate = parseDateFormat(attributes.get("expirationDate").toString());

                ProfileCertificate profileCertificate = ProfileCertificate.builder().certificateId(certificateId)
                        .type(type)
                        .expirationDate(expirationDate)
                        .displayName(displayName)
                        .certificateType(CertificateType.get(certificateType))
                        .build();
                profileCertificates.add(profileCertificate);
            }
            profile.insertAllCertificate(profileCertificates);
            profileCertificateRepositoryPort.saveAll(profileCertificates);
        }
    }

    @Override
    public void deleteByProfile(Profile profile) {
        profileCertificateRepositoryPort.deleteByProfileId(profile);
    }

    /**
     * 선택된 id들로 인증서를 찾는다.
     * 그리고 연관관계 매핑
     * @param profile
     * @param certificateIds
     */
    @Override
    public void saveUpdatedResult(Profile profile, List<String> certificateIds) {
        List<ProfileCertificate> profileCertificates = certificateRepositoryPort.findByCertificateIds(certificateIds).stream().map(ProfileCertificate::of).collect(Collectors.toList());
        profile.insertAllCertificate(profileCertificates);
        profileCertificateRepositoryPort.saveAll(profileCertificates);
    }

    /**
     * edit 페이지에 출력하는 용도.
     * @param profileId
     * @return
     */
    @Override
    public ProfileCertificateShowListDto getProfileCertificateList(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new CustomException(ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        // 개발 인증서인지 운영 인증서인지 체크
        String certificateType = profile.getProfileType().getValue();
        List<Certificate> byCertificateType = certificateRepositoryPort.findByCertificateType(CertificateType.get(certificateType)).orElseThrow(()->new CustomException(ErrorCode.CERTIFICATE_NOT_EXIST.getMessage(), ErrorCode.CERTIFICATE_NOT_EXIST));
        // select 여부 체크해서 return
        final long[] idx = {0};
        return ProfileCertificateShowListDto.builder().certificateData(byCertificateType.stream().map(certificate -> ProfileCertificateShowDto.of(certificate, profileCertificateRepositoryPort.isExist(certificate.getCertificateId(), profile), idx[0]++)).collect(Collectors.toList())).build();
    }

    @Override
    public List<ProfileCertificateShowDto> getProfileCertificateForEdit(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new CustomException(ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        // 개발 인증서인지 운영 인증서인지 체크
        String certificateType = profile.getProfileType().getValue();
        List<Certificate> byCertificateType = certificateRepositoryPort.findByCertificateType(CertificateType.get(certificateType)).orElseThrow(()->new CustomException(ErrorCode.CERTIFICATE_NOT_EXIST.getMessage(), ErrorCode.CERTIFICATE_NOT_EXIST));
        final long[] idx = {0};
        return byCertificateType.stream().map(certificate -> ProfileCertificateShowDto.of(certificate, profileCertificateRepositoryPort.isExist(certificate.getCertificateId(), profile), idx[0]++)).collect(Collectors.toList());
    }

    @Override
    public JsonArray getProfileCertificates(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new CustomException(ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        List<ProfileCertificate> byProfileId = profileCertificateRepositoryPort.findByProfileId(profile);
        JsonArray certificates = new JsonArray();
        byProfileId.stream().forEach(profileCertificate -> {
            JsonObject object = new JsonObject();
            object.addProperty("type", profileCertificate.getType());
            object.addProperty("id", profileCertificate.getCertificateId());
            certificates.add(object);
        });
        return certificates;
    }

    @Override
    public JsonObject getProfileCertificatesForUpdate(String profileId) {
        JsonObject object = new JsonObject();
        JsonArray profileCertificates = getProfileCertificates(profileId);
        object.add("data", profileCertificates);
        return object;
    }

    /**
     * request의 인증서 데이터 기반으로
     * 파싱을 진행     * @param certificateData
     * @return
     */
    @Override
    public JsonObject getProfileCertificatesForUpdate(List<ProfileCertificateShowDto> certificateData) {
        JsonArray certificates = new JsonArray();
        certificateData.stream().filter(ProfileCertificateShowDto::isChosen).forEach(profileCertificateShowDto -> {
            JsonObject object = new JsonObject();
            object.addProperty("type", profileCertificateShowDto.getType());
            object.addProperty("id", profileCertificateShowDto.getCertificateId());
            certificates.add(object);
        });
        JsonObject param = new JsonObject();
        param.add("data", certificates);
        return param;
    }

}
