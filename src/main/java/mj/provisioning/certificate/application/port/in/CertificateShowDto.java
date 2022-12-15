package mj.provisioning.certificate.application.port.in;

import lombok.*;
import mj.provisioning.certificate.domain.Certificate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class CertificateShowDto {
    private String displayName;
    private String expirationDate;

    public static CertificateShowDto of(Certificate certificate){
        return CertificateShowDto.builder().displayName(certificate.getDisplayName())
                .expirationDate(certificate.getExpirationDate())
                .build();
    }
}
