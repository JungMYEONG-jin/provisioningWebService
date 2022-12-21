package mj.provisioning.profilecertificate.application.port.in;

import lombok.*;
import mj.provisioning.certificate.domain.Certificate;
import mj.provisioning.profilecertificate.domain.ProfileCertificate;

/**
 * 해당 정보를 바탕으로 다시 update에 써야 하기 때문에 type, id 필수
 */
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
    private String type;
    private boolean isSelected;
    public static ProfileCertificateShowDto of(ProfileCertificate certificate, boolean isSelected, Long key){
        return ProfileCertificateShowDto.builder().displayName(certificate.getDisplayName())
                .expirationDate(certificate.getExpirationDate())
                .certificateId(certificate.getCertificateId())
                .isSelected(isSelected)
                .key(key)
                .type(certificate.getType())
                .build();
    }

    public static ProfileCertificateShowDto of(Certificate certificate, boolean isSelected, Long key){
        return ProfileCertificateShowDto.builder().displayName(certificate.getDisplayName())
                .expirationDate(certificate.getExpirationDate())
                .certificateId(certificate.getCertificateId())
                .isSelected(isSelected)
                .key(key)
                .type(certificate.getType())
                .build();
    }
}
