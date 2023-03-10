package mj.provisioning.device.application.port.in;

import mj.provisioning.device.domain.Device;

public interface DeviceEditCase {
    void disableDevice(Device device);
    void disableDevicesFromExcel(String excelPath);
}
