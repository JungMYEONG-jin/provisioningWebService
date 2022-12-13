package mj.provisioning.profile.adapter.out.repository;

import mj.provisioning.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long>, CustomProfileRepository {
    Optional<Profile> findByName(String name);
    Optional<Profile> findByProfileId(String profileId);
}
