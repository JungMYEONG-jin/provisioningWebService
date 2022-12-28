package mj.provisioning.profilebundle.application.port.in;

import com.google.gson.JsonObject;
import mj.provisioning.profile.domain.Profile;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ProfileBundleUseCase {
    void saveProfileBundles(String profileId);
    void deleteByProfile(Profile profile);
    ProfileBundleShowListDto getBundleList(String profileId);
    List<ProfileBundleShowDto> getBundleForEdit(String profileId);
    JsonObject getProfileBundleForUpdate(String profileId);
    JsonObject getProfileBundleForUpdate(List<ProfileBundleShowDto> bundle);
    void saveUpdatedResult(Profile profile, String bundleId);
}
