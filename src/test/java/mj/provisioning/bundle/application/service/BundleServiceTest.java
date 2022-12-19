package mj.provisioning.bundle.application.service;

import mj.provisioning.bundle.application.port.in.BundleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BundleServiceTest {

    @Autowired
    BundleUseCase bundleUseCase;

    @Test
    void saveTest() {
        bundleUseCase.saveBundles();
    }
}