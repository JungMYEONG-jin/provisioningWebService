package mj.provisioning.profiledevice.application.port.out;

import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profiledevice.domain.ProfileDevice;

import java.util.List;

public interface ProfileDeviceFindPort {
    List<ProfileDevice> findByProfile(Profile profile);
    boolean isExist(String deviceId, Profile profile);
}
