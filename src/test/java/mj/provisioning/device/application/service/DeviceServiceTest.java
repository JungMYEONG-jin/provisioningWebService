package mj.provisioning.device.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DeviceServiceTest {

    @Autowired
    DeviceService deviceService;

    @Test
    void saveTest() {
        deviceService.saveDevices();
    }

    @Test
    void getDeviceForDeleteTest() {
        deviceService.disableDevicesFromExcel("src/main/resources/static/sample.json");
    }

    @Test
    void registerDeviceTest() {
        deviceService.registerDevicesFromJson("src/main/resources/static/register.json");
    }


}