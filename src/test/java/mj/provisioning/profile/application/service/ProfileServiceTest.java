package mj.provisioning.profile.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProfileServiceTest {

    @Autowired
    ProfileService profileService;

    @Test
    void getProfilesTest() {
        profileService.saveProfiles();
    }
}