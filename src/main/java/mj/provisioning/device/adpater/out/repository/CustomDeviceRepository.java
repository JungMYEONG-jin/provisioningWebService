package mj.provisioning.device.adpater.out.repository;

import mj.provisioning.device.domain.Device;

import java.util.List;

public interface CustomDeviceRepository {
    List<Device> findByNames(List<String> names);
    List<Device> findByDeviceClass(String deviceClass);
    List<Device> findByDeviceIds(List<String> deviceIds);
    List<Device> findByUdIds(List<String> udIds);
}
