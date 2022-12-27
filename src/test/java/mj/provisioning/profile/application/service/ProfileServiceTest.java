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
import mj.provisioning.util.apple.AppleApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static mj.provisioning.util.FileUploadUtils.*;
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

        writeProvisioning(newProfile.getProfileContent(), newProfile.getName(), "/provisioning");
    }

    @Description("SVN에 올린거 실제로 되난 테스트")
    @Test
    void ftpTest() throws IOException, SVNException {
        Profile profile = profileRepository.findByProfileId("CMMGLKT75U").get();
        // checkout example
        // init
        DAVRepositoryFactory.setup();
        SVNURL url = SVNURL.parseURIEncoded("https://10.25.219.102/svn/FILESHARE_REPOSITORY/svntest");
        // credential
        String user = "admin";
        String pw = "1q2w3e4r!!";
        // get repository
        SVNRepository svnRepository = SVNRepositoryFactory.create(url);
        // User 인증 정보는 ISVNAuthenticationManager 통해 제공 된다.
        ISVNAuthenticationManager authenticationManager = SVNWCUtil.createDefaultAuthenticationManager(user, pw);
        // 인증 정보 적용
        svnRepository.setAuthenticationManager(authenticationManager);
        // 해당 url에 지정된 node type 파악
        SVNNodeKind nodeKind = svnRepository.checkPath("", -1);
        // 폴더가 아니면 종료해버리기
        if (nodeKind == SVNNodeKind.NONE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "No entry at URL {0}", url);
            throw new SVNException(err);
        } else if (nodeKind == SVNNodeKind.FILE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "Entry at URL {0} is a file while directory ws expected", url);
            throw new SVNException(err);
        }
        // get latest head version
        long latestRevision = svnRepository.getLatestRevision();
        System.out.println("latestRevision = " + latestRevision);
        // commit 전까지 SVNRepository의 메소드를 invoke 하면 안된다.
        // commit 메세지는 커밋 로그에 남는다.
        // ISVNWorkspaceMediator 는 템프 파일을 저장하기 위해 사용된다. 만약 null이 넘겨지면
        // 기본 임시 폴더가 사용될것이다.
        ISVNEditor editor = svnRepository.getCommitEditor("provisioning test", null);
        // 작업중인 svn url에 폴더나 파일을 add하자.
        // SVNCommitInfo는 기본 커밋 정보를 담는다.
        // revision number, 작성자, 커밋 날짜, 커밋 메세지등
        // 프로비저닝 디코드
        byte[] contents = DatatypeConverter.parseBase64Binary(profile.getProfileContent());
        SVNCommitInfo commitInfo = addFile(editor,profile.getName()+"_test"+".mobileprovision", contents);
        System.out.println("commitInfo = " + commitInfo);
    }
}