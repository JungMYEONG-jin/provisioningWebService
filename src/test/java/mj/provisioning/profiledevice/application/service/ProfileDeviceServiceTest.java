package mj.provisioning.profiledevice.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProfileDeviceServiceTest {

    @Autowired
    ProfileDeviceUseCase profileDeviceUseCase;
    @Autowired
    ProfileUseCase profileUseCase;
    @Test
    void insertTest() {
        List<Profile> all = profileUseCase.findAll();
        all.stream().forEach(profile -> profileDeviceUseCase.saveProfileDevice(profile.getProfileId()));
    }

    @Test
    void getDeviceInfo() {
        String profile = "2BV6CUSYMK";
        JsonArray deviceJson = profileDeviceUseCase.getDeviceJson(profile);
        System.out.println("deviceJson = " + deviceJson);
        System.out.println("deviceJson = " + deviceJson.size());
    }

    @Test
    void getDeviceForUpdate() {
        String profile = "2BV6CUSYMK";
        JsonObject deviceForUpdateProfile = profileDeviceUseCase.getDeviceForUpdateProfile(profile);
        System.out.println("deviceForUpdateProfile = " + deviceForUpdateProfile);
    }
}