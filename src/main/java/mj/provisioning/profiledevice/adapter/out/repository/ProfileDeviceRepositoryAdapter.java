package mj.provisioning.profiledevice.adapter.out.repository;

import lombok.RequiredArgsConstructor;
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
        List<ProfileDevice> notContained = profileDeviceList.stream().filter(profileDevice -> !profileDeviceRepository.existsByDeviceIdAndProfile(profileDevice.getDeviceId(), profileDevice.getProfile())).collect(Collectors.toList());
        return profileDeviceRepository.saveAll(notContained);
    }
}
