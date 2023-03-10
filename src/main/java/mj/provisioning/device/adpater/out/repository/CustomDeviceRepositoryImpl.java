package mj.provisioning.device.adpater.out.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mj.provisioning.device.domain.Device;
import org.springframework.stereotype.Repository;

import java.util.List;

import static mj.provisioning.device.domain.QDevice.device;

@Repository
@RequiredArgsConstructor
public class CustomDeviceRepositoryImpl implements CustomDeviceRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Device> findByNames(List<String> names) {
        return jpaQueryFactory.selectFrom(device)
                .where(device.name.in(names))
                .fetch();
    }

    @Override
    public List<Device> findByDeviceClass(String deviceClass) {
        return jpaQueryFactory.selectFrom(device)
                .where(device.deviceClass.eq(deviceClass))
                .fetch();
    }

    @Override
    public List<Device> findByDeviceIds(List<String> deviceIds) {
        return jpaQueryFactory.selectFrom(device)
                .where(device.deviceId.in(deviceIds))
                .fetch();
    }

    @Override
    public List<Device> findByUdIds(List<String> udIds) {
        return jpaQueryFactory.selectFrom(device)
                .where(device.udId.in(udIds))
                .fetch();
    }

}
