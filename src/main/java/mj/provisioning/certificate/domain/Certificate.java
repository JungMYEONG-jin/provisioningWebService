package mj.provisioning.certificate.domain;

import com.google.gson.JsonObject;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@ToString(exclude = {"id", "serialNumber"})
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long id;

    @Column(name = "real_type")
    private String type;
    @Column(name = "certificate_real_id")
    private String certificateId;
    private String serialNumber;
    private String displayName;
    private String name;
    private String expirationDate;
    @Enumerated(EnumType.STRING)
    private CertificateType certificateType;
}
