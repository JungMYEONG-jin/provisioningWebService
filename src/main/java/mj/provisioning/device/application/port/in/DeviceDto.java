package mj.provisioning.device.application.port.in;

import lombok.*;
import mj.provisioning.device.domain.Device;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DeviceDto {
    private String name;
    public static DeviceDto to(Device device){
        return DeviceDto.builder().name(device.getName()).build();
    }
}
