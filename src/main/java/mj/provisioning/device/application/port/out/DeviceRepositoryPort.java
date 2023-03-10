package mj.provisioning.device.application.port.out;

import mj.provisioning.device.domain.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepositoryPort {
    Device save(Device device);
    List<Device> saveAll(List<Device> devices);
    Optional<Device> findById(Long id);
    Optional<Device> findByUdId(String udId);
    Optional<Device> findByName(String name);
    List<Device> findByNames(List<String> names);
    List<Device> findByUdIds(List<String> udids);
    List<Device> findByIds(List<String> ids);
    List<Device> findByClass(String deviceClass);
    Optional<List<Device>> findAll();
    void deleteAll();
}
