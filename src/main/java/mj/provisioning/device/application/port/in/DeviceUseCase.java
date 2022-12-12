package mj.provisioning.device.application.port.in;

import java.util.List;

public interface DeviceUseCase {
    void saveDevices();
    List<DeviceDto> getAllDeviceList();
}
