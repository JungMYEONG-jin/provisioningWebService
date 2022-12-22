package mj.provisioning.profiledevice.adapter.out.repository;

import mj.provisioning.profile.adapter.out.repository.ProfileRepository;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProfileDeviceRepositoryTest {

    @Autowired
    ProfileDeviceUseCase profileDeviceUseCase;
    @Autowired
    ProfileRepository profileRepository;
    @Test
    void deleteAllTest() {
        Optional<Profile> byId = profileRepository.findById(115L);
        Profile profile = byId.get();
        profileDeviceUseCase.deleteByProfile(profile);
    }
}