package mj.provisioning.profilecertificate.application.service;

import com.google.gson.JsonObject;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilecertificate.application.data.ProfileCertificateShowDto;
import mj.provisioning.profilecertificate.application.data.ProfileCertificateShowListDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ProfileCertificateServiceTest {

    @Autowired
    ProfileCertificateUseCase profileCertificateUseCase;
    @Autowired
    ProfileUseCase profileUseCase;

    @Test
    void saveTest() {
        profileCertificateUseCase.saveProfileCertificate("CVKHMHSU56");
    }

    @Test
    void saveAllTest() {
        List<Profile> all = profileUseCase.findAll();
        for (Profile profile : all) {
            profileCertificateUseCase.saveProfileCertificate(profile.getProfileId());
        }
    }

    @Test
    void getCertificateTest() {
        JsonObject profileCertificatesForUpdate = profileCertificateUseCase.getProfileCertificatesForUpdate("CVKHMHSU56");
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