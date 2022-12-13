package mj.provisioning.profile.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import mj.provisioning.device.domain.Device;
import mj.provisioning.profiledevice.domain.ProfileDevice;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String profileId;
    private String name;
    private String expirationDate;
    @Enumerated(EnumType.STRING)
    private ProfilePlatform platform;
    @Enumerated(EnumType.STRING)
    private ProfileState profileState;
    @Enumerated(EnumType.STRING)
    private ProfileType profileType;
    @Column(columnDefinition = "TEXT")
    private String profileContent;
    private String uuid;

    /**
     * 매번 가져오면 시간이 비효율적이다
     * 새벽에 매번 리스트를 업데이트하자.
     *
     */
    @OneToMany(mappedBy = "profile")
    private List<ProfileDevice> deviceList = new ArrayList<>();

    public void insertDevice(ProfileDevice profileDevice){
        deviceList.add(profileDevice);
        profileDevice.setProfile(this);
    }

    public void insertAll(List<ProfileDevice> all){
        deviceList = all;
        for (ProfileDevice profileDevice : all) {
            profileDevice.setProfile(this);
        }
    }

}
