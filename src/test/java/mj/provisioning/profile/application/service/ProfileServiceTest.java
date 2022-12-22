package mj.provisioning.profile.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mj.provisioning.profile.adapter.out.repository.ProfileRepository;
import mj.provisioning.profile.application.port.in.ProfileEditRequestDto;
import mj.provisioning.profile.application.port.in.ProfileEditShowDto;
import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profile.domain.ProfileType;
import mj.provisioning.profilebundle.application.service.ProfileBundleService;
import mj.provisioning.profilecertificate.application.service.ProfileCertificateService;
import mj.provisioning.profiledevice.application.service.ProfileDeviceService;
import mj.provisioning.util.apple.AppleApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProfileServiceTest {

    @Autowired
    ProfileService profileService;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    ProfileCertificateService profileCertificateService;
    @Autowired
    ProfileDeviceService profileDeviceService;
    @Autowired
    ProfileBundleService profileBundleService;
    @Autowired
    AppleApi appleApi;

    @Test
    void getProfilesTest() {
        profileService.saveProfiles();
    }

    @Test
    void allConditionTest() {
        ProfileSearchCondition a = new ProfileSearchCondition();
        List<ProfileShowDto> profileShowDtos = profileService.searchByCondition(a);
        List<Profile> all = profileRepository.findAll();
        assertThat(profileShowDtos.size()).isEqualTo(all.size());
    }

    @Test
    void searchByNameTest() {
        ProfileSearchCondition a = new ProfileSearchCondition();
        a.setName("sbiz");
        List<ProfileShowDto> profileShowDtos = profileService.searchByCondition(a);
        assertThat(profileShowDtos.size()).isEqualTo(5);
    }

    @Test
    void searchByPlatformTest() {
        ProfileSearchCondition a = new ProfileSearchCondition();
        a.setProfilePlatform("IOS");
        List<ProfileShowDto> profileShowDtos = profileService.searchByCondition(a);
        assertThat(profileShowDtos.size()).isEqualTo(112);
    }

    @Test
    void searchByTypeTest() {
        ProfileSearchCondition a = new ProfileSearchCondition();
        a.setProfileType("IOS_APP_STORE");
        List<ProfileShowDto> profileShowDtos = profileService.searchByCondition(a);
        assertThat(profileShowDtos.size()).isEqualTo(50);
    }

    @Test
    void searchByNameAndTypeTest() {
        ProfileSearchCondition a = new ProfileSearchCondition();
        a.setProfileType("IOS_APP_DEVELOPMENT");
        a.setName("sbiz");
        List<ProfileShowDto> profileShowDtos = profileService.searchByCondition(a);
        assertThat(profileShowDtos.size()).isEqualTo(2);
    }

    @Test
    void deleteTest() {
        Profile sol_development_widget_test = profileRepository.findByName("sol_development_widget_test_2023231").orElseThrow(() -> new RuntimeException("일차하는 프로비저닝이 없습니다."));
        profileService.deleteProfile(sol_development_widget_test.getProfileId());
    }

    @Test
    void dataFormatting() {
        List<Profile> all = profileService.findAll();
        Profile profile = all.get(0);
        String expirationDate = profile.getExpirationDate().replaceAll("[^0-9]", "");
        System.out.println("expirationDate = " + expirationDate);
    }

    @Test
    void getEditTest() {
        //64TC4LQ99S
        //CVKHMHSU56 test
        ProfileEditShowDto editShow = profileService.getEditShow("CVKHMHSU56");
        System.out.println("editShow = " + editShow);
        ProfileEditRequestDto editRequestDto = new ProfileEditRequestDto();
        editRequestDto.setProfileId(editShow.getProfileId());
        editRequestDto.setBundleData(editShow.getBundle());
        editRequestDto.setDeviceData(editShow.getDevices());
        editRequestDto.setCertificateData(editShow.getCertificates());

        JsonObject profileCertificatesForUpdate = profileCertificateService.getProfileCertificatesForUpdate(editRequestDto.getCertificateData());
        System.out.println("profileCertificatesForUpdate = " + profileCertificatesForUpdate);
        JsonObject profileBundleForUpdate = profileBundleService.getProfileBundleForUpdate(editRequestDto.getBundleData());
        System.out.println("profileBundleForUpdate = " + profileBundleForUpdate);
        JsonObject deviceForUpdateProfile = profileDeviceService.getDeviceForUpdateProfile(editRequestDto.getDeviceData());
        System.out.println("deviceForUpdateProfile = " + deviceForUpdateProfile);
    }

    @Commit
    @Test
    void doEditTest() throws IOException {
        ProfileEditShowDto editShow = profileService.getEditShow("KAQZ2HT9YT");
        Profile prev = profileService.getProfile(editShow.getProfileId());
        ProfileEditRequestDto editRequestDto = new ProfileEditRequestDto();
        editRequestDto.setName("test_for_prov_temp_why_not");
        editRequestDto.setProfileId(editShow.getProfileId());
        editRequestDto.setBundleData(editShow.getBundle());
        editRequestDto.setDeviceData(editShow.getDevices());
        editRequestDto.setCertificateData(editShow.getCertificates());
        editRequestDto.setType(editShow.getType());

        JsonObject attr = new JsonObject();
        attr.addProperty("name", editRequestDto.getName());
        attr.addProperty("profileType", editRequestDto.getType()); // dist, dev
        JsonObject relationships = new JsonObject();

        JsonObject profileCertificatesForUpdate = profileCertificateService.getProfileCertificatesForUpdate(editRequestDto.getCertificateData());
        JsonObject profileBundleForUpdate = profileBundleService.getProfileBundleForUpdate(editRequestDto.getBundleData());
        JsonObject deviceForUpdateProfile = null;
        if (editRequestDto.getType().equals(ProfileType.IOS_APP_DEVELOPMENT.getValue()) || editRequestDto.getType().equals(ProfileType.IOS_APP_DEVELOPMENT.name())) {
            deviceForUpdateProfile = profileDeviceService.getDeviceForUpdateProfile(editRequestDto.getDeviceData());
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
        profileDeviceService.deleteByProfile(prev);
        profileCertificateService.deleteByProfile(prev);
        profileBundleService.deleteByProfile(prev);
        // 새 프로파일 정보로 업데이트
        profileService.updateProfile(prev, data);
        // 연관 관계 다시 맺어주자
        JsonObject newRelationships = data.getAsJsonObject("relationships");
        JsonObject bundleData = newRelationships.getAsJsonObject("bundleId").getAsJsonObject("data");
        // bundle update
        String newBundleId = bundleData.get("id").toString().replaceAll("\"", "");
        profileBundleService.saveUpdatedResult(prev, newBundleId);
        // certificate update
        JsonArray newCertificates = newRelationships.getAsJsonObject("certificates").getAsJsonArray("data");
        List<String> certificateIds = new ArrayList<>();
        for (JsonElement newCertificate : newCertificates) {
            String id = newCertificate.getAsJsonObject().get("id").toString().replaceAll("\"", "");
            certificateIds.add(id);
        }
        profileCertificateService.saveUpdatedResult(prev, certificateIds);
        // device update
        if (editRequestDto.getType().equals(ProfileType.IOS_APP_DEVELOPMENT.getValue()) || editRequestDto.getType().equals(ProfileType.IOS_APP_DEVELOPMENT.name())) {
            JsonArray newDevices = newRelationships.getAsJsonObject("devices").getAsJsonArray("data");
            List<String> deviceIds = new ArrayList<>();
            for (JsonElement newDevice : newDevices) {
                String id = newDevice.getAsJsonObject().get("id").toString().replaceAll("\"", "");
                deviceIds.add(id);
            }
            profileDeviceService.saveUpdatedResult(prev, deviceIds);
        }
        // 기존꺼 삭제
        appleApi.deleteProfile(editRequestDto.getProfileId());
    }


}