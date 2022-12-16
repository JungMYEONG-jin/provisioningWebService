package mj.provisioning.bundle.domain;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Bundle {
    /**
     * id, name,identifier, seedId
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bundle_real_id")
    private String bundleId;
    private String name;
    private String identifier;
    private String seedId;
    @Column(name = "real_type")
    private String type;
}
