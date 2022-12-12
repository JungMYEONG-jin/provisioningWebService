package mj.provisioning.device.application.port.in;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DeviceSearchDto {
    private String name;
    private String udId;
}
