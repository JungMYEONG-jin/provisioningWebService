package mj.provisioning.profile.domain;

import com.google.gson.JsonObject;
import lombok.*;
import mj.provisioning.profilebundle.domain.ProfileBundle;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "profile_real_id")
    private String profileId;
    @Column(name = "real_type")
    private String type;
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

    @OneToOne(mappedBy = "profile", cascade = CascadeType.REMOVE)
    private ProfileBundle profileBundle;

    public void insertBundle(ProfileBundle profileBundle){
        this.profileBundle = profileBundle;
        profileBundle.setProfile(this);
    }

    public void updateProfile(JsonObject object){
        JsonObject attributes = object.getAsJsonObject("attributes");
        String profileId = object.get("id").toString().replaceAll("\"", "");
        String type = object.get("type").toString().replaceAll("\"", "");
        String profileName = attributes.get("name").toString().replaceAll("\"", "");
        String expirationDate = attributes.get("expirationDate").toString().replaceAll("\"", "").replaceAll("[^0-9]", "");
        expirationDate = expirationDate.substring(0, 4) + "/" + expirationDate.substring(4, 6) + "/" + expirationDate.substring(6, 8);
        String platform = attributes.get("platform").toString().replaceAll("\"", "");
        String profileState = attributes.get("profileState").toString().replaceAll("\"", "");
        String profileType = attributes.get("profileType").toString().replaceAll("\"", "");
        String profileContent = attributes.get("profileContent").toString().replaceAll("\"", "");
        String uuid = attributes.get("uuid").toString().replaceAll("\"", "");
        this.profileId = profileId;
        this.type = type;
        this.name = profileName;
        this.expirationDate = expirationDate;
        this.platform = ProfilePlatform.get(platform);
        this.profileState = ProfileState.get(profileState);
        this.profileType = ProfileType.get(profileType);
        this.profileContent = profileContent;
        this.uuid = uuid;
    }

}
