package mj.provisioning.certificate.application.port.in;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class CertificateShowListDto {
    private List<CertificateShowDto> data = new ArrayList<>();
}
