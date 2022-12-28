package mj.provisioning.profile.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.device.application.port.in.DeviceShowDto;
import mj.provisioning.profile.application.port.in.*;
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

@Slf4j
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

    /**
     * @param editRequestDto
     * @return new Profile
     */
    @Override
    public Profile editProvisioning(ProfileEditRequestDto editRequestDto) {
        // get Profile
        Profile prev = getProfile(editRequestDto.getProfileId());
        // get Json Param
        JsonObject attr = new JsonObject();
        attr.addProperty("name", editRequestDto.getName());
        attr.addProperty("profileType", editRequestDto.getType()); // dist, dev
        JsonObject relationships = new JsonObject();

        JsonObject profileCertificatesForUpdate = profileCertificateUseCase.getProfileCertificatesForUpdate(editRequestDto.getCertificates());
        JsonObject profileBundleForUpdate = profileBundleUseCase.getProfileBundleForUpdate(editRequestDto.getBundles());
        JsonObject deviceForUpdateProfile = null;
        // 운영이 아닌애들은 전부 디바이스를 가짐
        if (!editRequestDto.getType().equals(ProfileType.IOS_APP_STORE.name())) {
            deviceForUpdateProfile = profileDeviceUseCase.getDeviceForUpdateProfile(editRequestDto.getDevices());
            relationships.add("devices", deviceForUpdateProfile);
        }
        relationships.add("bundleId", profileBundleForUpdate);
        relationships.add("certificates", profileCertificatesForUpdate);

        JsonObject param = new JsonObject();
        param.addProperty("type", "profiles");
        param.add("attributes", attr);
        param.add("relationships", relationships);

        JsonObject toPost = new JsonObject();
        toPost.add("data", param);

        System.out.println("toPost = " + toPost);
        // 새로 생성
        String profile = appleApi.createProfileNew(appleApi.createJWT(), toPost);
        System.out.println("profile = " + profile);

        // 파싱해서 업데이트하자
        JsonParser parser = new JsonParser();
        JsonObject updatedResult = parser.parse(profile).getAsJsonObject();
        JsonObject data = updatedResult.getAsJsonObject("data");
        // 이전 프로파일 업데이트 전 연결된 애들 삭제
        profileDeviceUseCase.deleteByProfile(prev);
        profileCertificateUseCase.deleteByProfile(prev);
        profileBundleUseCase.deleteByProfile(prev);
        // 새 프로파일 정보로 업데이트
        updateProfile(prev, data);
        // 연관 관계 다시 맺어주자
        JsonObject newRelationships = data.getAsJsonObject("relationships");
        JsonObject bundleData = newRelationships.getAsJsonObject("bundleId").getAsJsonObject("data");
        // bundle update
        String newBundleId = bundleData.get("id").toString().replaceAll("\"", "");
        profileBundleUseCase.saveUpdatedResult(prev, newBundleId);
        // certificate update
        JsonArray newCertificates = newRelationships.getAsJsonObject("certificates").getAsJsonArray("data");
        List<String> certificateIds = new ArrayList<>();
        for (JsonElement newCertificate : newCertificates) {
            String id = newCertificate.getAsJsonObject().get("id").toString().replaceAll("\"", "");
            certificateIds.add(id);
        }
        profileCertificateUseCase.saveUpdatedResult(prev, certificateIds);
        // device update
        if (!editRequestDto.getType().equals(ProfileType.IOS_APP_STORE.name())) {
            JsonArray newDevices = newRelationships.getAsJsonObject("devices").getAsJsonArray("data");
            List<String> deviceIds = new ArrayList<>();
            for (JsonElement newDevice : newDevices) {
                String id = newDevice.getAsJsonObject().get("id").toString().replaceAll("\"", "");
                deviceIds.add(id);
            }
            profileDeviceUseCase.saveUpdatedResult(prev, deviceIds);
        }
        // update
        saveProfile(prev);
        // 기존꺼 삭제
        appleApi.deleteProfile(editRequestDto.getProfileId());
        // log
        log.info("new id {}", prev.getProfileId());
        log.info("new content {}", prev.getProfileContent());
        // 새거 반환
        return prev;
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
        if (!profile.getProfileType().equals(ProfileType.IOS_APP_STORE)) // 운영 제외하고 기기 등록 전부 가능
            deviceShowList = profileDeviceUseCase.getDeviceForEdit(profileId);
        List<ProfileCertificateShowDto> profileCertificateList = profileCertificateUseCase.getProfileCertificateForEdit(profileId);
        return ProfileEditShowDto.builder().name(profile.getName())
                .expires(profile.getExpirationDate())
                .type(profile.getProfileType().name())
                .status(profile.getProfileState().getValue())
                .certificates(profileCertificateList)
                .bundles(bundleList)
                .devices(deviceShowList)
                .profileId(profileId)
                .build();
    }

    @Override
    public List<Profile> findByNameLike(String name) {
        return profileRepositoryPort.findByNameLike(name);
    }



}
