package mj.provisioning.profilecertificate.application.port.in;

public interface ProfileCertificateUseCase {
    void saveProfileCertificate(String profileId);
    ProfileCertificateShowListDto getProfileCertificateList(String profileId);
}
