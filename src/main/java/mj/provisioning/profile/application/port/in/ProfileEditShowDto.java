package mj.provisioning.profile.application.port.in;

import lombok.*;
import mj.provisioning.certificate.application.port.in.CertificateShowListDto;
import mj.provisioning.device.application.port.in.DeviceShowDto;
import mj.provisioning.device.application.port.in.DeviceShowListDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleShowDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleShowListDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateShowDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateShowListDto;

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
}
