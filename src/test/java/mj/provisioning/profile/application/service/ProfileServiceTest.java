package mj.provisioning.profile.application.service;

import mj.provisioning.profile.adapter.out.repository.ProfileRepository;
import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
import mj.provisioning.profile.domain.Profile;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProfileServiceTest {

    @Autowired
    ProfileService profileService;
    @Autowired
    ProfileRepository profileRepository;

    @Test
    void getProfilesTest() {
        profileService.saveProfiles();
    }

    @Test
    void allConditionTest() {
        ProfileSearchCondition a = new ProfileSearchCondition();
        List<ProfileShowDto> profileShowDtos = profileService.searchByCondition(a);
        List<Profile> all = profileRepository.findAll();
        Assertions.assertThat(profileShowDtos.size()).isEqualTo(all.size());
    }

    @Test
    void searchByNameTest() {
        ProfileSearchCondition a = new ProfileSearchCondition();
        a.setName("sbiz");
        List<ProfileShowDto> profileShowDtos = profileService.searchByCondition(a);
        Assertions.assertThat(profileShowDtos.size()).isEqualTo(5);
    }

    @Test
    void searchByPlatformTest() {
    }

    @Test
    void searchByTypeTest() {
    }
}