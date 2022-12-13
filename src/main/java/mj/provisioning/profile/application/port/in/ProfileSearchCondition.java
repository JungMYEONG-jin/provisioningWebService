package mj.provisioning.profile.application.port.in;

import lombok.Getter;
import lombok.Setter;

/**
 * profile 검색 조건
 */
@Getter
@Setter
public class ProfileSearchCondition {
    private String name;
    private String profilePlatform;
    private String profileType;
}
