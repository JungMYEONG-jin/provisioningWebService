package mj.provisioning.certificate.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.certificate.application.port.out.CertificateRepositoryPort;
import mj.provisioning.certificate.domain.Certificate;
import mj.provisioning.certificate.domain.CertificateType;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<List<Certificate>> findByCertificateType(CertificateType certificateType) {
        return certificateRepository.findAllByCertificateType(certificateType);
    }
}
