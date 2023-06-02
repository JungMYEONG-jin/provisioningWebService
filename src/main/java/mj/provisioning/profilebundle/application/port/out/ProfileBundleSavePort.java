package mj.provisioning.profilebundle.application.port.out;

import mj.provisioning.profilebundle.domain.ProfileBundle;

public interface ProfileBundleSavePort {
    ProfileBundle save(ProfileBundle profileBundle);
}
