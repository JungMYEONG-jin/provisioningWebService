package mj.provisioning.certificate.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.certificate.application.port.out.CertificateRepositoryPort;
import mj.provisioning.certificate.domain.Certificate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CertificateRepositoryAdapter implements CertificateRepositoryPort {

    private final CertificateRepository certificateRepository;

    @Override
    public void deleteAll() {
        certificateRepository.deleteAll();
    }

    @Override
    public List<Certificate> saveAll(List<Certificate> certificates) {
        return certificateRepository.saveAll(certificates);
    }

    @Override
    public Certificate save(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    @Override
    public void deleteByCertificateId(String certificateId) {
        certificateRepository.deleteAllByCertificateId(certificateId);
    }

    @Override
    public List<Certificate> findAll() {
        return certificateRepository.findAll();
    }
}
