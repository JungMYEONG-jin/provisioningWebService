package mj.provisioning.profile.application.port.in;

import lombok.*;
import mj.provisioning.device.application.port.in.DeviceShowDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleShowDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateShowDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileEditRequestDto {
    private String profileId;
    private String name;
    private String type;
    private List<ProfileBundleShowDto> bundleData = new ArrayList<>();
    private List<ProfileCertificateShowDto> certificateData = new ArrayList<>();
    private List<DeviceShowDto> deviceData = new ArrayList<>();
}
