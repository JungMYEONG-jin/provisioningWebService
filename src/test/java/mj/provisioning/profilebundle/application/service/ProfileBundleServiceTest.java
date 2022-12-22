package mj.provisioning.profilebundle.application.service;

import com.google.gson.JsonObject;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleShowDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleShowListDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProfileBundleServiceTest {

    @Autowired
    ProfileBundleUseCase profileBundleUseCase;
    @Autowired
    ProfileRepositoryPort profileRepositoryPort;
    @Commit
    @Test
    void saveTest() {
        List<Profile> test = profileRepositoryPort.findByNameLike("test");
        for (Profile profile : test) {
            profileBundleUseCase.deleteByProfile(profile);
        }
        for (Profile profile : test) {
            profileBundleUseCase.saveProfileBundles(profile.getProfileId());
        }
    }

    @Test
    void getJsonTest() {
        JsonObject profileBundleForUpdate = profileBundleUseCase.getProfileBundleForUpdate("2BV6CUSYMK");
        System.out.println("profileBundleForUpdate = " + profileBundleForUpdate);
    }

    @Test
    void getEditPageList() {
        ProfileBundleShowListDto bundleList = profileBundleUseCase.getBundleList("2BV6CUSYMK");
        List<ProfileBundleShowDto> bundleData = bundleList.getBundleData();
        for (ProfileBundleShowDto bundleDatum : bundleData) {
            System.out.println("bundleDatum = " + bundleDatum);
        }
    }
}