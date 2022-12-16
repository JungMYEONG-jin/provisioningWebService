package mj.provisioning.profilecertificate.application.port.in;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ProfileCertificateShowListDto {
    private List<ProfileCertificateShowDto> data = new ArrayList<>();
}
