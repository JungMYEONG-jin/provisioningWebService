package mj.provisioning.profilebundle.application.port.in;

import com.google.gson.JsonObject;
import mj.provisioning.device.application.port.in.DeviceShowDto;

import java.util.List;

public interface ProfileBundleUseCase {
    void saveProfileBundles(String profileId);
    ProfileBundleShowListDto getBundleList(String profileId);
    List<ProfileBundleShowDto> getBundleForEdit(String profileId);
    JsonObject getProfileBundleForUpdate(String profileId);
    JsonObject getProfileBundleForUpdate(List<ProfileBundleShowDto> bundle);
}
