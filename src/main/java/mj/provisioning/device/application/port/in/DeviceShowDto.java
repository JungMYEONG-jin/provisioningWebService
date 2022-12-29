package mj.provisioning.device.application.port.in;

import lombok.*;
import mj.provisioning.device.domain.Device;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
@ToString
public class DeviceShowDto {
    private Long key;
    private String name;
    private String deviceId;
    private String type;
    private boolean chosen;
    public static DeviceShowDto of(Device device, boolean chosen, Long key){
        return DeviceShowDto.builder()
                .name(device.getName())
                .deviceId(device.getDeviceId())
                .chosen(chosen)
                .key(key)
                .type(device.getType())
                .build();
    }
}
