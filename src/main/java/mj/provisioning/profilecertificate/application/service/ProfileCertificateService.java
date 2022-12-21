package mj.provisioning.profilecertificate.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import mj.provisioning.certificate.application.port.out.CertificateRepositoryPort;
import mj.provisioning.certificate.domain.Certificate;
import mj.provisioning.certificate.domain.CertificateType;
import mj.provisioning.profile.application.port.in.ProfileEditRequestDto;
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

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileCertificateService implements ProfileCertificateUseCase {

    private final AppleApi appleApi;
    private final ProfileCertificateRepositoryPort profileCertificateRepositoryPort;
    private final ProfileRepositoryPort profileRepositoryPort;
    private final CertificateRepositoryPort certificateRepositoryPort;

    @Override
    public void saveProfileCertificate(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileId(profileId).orElseThrow(()-> new RuntimeException("존재하지 않는 프로비저닝입니다."));
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
                String certificateId = dd.get("id").toString().replaceAll("\"","");
                String type = dd.get("type").toString().replaceAll("\"","");
                JsonObject attributes = dd.getAsJsonObject("attributes");
                String displayName = attributes.get("displayName").toString().replaceAll("\"", "");
                String certificateType = attributes.get("certificateType").toString().replaceAll("\"", "");
                String expirationDate = attributes.get("expirationDate").toString().replaceAll("\"", "").replaceAll("[^0-9]", "");
                expirationDate = expirationDate.substring(0, 4) + "/" + expirationDate.substring(4, 6) + "/" + expirationDate.substring(6, 8);

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

    /**
     * edit 페이지에 출력하는 용도.
     * @param profileId
     * @return
     */
    @Override
    public ProfileCertificateShowListDto getProfileCertificateList(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileId(profileId).orElseThrow(()-> new RuntimeException("존재하지 않는 프로비저닝입니다."));
        // 개발 인증서인지 운영 인증서인지 체크
        String certificateType = profile.getProfileType().getValue();
        List<Certificate> byCertificateType = certificateRepositoryPort.findByCertificateType(CertificateType.get(certificateType)).orElseThrow(()->new RuntimeException("해당 타입에 매치되는 인증서가 존재하지 않습니다."));

        // select 여부 체크해서 return
        final long[] idx = {0};
        return ProfileCertificateShowListDto.builder().certificateData(byCertificateType.stream().map(certificate -> {
             return ProfileCertificateShowDto.of(certificate, profileCertificateRepositoryPort.isExist(certificate.getCertificateId(), profile), idx[0]++);
        }).collect(Collectors.toList())).build();
    }

    @Override
    public List<ProfileCertificateShowDto> getProfileCertificateForEdit(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileId(profileId).orElseThrow(()-> new RuntimeException("존재하지 않는 프로비저닝입니다."));
        // 개발 인증서인지 운영 인증서인지 체크
        String certificateType = profile.getProfileType().getValue();
        List<Certificate> byCertificateType = certificateRepositoryPort.findByCertificateType(CertificateType.get(certificateType)).orElseThrow(()->new RuntimeException("해당 타입에 매치되는 인증서가 존재하지 않습니다."));
        final long[] idx = {0};
        return byCertificateType.stream().map(certificate -> {
            return ProfileCertificateShowDto.of(certificate, profileCertificateRepositoryPort.isExist(certificate.getCertificateId(), profile), idx[0]++);
        }).collect(Collectors.toList());
    }

    @Override
    public JsonArray getProfileCertificates(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileId(profileId).orElseThrow(()-> new RuntimeException("존재하지 않는 프로비저닝입니다."));
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
        certificateData.stream().forEach(profileCertificateShowDto -> {
            if (profileCertificateShowDto.isSelected()) { // true인 애들만
                JsonObject object = new JsonObject();
                object.addProperty("type", profileCertificateShowDto.getType());
                object.addProperty("id", profileCertificateShowDto.getCertificateId());
                certificates.add(object);
            }
        });
        JsonObject param = new JsonObject();
        param.add("data", certificates);
        return param;
    }

}
