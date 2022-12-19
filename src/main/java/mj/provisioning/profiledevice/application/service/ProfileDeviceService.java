package mj.provisioning.profiledevice.application.service;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import mj.provisioning.device.application.port.in.DeviceShowDto;
import mj.provisioning.device.application.port.in.DeviceShowListDto;
import mj.provisioning.device.application.port.out.DeviceRepositoryPort;
import mj.provisioning.device.domain.Device;
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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileDeviceService implements ProfileDeviceUseCase {

    private final ProfileDeviceRepositoryPort profileDeviceRepositoryPort;
    private final AppleApi appleApi;
    private final ProfileRepositoryPort profileRepositoryPort;
    private final DeviceRepositoryPort deviceRepositoryPort;

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


    /**
     * edit 페이지에서 사용자에게 체크박스 제공하기 위한 list
     * @param profileId
     * @return
     */
    @Override
    public DeviceShowListDto getDeviceShowList(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileId(profileId).orElseThrow(()-> new RuntimeException("존재하지 않는 프로비저닝입니다."));
        List<Device> all = deviceRepositoryPort.findAll().orElseThrow(()->new RuntimeException("등록된 디바이스가 존재하지 않습니다."));
        final long[] idx = {0};
        return DeviceShowListDto.builder().deviceData(all.stream().map(device -> {
            return DeviceShowDto.of(device, profileDeviceRepositoryPort.isExist(device.getDeviceId(), profile) ,idx[0]++);
        }).collect(Collectors.toList())).build();
    }

}

