package mj.provisioning.certificate.application.port.in;

import mj.provisioning.certificate.domain.Certificate;

import java.util.List;

public interface CertificateUseCase {
    void saveCertificates();
    void saveCertificate(Certificate certificate);
    void updateCertificates(String certificateId);
    void deleteCertificate(String certificateId);
    CertificateShowListDto findAll();
}
