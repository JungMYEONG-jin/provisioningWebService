package mj.provisioning.profilecertificate.application.port.in;

import lombok.*;
import mj.provisioning.profilecertificate.domain.ProfileCertificate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ProfileCertificateShowDto {
    private String displayName;
    private String expirationDate;
    private String certificateId;
    public static ProfileCertificateShowDto of(ProfileCertificate certificate){
        return ProfileCertificateShowDto.builder().displayName(certificate.getDisplayName())
                .expirationDate(certificate.getExpirationDate())
                .certificateId(certificate.getCertificateId())
                .build();
    }
}
