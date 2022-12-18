package mj.provisioning.profiledevice.application.port.in;

import com.google.gson.JsonArray;

public interface ProfileDeviceUseCase {
    void saveProfileDevice(String profileId);
    JsonArray getDeviceJson(String profileId);
}
