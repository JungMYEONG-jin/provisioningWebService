package mj.provisioning.profilecertificate.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilecertificate.application.port.out.ProfileCertificateRepositoryPort;
import mj.provisioning.profilecertificate.domain.ProfileCertificate;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ProfileCertificateRepositoryAdapter implements ProfileCertificateRepositoryPort {

    private final ProfileCertificateRepository profileCertificateRepository;

    @Override
    public List<ProfileCertificate> saveAll(List<ProfileCertificate> profileCertificates) {
        return profileCertificateRepository.saveAll(profileCertificates);
    }

    @Override
    public Long deleteByProfileId(String profileId) {
        return profileCertificateRepository.deleteByProfile_ProfileId(profileId);
    }

    @Override
    public List<ProfileCertificate> findByProfileId(String profileId) {
        return profileCertificateRepository.findByProfile_ProfileId(profileId);
    }

    @Override
    public List<ProfileCertificate> findByProfileId(Profile profile) {
        return profileCertificateRepository.findByProfile(profile);
    }

    @Override
    public boolean isExist(String certificateId, Profile profile) {
        return profileCertificateRepository.existsByCertificateIdAndProfile(certificateId, profile);
    }
}
