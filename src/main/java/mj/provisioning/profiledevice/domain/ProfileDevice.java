package mj.provisioning.profiledevice.domain;

import lombok.*;
import mj.provisioning.device.domain.Device;
import mj.provisioning.profile.domain.Profile;

import javax.persistence.*;

/**
 * 매번 api 통해서 가져오면 비효율적이다.
 * profile과 device간 매핑을 지정해주자.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class ProfileDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceId;
    @Column(name = "real_type")
    private String type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public void setProfile(Profile profile){
        this.profile = profile;
    }

    public static ProfileDevice of(Device device){
        return ProfileDevice.builder().deviceId(device.getDeviceId())
                .type(device.getType())
                .build();
    }
}
