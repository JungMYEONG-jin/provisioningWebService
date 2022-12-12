package mj.provisioning.profile.application.port.in;

import lombok.Getter;
import lombok.Setter;
import mj.provisioning.profile.domain.ProfilePlatform;
import mj.provisioning.profile.domain.ProfileType;

@Getter
@Setter
public class ProfileSearchCondition {
    private String name;
    private ProfilePlatform profilePlatform;
    private ProfileType profileType;
}
