package mj.provisioning.profile.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProfileEnumTest {

    @Test
    void ProfileTypeTest() {
        String type = "IOS_APP_DEVELOPMENT";
        ProfileType profileType = ProfileType.get(type);
        Assertions.assertThat(profileType).isEqualTo(ProfileType.IOS_APP_DEVELOPMENT);
    }

    @Test
    void ProfilePlatformTest() {
        String platform = "IOS";
        ProfilePlatform platform1 = ProfilePlatform.get(platform);
        Assertions.assertThat(platform1).isEqualTo(ProfilePlatform.IOS);
    }

    @Test
    void ProfileStateTest() {
        String status = "ACTIVE";
        ProfileState profileState = ProfileState.get(status);
        Assertions.assertThat(profileState).isEqualTo(ProfileState.ACTIVE);
    }
}