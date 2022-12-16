package mj.provisioning.profile.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import mj.provisioning.device.domain.Device;
import mj.provisioning.profilecertificate.domain.ProfileCertificate;
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
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    @Id
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
    @OneToMany(mappedBy = "profile", cascade = CascadeType.REMOVE) // profile 삭제시 자식도 삭제
    private List<ProfileDevice> deviceList = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.REMOVE)
    private List<ProfileCertificate> certificates = new ArrayList<>();

    public void insertCertificate(ProfileCertificate profileCertificate){
        certificates.add(profileCertificate);
        profileCertificate.setProfile(this);
    }

    public void insertAllCertificate(List<ProfileCertificate> all){
        certificates = all;
        for (ProfileCertificate profileCertificate : all) {
            profileCertificate.setProfile(this);
        }
    }

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
