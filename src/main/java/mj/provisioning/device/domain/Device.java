package mj.provisioning.device.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String udId; // 유일함
    @Column(name = "device_real_id")
    private String deviceId;
    private String type;
    private String name;
    private String deviceClass;
}
