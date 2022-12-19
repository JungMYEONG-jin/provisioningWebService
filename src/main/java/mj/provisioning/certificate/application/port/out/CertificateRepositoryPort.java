package mj.provisioning.certificate.application.port.out;

import mj.provisioning.certificate.domain.Certificate;
import mj.provisioning.certificate.domain.CertificateType;

import java.util.List;
import java.util.Optional;

public interface CertificateRepositoryPort {
    void deleteAll();
    List<Certificate> saveAll(List<Certificate> certificates);
    Certificate save(Certificate certificate);
    void deleteByCertificateId(String certificateId);
    List<Certificate> findAll();
    Optional<List<Certificate>> findByCertificateType(CertificateType certificateType);
}
