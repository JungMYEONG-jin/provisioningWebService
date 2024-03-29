package mj.provisioning.profilecertificate.application.data;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ProfileCertificateShowListDto {
    @Builder.Default
    private List<ProfileCertificateShowDto> certificateData = new ArrayList<>();
}
