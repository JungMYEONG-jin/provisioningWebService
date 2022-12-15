package mj.provisioning.certificate.adapter.out.repository;

import mj.provisioning.certificate.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    void deleteAllByCertificateId(String certificateId);
}
