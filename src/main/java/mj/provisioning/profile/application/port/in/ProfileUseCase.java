package mj.provisioning.profile.application.port.in;

import mj.provisioning.profile.domain.Profile;

import java.util.List;

public interface ProfileUseCase {
    void saveProfiles();
    void saveProfile(Profile profile);
    void updateProfile(String profileId);
    void deleteProfile(String profileId);
    List<ProfileShowDto> searchByCondition(ProfileSearchCondition condition);
    List<Profile> findAll();
    Profile getProfile(String profileId);
    ProfileEditShowDto getEditShow(String profileId);
}
