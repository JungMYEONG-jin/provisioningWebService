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
    private Long key;
    private String displayName;
    private String expirationDate;
    private String certificateId;
    private boolean isSelected;
    public static ProfileCertificateShowDto of(ProfileCertificate certificate, boolean isSelected, Long key){
        return ProfileCertificateShowDto.builder().displayName(certificate.getDisplayName())
                .expirationDate(certificate.getExpirationDate())
                .certificateId(certificate.getCertificateId())
                .isSelected(isSelected)
                .key(key)
                .build();
    }

    public static ProfileCertificateShowDto of(Certificate certificate, boolean isSelected, Long key){
        return ProfileCertificateShowDto.builder().displayName(certificate.getDisplayName())
                .expirationDate(certificate.getExpirationDate())
                .certificateId(certificate.getCertificateId())
                .isSelected(isSelected)
                .key(key)
                .build();
    }
}
