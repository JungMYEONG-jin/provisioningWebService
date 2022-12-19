package mj.provisioning.certificate.adapter.out.repository;

import mj.provisioning.certificate.domain.Certificate;
import mj.provisioning.certificate.domain.CertificateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    void deleteAllByCertificateId(String certificateId);
    Optional<List<Certificate>> findAllByCertificateType(CertificateType certificateType);
}
