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
    private String NAME;
    private String IDENTIFIER;
    private String PLATFORM;

    @Builder
    public DeviceCreateDto(String NAME, String IDENTIFIER, String PLATFORM) {
        this.NAME = NAME;
        this.IDENTIFIER = IDENTIFIER;
        this.PLATFORM = PLATFORM;
    }
}
