package mj.provisioning.profilecertificate.application.service;

import com.google.gson.JsonObject;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateShowDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateShowListDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProfileCertificateServiceTest {

    @Autowired
    ProfileCertificateUseCase profileCertificateUseCase;

    @Test
    void getCertificateTest() {
        JsonObject profileCertificatesForUpdate = profileCertificateUseCase.getProfileCertificatesForUpdate("2BV6CUSYMK");
        System.out.println("profileCertificatesForUpdate = " + profileCertificatesForUpdate);
    }

    @Test
    void getProfileCertificateList() {
        ProfileCertificateShowListDto profileCertificateList = profileCertificateUseCase.getProfileCertificateList("2BV6CUSYMK");
        List<ProfileCertificateShowDto> data = profileCertificateList.getCertificateData();
        for (ProfileCertificateShowDto datum : data) {
            System.out.println("datum = " + datum);
        }
    }

    @Test
    void getProfileCertificateForEditTest() {
        List<ProfileCertificateShowDto> data = profileCertificateUseCase.getProfileCertificateForEdit("2BV6CUSYMK");
        for (ProfileCertificateShowDto datum : data) {
            System.out.println("datum = " + datum);
        }
    }
}