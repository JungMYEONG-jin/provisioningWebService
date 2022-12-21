package mj.provisioning.profilebundle.domain;

import lombok.*;
import mj.provisioning.bundle.domain.Bundle;
import mj.provisioning.profile.domain.Profile;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class ProfileBundle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bundleId;
    private String name;
    private String identifier;
    private String seedId;
    @Column(name = "real_type")
    private String type;
    @OneToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    /**
     * String type = data.get("type").toString().replaceAll("\"", "");
     *         String id = data.get("id").toString().replaceAll("\"", "");
     *         JsonObject attributes = data.getAsJsonObject("attributes");
     *         String name = attributes.get("name").toString().replaceAll("\"", "");
     *         String identifier = attributes.get("identifier").toString().replaceAll("\"", "");
     *         String seedId = attributes.get("seedId").toString().replaceAll("\"", "");
     * @param profile
     */

    public void setProfile(Profile profile){
        this.profile = profile;
    }
    public static ProfileBundle update(Profile profile, Bundle bundle){
        return ProfileBundle.builder().bundleId(bundle.getBundleId())
                .identifier(bundle.getIdentifier())
                .name(bundle.getName())
                .profile(profile)
                .seedId(bundle.getSeedId())
                .type(bundle.getType())
                .build();
    }
}
