package mj.provisioning.profile.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import mj.provisioning.device.application.port.in.DeviceShowDto;
import mj.provisioning.profile.application.port.in.ProfileEditShowDto;
import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profile.domain.ProfilePlatform;
import mj.provisioning.profile.domain.ProfileState;
import mj.provisioning.profile.domain.ProfileType;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleShowDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleUseCase;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateShowDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ProfileService implements ProfileUseCase {

    private final ProfileRepositoryPort profileRepositoryPort;
    private final AppleApi appleApi;

    private final ProfileBundleUseCase profileBundleUseCase;
    private final ProfileDeviceUseCase profileDeviceUseCase;
    private final ProfileCertificateUseCase profileCertificateUseCase;


    /**
     *  base64 인코딩된거를 디코드하고 .mobileprovision 형식으로 저장하면 됨.
     *  추후 요청시 profile 업데이트하고 content 가져와서 파일로 저장후 svn에 올리자
     *  스프링 배치 돌려서 새벽에 돌도록 나중에 설정하자.
     */
    @Override
    public void saveProfiles() {
        // profile 생성 전략이
        // 수정을 해도 모든값이 변경되어 저장이된다.
        // 어차피 값도 작으니 그냥 싹 삭제후 다시 받아오는게 최선인듯
        profileRepositoryPort.deleteAll();
        String response = appleApi.getProfileInfo();
        JsonParser parser = new JsonParser();
        JsonObject parse = parser.parse(response).getAsJsonObject();
        JsonArray data = parse.get("data").getAsJsonArray();
        List<Profile> profiles = new ArrayList<>();
        for (JsonElement datum : data) {
            JsonObject dd = datum.getAsJsonObject();
            JsonObject attributes = dd.getAsJsonObject("attributes");
            String profileId = dd.get("id").toString().replaceAll("\"", "");
            String type = dd.get("type").toString().replaceAll("\"", "");
            String profileName = attributes.get("name").toString().replaceAll("\"", "");
            String expirationDate = attributes.get("expirationDate").toString().replaceAll("\"", "").replaceAll("[^0-9]", "");
            expirationDate = expirationDate.substring(0, 4) + "/" + expirationDate.substring(4, 6) + "/" + expirationDate.substring(6, 8);
            String platform = attributes.get("platform").toString().replaceAll("\"", "");
            String profileState = attributes.get("profileState").toString().replaceAll("\"", "");
            String profileType = attributes.get("profileType").toString().replaceAll("\"", "");
            String profileContent = attributes.get("profileContent").toString().replaceAll("\"", "");
            String uuid = attributes.get("uuid").toString().replaceAll("\"", "");
            /**
             * 빌더 패턴 사용시 기본 생성자 전략 무시함!!
             * 따라서 nullpointer 조심하자~
             */
            Profile build = Profile.builder().profileId(profileId)
                    .name(profileName)
                    .type(type)
                    .profileState(ProfileState.get(profileState))
                    .profileType(ProfileType.get(profileType))
                    .expirationDate(expirationDate)
                    .platform(ProfilePlatform.get(platform))
                    .profileContent(profileContent)
                    .uuid(uuid)
                    .deviceList(new ArrayList<>())
                    .certificates(new ArrayList<>())
                    .build();
            // 개발용만 device가 존재
            profiles.add(build);
            }
        profileRepositoryPort.saveAll(profiles);
    }

    @Override
    public void saveProfile(Profile profile) {
        profileRepositoryPort.save(profile);
    }

    @Override
    public void updateProfile(Profile profile, JsonObject param) {
        profile.updateProfile(param);
    }

    /**
     * profileId는 고유하기 때문에 문제 없음.
     * @param profileId
     */
    @Override
    public void deleteProfile(String profileId) {
        appleApi.deleteProfile(profileId);
        profileRepositoryPort.deleteProfile(profileId);
    }

    @Override
    public List<ProfileShowDto> searchByCondition(ProfileSearchCondition condition) {
        return profileRepositoryPort.searchCondition(condition);
    }

    @Override
    public List<Profile> findAll() {
        return profileRepositoryPort.findAll();
    }

    @Override
    public Profile getProfile(String profileId) {
        return profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(() -> new RuntimeException("해당 조건에 맞는 프로비저닝이 존재하지 않습니다."));
    }

    @Override
    public ProfileEditShowDto getEditShow(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(() -> new RuntimeException("해당 조건에 맞는 프로비저닝이 존재하지 않습니다."));
        List<ProfileBundleShowDto> bundleList = profileBundleUseCase.getBundleForEdit(profileId);
        List<DeviceShowDto> deviceShowList = new ArrayList<>();
        if (profile.getProfileType().equals(ProfileType.IOS_APP_DEVELOPMENT)) // 개발만 기기 등록이 가능함.
            deviceShowList = profileDeviceUseCase.getDeviceForEdit(profileId);
        List<ProfileCertificateShowDto> profileCertificateList = profileCertificateUseCase.getProfileCertificateForEdit(profileId);
        return ProfileEditShowDto.builder().name(profile.getName())
                .expires(profile.getExpirationDate())
                .type(profile.getProfileType().name())
                .status(profile.getProfileState().getValue())
                .certificates(profileCertificateList)
                .bundle(bundleList)
                .devices(deviceShowList)
                .profileId(profileId)
                .build();
    }

    @Override
    public List<Profile> findByNameLike(String name) {
        return profileRepositoryPort.findByNameLike(name);
    }

}
