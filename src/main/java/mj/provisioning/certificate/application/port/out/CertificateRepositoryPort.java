package mj.provisioning.certificate.application.port.out;

import mj.provisioning.certificate.domain.Certificate;

import java.util.List;

public interface CertificateRepositoryPort {
    void deleteAll();
    List<Certificate> saveAll();
    Certificate save();
    void deleteByCertificateId(String certificateId);
}
