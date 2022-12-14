package mj.provisioning.profiledevice.application.port.out;

import mj.provisioning.profiledevice.domain.ProfileDevice;

import java.util.List;

public interface ProfileDeviceRepositoryPort {
    List<ProfileDevice> saveAll(List<ProfileDevice> profileDeviceList);
    Long deleteByProfileId(String profileId);
}
