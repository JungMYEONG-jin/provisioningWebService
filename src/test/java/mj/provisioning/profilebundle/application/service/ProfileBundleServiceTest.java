package mj.provisioning.profilebundle.application.service;

import com.google.gson.JsonObject;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProfileBundleServiceTest {

    @Autowired
    ProfileBundleUseCase profileBundleUseCase;

    @Commit
    @Test
    void saveTest() {
        profileBundleUseCase.saveProfileBundles("2BV6CUSYMK");
    }

    @Test
    void getJsonTest() {
        JsonObject profileBundleForUpdate = profileBundleUseCase.getProfileBundleForUpdate("2BV6CUSYMK");
        System.out.println("profileBundleForUpdate = " + profileBundleForUpdate);
    }
}