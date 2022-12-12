package mj.provisioning.profile.adapter.out.repository;

import mj.provisioning.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
