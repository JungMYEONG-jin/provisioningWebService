package mj.provisioning.profilebundle.application.port.in;

import com.google.gson.JsonObject;

public interface ProfileBundleUseCase {
    void saveProfileBundles(String profileId);
    ProfileBundleShowListDto getBundleList(String profileId);
    JsonObject getProfileBundleForUpdate(String profileId);
}
