package mj.provisioning.device.application.port.in;

import lombok.*;
import mj.provisioning.device.domain.Device;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class DeviceShowDto {
    private Long key;
    private String name;
    private String deviceId;
    private boolean isSelected;
    public static DeviceShowDto of(Device device, boolean isSelected, Long key){
        return DeviceShowDto.builder()
                .name(device.getName())
                .deviceId(device.getDeviceId())
                .isSelected(isSelected)
                .key(key)
                .build();
    }
}
