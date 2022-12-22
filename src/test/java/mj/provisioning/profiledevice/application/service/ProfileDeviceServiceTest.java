package mj.provisioning.profiledevice.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mj.provisioning.device.application.port.in.DeviceShowDto;
import mj.provisioning.device.application.port.in.DeviceShowListDto;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
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
    void saveTest() {
        profileDeviceUseCase.saveProfileDevice("CVKHMHSU56");
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

    @Test
    void getCurrentProfileDeviceList() {
        String profile = "2BV6CUSYMK";
        DeviceShowListDto deviceShowList = profileDeviceUseCase.getDeviceShowList(profile);
        List<DeviceShowDto> deviceData = deviceShowList.getDeviceData();
        int cnt = 0;
        for (DeviceShowDto deviceDatum : deviceData) {
            System.out.println("deviceDatum = " + deviceDatum);
            if (deviceDatum.isSelected())
                cnt++;
        }
        System.out.println("cnt = " + cnt);
    }
}