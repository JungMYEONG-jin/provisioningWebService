package mj.provisioning.device.application.port.in;

import lombok.*;
import mj.provisioning.device.domain.Device;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DeviceShowDto {
    private String name;
    private String udId;
    private boolean isSelected;
    public static DeviceShowDto of(Device device, boolean isSelected){
        return DeviceShowDto.builder()
                .name(device.getName())
                .udId(device.getUdId())
                .isSelected(isSelected)
                .build();
    }
}
