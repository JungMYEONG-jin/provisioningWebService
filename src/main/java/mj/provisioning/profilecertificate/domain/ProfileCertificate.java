package mj.provisioning.profilecertificate.domain;

import lombok.*;
import mj.provisioning.certificate.domain.Certificate;
import mj.provisioning.certificate.domain.CertificateType;
import mj.provisioning.profile.domain.Profile;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class ProfileCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String expirationDate;
    private String displayName;
    private String certificateId;
    @Column(name = "real_type")
    private String type;
    @Enumerated(EnumType.STRING)
    private CertificateType certificateType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public void setProfile(Profile profile){
        this.profile = profile;
    }

    public static ProfileCertificate of(Certificate certificate){
        return ProfileCertificate.builder()
                .certificateType(certificate.getCertificateType())
                .displayName(certificate.getDisplayName())
                .type(certificate.getType())
                .certificateId(certificate.getCertificateId())
                .expirationDate(certificate.getExpirationDate())
                .build();
    }
}
