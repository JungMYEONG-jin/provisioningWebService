package mj.provisioning.certificate.application.service;

import mj.provisioning.certificate.adapter.out.repository.CertificateRepository;
import mj.provisioning.certificate.application.port.in.CertificateShowListDto;
import mj.provisioning.certificate.application.port.in.CertificateUseCase;
import mj.provisioning.certificate.domain.Certificate;
import mj.provisioning.certificate.domain.CertificateType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CertificateServiceTest {

    @Autowired
    CertificateUseCase certificateUseCase;
    @Autowired
    CertificateRepository certificateRepository;

    @Test
    void saveTest() {
        certificateUseCase.saveCertificates();
    }

    @Test
    void findAllTest() {
        CertificateShowListDto all = certificateUseCase.findAll();
        System.out.println("all = " + all);
    }

    @Test
    void getListByType() {
        List<Certificate> allByCertificateType = certificateRepository.findAllByCertificateType(CertificateType.DEVELOPMENT).orElseThrow(()->new RuntimeException("해당 타입에 매치되는 인증서가 없습니다."));
        for (Certificate certificate : allByCertificateType) {
            System.out.println("certificate = " + certificate);
        }
    }
}