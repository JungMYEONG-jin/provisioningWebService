package mj.provisioning.profilecertificate.application.port.out;

import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilecertificate.domain.ProfileCertificate;

import java.util.List;

public interface ProfileCertificateRepositoryPort {
    List<ProfileCertificate> saveAll(List<ProfileCertificate> profileCertificates);
    Long deleteByProfileId(String profileId);
    Long deleteByProfileId(Profile profile);
    List<ProfileCertificate> findByProfileId(String profileId);
    List<ProfileCertificate> findByProfileId(Profile profile);
    boolean isExist(String certificateId, Profile profile);
}
