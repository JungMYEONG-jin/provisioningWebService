package mj.provisioning.profile.adapter.out.repository;

import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class ProfileRepositoryAdapterTest {

    @Autowired
    ProfileRepositoryPort profileRepositoryPort;

    @Transactional
    @Test
    void fetchTest() {
        Profile byProfileIdFetchJoin = profileRepositoryPort.findByProfileIdFetchJoin("363QUF539S");
        System.out.println("byProfileIdFetchJoin = " + byProfileIdFetchJoin);
        System.out.println("getCertificates = " + byProfileIdFetchJoin.getCertificates().size());
        System.out.println("getDeviceList = " + byProfileIdFetchJoin.getDeviceList().size());
        System.out.println("getProfileBundle = " + byProfileIdFetchJoin.getProfileBundle().toString());
    }
}