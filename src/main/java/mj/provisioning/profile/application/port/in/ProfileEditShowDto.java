package mj.provisioning.profile.application.port.in;

import lombok.*;
import mj.provisioning.device.application.port.in.DeviceShowDto;
import mj.provisioning.profilebundle.application.data.ProfileBundleShowDto;
import mj.provisioning.profilecertificate.application.data.ProfileCertificateShowDto;
import mj.provisioning.svn.dto.ProvisioningRepositoryDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProfileEditShowDto {
    private String name;
    private String status;
    private String type;
    private String expires;
    private String profileId;
//    private ProfileBundleShowListDto bundle;
//    private ProfileCertificateShowListDto certificates;
//    private DeviceShowListDto devices;
    @Builder.Default
    private List<ProfileBundleShowDto> bundles = new ArrayList<>();
    @Builder.Default
    private List<ProfileCertificateShowDto> certificates = new ArrayList<>();
    @Builder.Default
    private List<DeviceShowDto> devices = new ArrayList<>();
    @Builder.Default
    private List<ProvisioningRepositoryDto> svnRepos = new ArrayList<>();
}
