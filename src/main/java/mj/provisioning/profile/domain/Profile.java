package mj.provisioning.profile.domain;

import lombok.*;

import javax.persistence.*;

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
    private String profileContent;
    private String uuid;



}
