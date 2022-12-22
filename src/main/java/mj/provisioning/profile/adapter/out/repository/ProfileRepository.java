package mj.provisioning.profile.adapter.out.repository;

import mj.provisioning.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long>, CustomProfileRepository {
    Optional<Profile> findByName(String name);
    Optional<Profile> findByProfileId(String profileId);
    Long deleteByProfileId(String profileId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Profile p WHERE p.profileId = :profileId")
    void deleteAllByProfileId(String profileId);
    boolean existsByProfileId(String profileId);
}
