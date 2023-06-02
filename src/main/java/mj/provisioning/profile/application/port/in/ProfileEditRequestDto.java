package mj.provisioning.profile.application.port.in;

import lombok.*;
import mj.provisioning.device.application.port.in.DeviceShowDto;
import mj.provisioning.profilebundle.application.data.ProfileBundleShowDto;
import mj.provisioning.profilecertificate.application.data.ProfileCertificateShowDto;
import mj.provisioning.svn.dto.ProvisioningRepositoryDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@ToString
public class ProfileEditRequestDto {
    private String profileId;
    private String name;
    private String type;
    @Builder.Default
    private List<ProfileBundleShowDto> bundles = new ArrayList<>();
    @Builder.Default
    private List<ProfileCertificateShowDto> certificates = new ArrayList<>();
    @Builder.Default
    private List<DeviceShowDto> devices = new ArrayList<>();
    @Builder.Default
    private List<ProvisioningRepositoryDto> svnRepos = new ArrayList<>();
}
