package mj.provisioning.profilebundle.application.port.out;

import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilebundle.domain.ProfileBundle;

public interface ProfileBundleDeletePort {
    Long deleteByProfileId(String profileId);
    void deleteByProfile(Profile profile);
}
