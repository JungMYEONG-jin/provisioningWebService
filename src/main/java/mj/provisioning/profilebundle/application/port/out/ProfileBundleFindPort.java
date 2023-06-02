package mj.provisioning.profilebundle.application.port.out;

import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilebundle.domain.ProfileBundle;

public interface ProfileBundleFindPort {
    ProfileBundle findByProfileId(String profileId);
    boolean isExist(String bundleId, Profile profile);
}
