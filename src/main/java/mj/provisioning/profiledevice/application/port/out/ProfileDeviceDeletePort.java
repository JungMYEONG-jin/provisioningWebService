package mj.provisioning.profiledevice.application.port.out;

import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profiledevice.domain.ProfileDevice;

import java.util.List;

public interface ProfileDeviceDeletePort {
    void deleteByProfile(Profile profile);
}
