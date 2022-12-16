package mj.provisioning.profile.application.port.in;

import lombok.*;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profile.domain.ProfilePlatform;
import mj.provisioning.profile.domain.ProfileType;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ProfileShowDto {
    private Long id;
    private String profileId;
    private String name;
    private ProfilePlatform platform;
    private ProfileType profileType;
    private String expirationDate;

    public static ProfileShowDto of(Profile profile){
        return ProfileShowDto.builder()
                .id(profile.getId())
                .profileId(profile.getProfileId())
                .expirationDate(profile.getExpirationDate())
                .name(profile.getName())
                .platform(profile.getPlatform())
                .profileType(profile.getProfileType()).build();
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