package mj.provisioning.device.application.data;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceEditDto {
    private String NAME;
    private String IDENTIFIER;
}
