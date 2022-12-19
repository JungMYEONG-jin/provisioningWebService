package mj.provisioning.profiledevice.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profiledevice.application.port.out.ProfileDeviceRepositoryPort;
import mj.provisioning.profiledevice.domain.ProfileDevice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ProfileDeviceRepositoryAdapter implements ProfileDeviceRepositoryPort {

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
    public Long deleteByProfile(Profile profileId) {
        return profileDeviceRepository.deleteByProfile(profileId);
    }

    @Override
    public boolean isExist(String deviceId, Profile profile) {
        return profileDeviceRepository.existsByDeviceIdAndProfile(deviceId, profile);
    }
}
