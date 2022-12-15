package mj.provisioning.certificate.application.port.in;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class CertificateShowDto {
    private String displayName;
    private String expirationDate;
}
