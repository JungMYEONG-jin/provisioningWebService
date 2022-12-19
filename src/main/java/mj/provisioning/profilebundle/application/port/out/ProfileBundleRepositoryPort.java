package mj.provisioning.profilebundle.application.port.out;

import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilebundle.domain.ProfileBundle;

import java.util.List;

public interface ProfileBundleRepositoryPort {
    Long deleteByProfileId(String profileId);
    ProfileBundle findByProfileId(String profileId);
    List<ProfileBundle> getAllProfileBundles();
    ProfileBundle save(ProfileBundle profileBundle);
    boolean isExist(String bundleId, Profile profile);
}