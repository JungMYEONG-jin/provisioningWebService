package mj.provisioning.device.adpater.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.device.application.port.in.DeviceDto;
import mj.provisioning.device.application.port.out.DeviceRepositoryPort;
import mj.provisioning.device.domain.Device;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DeviceRepositoryAdapter implements DeviceRepositoryPort {

    private final DeviceRepository deviceRepository;

    @Override
    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    @Override
    public List<Device> saveAll(List<Device> devices) {
//        List<Device> notContained = devices.stream().filter(device -> !deviceRepository.existsByUdId(device.getUdId())).collect(Collectors.toList());
        return deviceRepository.saveAll(devices);
    }

    @Override
    public Optional<Device> findById(Long id) {
        return deviceRepository.findById(id);
    }

    @Override
    public Optional<Device> findByUdId(String udId) {
        return deviceRepository.findByUdId(udId);
    }

    @Override
    public Optional<Device> findByName(String name) {
        return deviceRepository.findByName(name);
    }

    @Override
    public List<Device> findByNames(List<String> names) {
        return deviceRepository.findByNames(names);
    }

    @Override
    public List<Device> findByUdIds(List<String> udids) {
        return deviceRepository.findByUdIds(udids);
    }

    @Override
    public List<Device> findByIds(List<String> ids) {
        return deviceRepository.findByDeviceIds(ids);
    }

    @Override
    public List<Device> findByClass(String deviceClass) {
        return deviceRepository.findByDeviceClass(deviceClass);
    }

    @Override
    public Optional<List<Device>> findAll() {
        return Optional.of(deviceRepository.findAll());
    }

    @Override
    public void deleteAll() {
        deviceRepository.deleteAll();
    }
}
