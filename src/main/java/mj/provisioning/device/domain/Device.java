package mj.provisioning.device.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@ToString
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;
    private String udId; // 유일함
    @Column(name = "device_real_id")
    private String deviceId;
    @Column(name = "real_type")
    private String type;
    private String name;
    private String deviceClass;
}
