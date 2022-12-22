package mj.provisioning.profile.application.port.in;

import com.google.gson.JsonObject;
import mj.provisioning.profile.domain.Profile;

import java.util.List;

public interface ProfileUseCase {
    void saveProfiles();
    void saveProfile(Profile profile);
    void updateProfile(Profile profile, JsonObject param);
    void deleteProfile(String profileId);
    List<ProfileShowDto> searchByCondition(ProfileSearchCondition condition);
    List<Profile> findAll();
    Profile getProfile(String profileId);
    ProfileEditShowDto getEditShow(String profileId);
    List<Profile> findByNameLike(String name);
}
