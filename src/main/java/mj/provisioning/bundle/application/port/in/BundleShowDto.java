package mj.provisioning.bundle.application.port.in;

import lombok.*;
import mj.provisioning.bundle.domain.Bundle;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class BundleShowDto {
    private Long key;
    private String name;
    private String identifier;
    private String seedId;
    private String appId;

    public static BundleShowDto of(Bundle bundle){
        return BundleShowDto.builder()
                .key(bundle.getId())
                .name(bundle.getName())
                .identifier(bundle.getIdentifier())
                .seedId(bundle.getSeedId())
                .appId(bundle.getName()+" ("+bundle.getSeedId()+"."+bundle.getIdentifier()+")")
                .build();
    }
}
