package mj.provisioning.profilecertificate.application.port.in;

import lombok.*;
import mj.provisioning.certificate.domain.Certificate;
import mj.provisioning.profilecertificate.domain.ProfileCertificate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
@ToString
public class ProfileCertificateShowDto {
    private String displayName;
    private String expirationDate;
    private String certificateId;
    private boolean isSelected;
    public static ProfileCertificateShowDto of(ProfileCertificate certificate, boolean isSelected){
        return ProfileCertificateShowDto.builder().displayName(certificate.getDisplayName())
                .expirationDate(certificate.getExpirationDate())
                .certificateId(certificate.getCertificateId())
                .isSelected(isSelected)
                .build();
    }

    public static ProfileCertificateShowDto of(Certificate certificate, boolean isSelected){
        return ProfileCertificateShowDto.builder().displayName(certificate.getDisplayName())
                .expirationDate(certificate.getExpirationDate())
                .certificateId(certificate.getCertificateId())
                .isSelected(isSelected)
                .build();
    }
}
