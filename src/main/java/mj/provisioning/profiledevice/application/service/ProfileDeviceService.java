package mj.provisioning.profiledevice.application.service;

import com.google.gson.*;
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
        profileDeviceRepositoryPort.deleteByProfile(profile);
        List<ProfileDevice> profileDevices = new ArrayList<>();

        if (profile.getProfileType().equals(ProfileType.IOS_APP_DEVELOPMENT)) {
            String response = appleApi.getDeviceInfoFromProfile(profileId);

            JsonParser parser = new JsonParser();
            JsonObject parse = parser.parse(response).getAsJsonObject();
            JsonArray data = parse.getAsJsonArray("data");
            if (data!=null) {
                for (JsonElement datum : data) {
                    JsonObject dd = datum.getAsJsonObject();
                    String deviceId = dd.get("id").toString().replaceAll("\"","");
                    String type = dd.get("type").toString().replaceAll("\"","");
                    ProfileDevice profileDevice = ProfileDevice.builder().deviceId(deviceId)
                            .type(type)
                            .build();
                    profileDevices.add(profileDevice);
                }
                profile.insertAll(profileDevices);
                profileDeviceRepositoryPort.saveAll(profileDevices);
            }
        }
    }

    /**
     * 현재 등록된 디바이스 목록을 json array 반환
     * @param profileId
     * @return
     */
    @Override
    public JsonArray getDeviceJson(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileId(profileId).orElseThrow(()-> new RuntimeException("존재하지 않는 프로비저닝입니다."));
        List<ProfileDevice> devices = profileDeviceRepositoryPort.findByProfile(profile);
        JsonArray jsonArray = new JsonArray();
        devices.forEach(profileDevice -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", profileDevice.getDeviceId());
            obj.addProperty("type", profileDevice.getType());
            jsonArray.add(obj);
        });
        return jsonArray;
    }

    @Override
    public JsonObject getDeviceForUpdateProfile(String profileId) {
        JsonArray deviceJson = getDeviceJson(profileId);
        JsonObject device = new JsonObject();
        device.add("data", deviceJson);
        return device;
    }


}

