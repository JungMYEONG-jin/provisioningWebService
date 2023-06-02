package mj.provisioning.profiledevice.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profiledevice.application.port.out.ProfileDeviceDeletePort;
import mj.provisioning.profiledevice.application.port.out.ProfileDeviceFindPort;
import mj.provisioning.profiledevice.application.port.out.ProfileDeviceSavePort;
import mj.provisioning.profiledevice.domain.ProfileDevice;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ProfileDeviceRepositoryAdapter implements ProfileDeviceDeletePort, ProfileDeviceSavePort, ProfileDeviceFindPort {

    private final ProfileDeviceRepository profileDeviceRepository;

    @Override
    public List<ProfileDevice> saveAll(List<ProfileDevice> profileDeviceList) {
//        List<ProfileDevice> notContained = profileDeviceList.stream().filter(profileDevice -> !profileDeviceRepository.existsByDeviceIdAndProfile(profileDevice.getDeviceId(), profileDevice.getProfile())).collect(Collectors.toList());
        return profileDeviceRepository.saveAll(profileDeviceList);
    }

    @Override
    public List<ProfileDevice> findByProfile(Profile profileId) {
        return profileDeviceRepository.findByProfile(profileId);
    }

    @Override
    public void deleteByProfile(Profile profile) {
        profileDeviceRepository.deleteByProfile(profile);
    }

    @Override
    public boolean isExist(String deviceId, Profile profile) {
        return profileDeviceRepository.existsByDeviceIdAndProfile(deviceId, profile);
    }
}
