package mj.provisioning.profilecertificate.application.port.out;

import mj.provisioning.profilecertificate.domain.ProfileCertificate;

import java.util.List;

public interface ProfileCertificateRepositoryPort {
    List<ProfileCertificate> saveAll(List<ProfileCertificate> profileCertificates);
    Long deleteByProfileId(String profileId);
}
