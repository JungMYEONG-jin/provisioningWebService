package mj.provisioning.profiledevice.adapter.out.repository;

import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profiledevice.domain.ProfileDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileDeviceRepository extends JpaRepository<ProfileDevice, Long> {
    boolean existsByDeviceIdAndProfile(String deviceId, Profile profile);
    Long deleteByProfile_ProfileId(String profileId);
}
