package mj.provisioning.profile.application.port.in;

import lombok.*;
import mj.provisioning.certificate.application.port.in.CertificateShowListDto;
import mj.provisioning.device.application.port.in.DeviceShowListDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleShowListDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateShowListDto;

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
    private ProfileBundleShowListDto bundle;
    private ProfileCertificateShowListDto certificates;
    private DeviceShowListDto devices;
}
