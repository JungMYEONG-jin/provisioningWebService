package mj.provisioning.profiledevice.application.port.in;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mj.provisioning.device.application.port.in.DeviceShowDto;
import mj.provisioning.device.application.port.in.DeviceShowListDto;
import mj.provisioning.profile.domain.Profile;

import java.util.List;

public interface ProfileDeviceUseCase {
    void saveProfileDevice(String profileId);
    void deleteByProfile(Profile profile);
    void saveUpdatedResult(Profile profile, List<String> deviceIds);
    JsonArray getDeviceJson(String profileId);
    JsonObject getDeviceForUpdateProfile(String profileId);
    JsonObject getDeviceForUpdateProfile(List<DeviceShowDto> deviceData);
    DeviceShowListDto getDeviceShowList(String profileId);
    List<DeviceShowDto> getDeviceForEdit(String profileId);
}
