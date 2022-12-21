package mj.provisioning.profilebundle.application.port.in;

import com.google.gson.JsonObject;

import java.util.List;

public interface ProfileBundleUseCase {
    void saveProfileBundles(String profileId);
    ProfileBundleShowListDto getBundleList(String profileId);
    List<ProfileBundleShowDto> getBundleForEdit(String profileId);
    JsonObject getProfileBundleForUpdate(String profileId);
}
