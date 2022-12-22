package mj.provisioning.profiledevice.adapter.out.repository;

import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profiledevice.domain.ProfileDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfileDeviceRepository extends JpaRepository<ProfileDevice, Long> {
    boolean existsByDeviceIdAndProfile(String deviceId, Profile profile);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM ProfileDevice pd WHERE pd.profile = :profile")
    void deleteByProfile(@Param("profile") Profile profile);
    List<ProfileDevice> findByProfile(Profile profile);
//    Long deleteByProfile_ProfileId(Profile profile);
//    List<ProfileDevice> findByProfile(Profile profile);
}

