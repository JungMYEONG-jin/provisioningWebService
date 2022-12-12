package mj.provisioning.device.adpater.out.repository;

import mj.provisioning.device.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long>, CustomDeviceRepository {
    Optional<Device> findByUdId(String udId);
    Optional<Device> findByName(String name);
    boolean existsByUdId(String udid);
}
