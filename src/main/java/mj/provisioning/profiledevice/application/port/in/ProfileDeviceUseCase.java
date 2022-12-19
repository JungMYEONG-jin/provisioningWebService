package mj.provisioning.profiledevice.application.port.in;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface ProfileDeviceUseCase {
    void saveProfileDevice(String profileId);
    JsonArray getDeviceJson(String profileId);
    JsonObject getDeviceForUpdateProfile(String profileId);
}
