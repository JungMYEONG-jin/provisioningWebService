package mj.provisioning.device.application.data;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceJsonDto {
    private List<DeviceEditDto> devices;
}
