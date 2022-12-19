package mj.provisioning.profiledevice.application.port.in;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mj.provisioning.device.application.port.in.DeviceShowListDto;

public interface ProfileDeviceUseCase {
    void saveProfileDevice(String profileId);
    JsonArray getDeviceJson(String profileId);
    JsonObject getDeviceForUpdateProfile(String profileId);
    DeviceShowListDto getDeviceShowList(String profileId);
}
