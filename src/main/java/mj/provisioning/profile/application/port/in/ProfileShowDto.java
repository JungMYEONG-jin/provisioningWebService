package mj.provisioning.profile.application.port.in;

import lombok.*;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profile.domain.ProfilePlatform;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfileShowDto {
    private String name;
    private String platform;
    private String profileType;
    private String expirationDate;

    public static ProfileShowDto of(Profile profile){
        return ProfileShowDto.builder().expirationDate(profile.getExpirationDate())
                .name(profile.getName())
                .platform(profile.getPlatform().name())
                .profileType(profile.getProfileType().name()).build();
    }
}

//Devices
//Profiles
//Keys
//Services
//Profiles
//Search
//All Types
//All Platforms
//Edit
//NAME	PLATFORM	TYPE	EXPIRATION