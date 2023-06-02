package mj.provisioning.profiledevice.application.port.out;

import mj.provisioning.profiledevice.domain.ProfileDevice;

import java.util.List;

public interface ProfileDeviceSavePort {
    List<ProfileDevice> saveAll(List<ProfileDevice> profileDeviceList);
}
