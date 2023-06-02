package mj.provisioning.profilecertificate.application.port.out;

import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilecertificate.domain.ProfileCertificate;

import java.util.List;

public interface ProfileCertificateDeletePort {
    Long deleteByProfileId(Profile profile);
}
