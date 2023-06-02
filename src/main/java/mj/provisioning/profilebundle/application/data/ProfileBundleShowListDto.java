package mj.provisioning.profilebundle.application.data;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ProfileBundleShowListDto {
    @Builder.Default
    private List<ProfileBundleShowDto> bundleData = new ArrayList<>();
}
