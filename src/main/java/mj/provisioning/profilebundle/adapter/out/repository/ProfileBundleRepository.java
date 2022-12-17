package mj.provisioning.profilebundle.adapter.out.repository;

import mj.provisioning.profilebundle.domain.ProfileBundle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileBundleRepository extends JpaRepository<ProfileBundle, Long> {
    Long deleteByProfile_ProfileId(String profileId);
    Optional<ProfileBundle> findByProfile_ProfileId(String profileId);
}
