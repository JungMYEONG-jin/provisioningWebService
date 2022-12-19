package mj.provisioning.profilecertificate.adapter.out.repository;

import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilecertificate.domain.ProfileCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileCertificateRepository extends JpaRepository<ProfileCertificate, Long> {
    boolean existsByCertificateIdAndProfile(String certificateId, Profile profile);
    Long deleteByProfile_ProfileId(String profileId);
    Long deleteByProfile(Profile profile);
    List<ProfileCertificate> findByProfile_ProfileId(String profileId);
    List<ProfileCertificate> findByProfile(Profile profile);
}
