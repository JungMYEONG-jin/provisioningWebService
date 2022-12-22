package mj.provisioning.profile.adapter.out.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProfileRepositoryTest {

    @Autowired
    ProfileRepository profileRepository;

    @Test
    void deleteCascadeTest() {
        profileRepository.deleteById(1L);
    }

    @Test
    void deleteAllTest() {
        profileRepository.deleteAll();
    }
}