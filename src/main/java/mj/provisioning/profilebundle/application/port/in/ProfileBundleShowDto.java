package mj.provisioning.profilebundle.application.port.in;

import lombok.*;
import mj.provisioning.bundle.domain.Bundle;
import mj.provisioning.profilebundle.domain.ProfileBundle;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ProfileBundleShowDto {
    private Long key;
    private String name;
    private String identifier;
    private String seedId;
    private String appId;

    public static ProfileBundleShowDto of(ProfileBundle bundle){
        return ProfileBundleShowDto.builder()
                .key(bundle.getId())
                .name(bundle.getName())
                .identifier(bundle.getIdentifier())
                .seedId(bundle.getSeedId())
                .appId(bundle.getName()+" ("+bundle.getSeedId()+"."+bundle.getIdentifier()+")")
                .build();
    }
}
