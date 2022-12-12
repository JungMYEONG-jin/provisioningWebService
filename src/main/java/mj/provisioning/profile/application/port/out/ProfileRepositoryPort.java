package mj.provisioning.profile.application.port.out;

import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.domain.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileRepositoryPort {
    Profile save(Profile profile);
    List<Profile> saveAll(List<Profile> profiles);
    Optional<Profile> findById(Long id);
    Optional<Profile> findByProfileId(String profileId);
    Optional<Profile> findByName(String name);
    List<Profile> findByNames(List<String> names);
    List<ProfileSearchCondition> searchCondition(ProfileSearchCondition condition); // 메인에서 조회 조건
}
