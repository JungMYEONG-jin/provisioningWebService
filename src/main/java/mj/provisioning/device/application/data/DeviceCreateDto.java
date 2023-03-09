package mj.provisioning.device.application.data;

import lombok.Builder;
import lombok.Data;

/**
 * name - 등록할 이름
 * udid - 기기번호
 * platform - 플랫폼 종류 IOS, MAC_OS
 */
@Data
public class DeviceCreateDto {
    private String name;
    private String udid;
    private String platform;

    @Builder
    public DeviceCreateDto(String name, String udid, String platform) {
        this.name = name;
        this.udid = udid;
        this.platform = platform;
    }
}
