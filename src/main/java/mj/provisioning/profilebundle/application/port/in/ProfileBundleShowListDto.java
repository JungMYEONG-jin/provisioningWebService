package mj.provisioning.profilebundle.application.port.in;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ProfileBundleShowListDto {
    private List<ProfileBundleShowDto> bundleData = new ArrayList<>();
}
