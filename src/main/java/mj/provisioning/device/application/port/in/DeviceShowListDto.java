package mj.provisioning.device.application.port.in;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DeviceShowListDto {
    private List<DeviceShowDto> deviceData = new ArrayList<>();
}
