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
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@ToString
public class ProfileEditRequestDto {
    private String profileId;
    private String name;
    private String type;
    private List<ProfileBundleShowDto> bundles = new ArrayList<>();
    private List<ProfileCertificateShowDto> certificates = new ArrayList<>();
    private List<DeviceShowDto> devices = new ArrayList<>();
}
