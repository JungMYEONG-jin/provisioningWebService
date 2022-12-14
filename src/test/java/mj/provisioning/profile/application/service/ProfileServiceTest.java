package mj.provisioning.profile.application.service;

import com.google.gson.JsonObject;
import mj.provisioning.profile.adapter.out.repository.ProfileRepository;
import mj.provisioning.profile.application.port.in.ProfileEditRequestDto;
import mj.provisioning.profile.application.port.in.ProfileEditShowDto;
import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilebundle.application.service.ProfileBundleService;
import mj.provisioning.profilecertificate.application.service.ProfileCertificateService;
import mj.provisioning.profiledevice.application.service.ProfileDeviceService;
import mj.provisioning.util.FileUploadUtils;
import mj.provisioning.util.apple.AppleApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.tmatesoft.svn.core.SVNException;

import java.io.IOException;
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
    @Autowired
    FileUploadUtils fileUploadUtils;

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
        Profile sol_development_widget_test = profileRepository.findByName("sol_development_widget_test_2023231").orElseThrow(() -> new RuntimeException("???????????? ?????????????????? ????????????."));
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
        editRequestDto.setBundles(editShow.getBundles());
        editRequestDto.setDevices(editShow.getDevices());
        editRequestDto.setCertificates(editShow.getCertificates());

        JsonObject profileCertificatesForUpdate = profileCertificateService.getProfileCertificatesForUpdate(editRequestDto.getCertificates());
        System.out.println("profileCertificatesForUpdate = " + profileCertificatesForUpdate);
        JsonObject profileBundleForUpdate = profileBundleService.getProfileBundleForUpdate(editRequestDto.getBundles());
        System.out.println("profileBundleForUpdate = " + profileBundleForUpdate);
        JsonObject deviceForUpdateProfile = profileDeviceService.getDeviceForUpdateProfile(editRequestDto.getDevices());
        System.out.println("deviceForUpdateProfile = " + deviceForUpdateProfile);
    }

    @Test
    void doEditTest() throws IOException {
        ProfileEditShowDto editShow = profileService.getEditShow("CHWCG6R845");
        ProfileEditRequestDto editRequestDto = new ProfileEditRequestDto();
        editRequestDto.setName("upload_file_test");
        editRequestDto.setProfileId(editShow.getProfileId());
        editRequestDto.setBundles(editShow.getBundles());
        editRequestDto.setDevices(editShow.getDevices());
        editRequestDto.setCertificates(editShow.getCertificates());
        editRequestDto.setType(editShow.getType());
        Profile newProfile = profileService.editProvisioning(editRequestDto);
        System.out.println("newProfile = " + newProfile.getName());
        System.out.println("newProfile = " + newProfile.getProfileId());
        System.out.println("newProfile = " + newProfile.getProfileContent());
    }

    @Description("SVN??? ????????? ????????? ?????? ?????????")
    @Test
    void ftpTest() throws IOException, SVNException {
        ProfileEditShowDto editShow = profileService.getEditShow("QCH44C9L9U");
        ProfileEditRequestDto editRequestDto = new ProfileEditRequestDto();
        editRequestDto.setName("upload_to_svn_test_componet");
        editRequestDto.setProfileId(editShow.getProfileId());
        editRequestDto.setBundles(editShow.getBundles());
        editRequestDto.setDevices(editShow.getDevices());
        editRequestDto.setCertificates(editShow.getCertificates());
        editRequestDto.setType(editShow.getType());
        Profile newProfile = profileService.editProvisioning(editRequestDto);
        System.out.println("newProfile = " + newProfile.getName());
        System.out.println("newProfile = " + newProfile.getProfileId());
        System.out.println("newProfile = " + newProfile.getProfileContent());
        fileUploadUtils.uploadToSVN("svntest", newProfile.getName(), newProfile.getProfileContent());
    }
}