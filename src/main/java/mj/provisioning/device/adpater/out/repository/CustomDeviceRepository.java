package mj.provisioning.device.adpater.out.repository;

import mj.provisioning.device.domain.Device;

import java.util.List;

public interface CustomDeviceRepository {
    List<Device> findByNames(List<String> names);
}
