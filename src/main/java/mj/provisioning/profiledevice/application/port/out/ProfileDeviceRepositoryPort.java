package mj.provisioning.profiledevice.application.port.out;

import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profiledevice.domain.ProfileDevice;

import java.util.List;

public interface ProfileDeviceRepositoryPort {
    List<ProfileDevice> saveAll(List<ProfileDevice> profileDeviceList);
    List<ProfileDevice> findByProfile(Profile profile);
    Long deleteByProfile(Profile profile);
//    List<ProfileDevice> findByProfile(String profileId);
//    Long deleteByProfile(String profileId);
}
