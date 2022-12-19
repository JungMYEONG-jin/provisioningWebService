package mj.provisioning.profile.application.port.in;

import mj.provisioning.certificate.application.port.in.CertificateShowListDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleShowListDto;

public class ProfileEditShowDto {
    private String name;
    private String status;
    private String type;
    private String expires;
    private ProfileBundleShowListDto bundles;
    private CertificateShowListDto certificateShowListDto;
}
