package mj.provisioning.profilebundle.application.data;

import lombok.*;
import mj.provisioning.bundle.domain.Bundle;
import mj.provisioning.profilebundle.domain.ProfileBundle;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
@ToString
public class ProfileBundleShowDto {
    private Long key;
    private String appId;
    private String bundleId;
    private boolean chosen;
    private String type;

    public static ProfileBundleShowDto of(ProfileBundle bundle){
        return ProfileBundleShowDto.builder()
                .key(bundle.getId())
                .appId(bundle.getName()+" ("+bundle.getSeedId()+"."+bundle.getIdentifier()+")")
                .type(bundle.getType())
                .build();
    }

    public static ProfileBundleShowDto of(Bundle bundle, boolean chosen){
        return ProfileBundleShowDto.builder()
                .key(bundle.getId())
                .appId(bundle.getName()+" ("+bundle.getSeedId()+"."+bundle.getIdentifier()+")")
                .bundleId(bundle.getBundleId())
                .chosen(chosen)
                .type(bundle.getType())
                .build();
    }
}
