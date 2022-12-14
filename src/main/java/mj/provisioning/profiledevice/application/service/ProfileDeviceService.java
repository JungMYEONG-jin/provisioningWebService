package mj.provisioning.profiledevice.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profile.domain.ProfileType;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import mj.provisioning.profiledevice.application.port.out.ProfileDeviceRepositoryPort;
import mj.provisioning.profiledevice.domain.ProfileDevice;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileDeviceService implements ProfileDeviceUseCase {

    private final ProfileDeviceRepositoryPort profileDeviceRepositoryPort;
    private final AppleApi appleApi;
    private final ProfileRepositoryPort profileRepositoryPort;

    @Override
    public void saveProfileDevice(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileId(profileId).orElseThrow(()-> new RuntimeException("존재하지 않는 프로비저닝입니다."));
        // 기존 삭제
        profileDeviceRepositoryPort.deleteByProfileId(profileId);
        List<ProfileDevice> profileDevices = new ArrayList<>();
        if (profile.getProfileType().equals(ProfileType.IOS_APP_DEVELOPMENT)) {
            JsonArray deviceInfoFromProfile = appleApi.getDeviceInfoFromProfile(profileId);
            for (JsonElement jsonElement : deviceInfoFromProfile) {
                JsonObject asJsonObject = jsonElement.getAsJsonObject();
                System.out.println("jsonElement = " + asJsonObject.toString());
                ProfileDevice profileDevice = ProfileDevice.builder().deviceId(asJsonObject.get("id").toString().replaceAll("\"", ""))
                        .type(asJsonObject.get("type").toString().replaceAll("\"", "")).build();
                profileDevices.add(profileDevice);
            }
        }
        profile.insertAll(profileDevices);
        profileDeviceRepositoryPort.saveAll(profileDevices);
    }

}

