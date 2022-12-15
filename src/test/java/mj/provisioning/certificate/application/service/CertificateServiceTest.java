package mj.provisioning.certificate.application.service;

import mj.provisioning.certificate.application.port.in.CertificateShowListDto;
import mj.provisioning.certificate.application.port.in.CertificateUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CertificateServiceTest {

    @Autowired
    CertificateUseCase certificateUseCase;

    @Test
    void saveTest() {
        certificateUseCase.saveCertificates();
    }

    @Test
    void findAllTest() {
        CertificateShowListDto all = certificateUseCase.findAll();
        System.out.println("all = " + all);
    }
}