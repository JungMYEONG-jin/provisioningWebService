package mj.provisioning.profile.application.port.out;

import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
import mj.provisioning.profile.domain.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileRepositoryPort {
    Profile save(Profile profile);
    List<Profile> saveAll(List<Profile> profiles);
    Optional<Profile> findById(Long id);
    Optional<Profile> findByProfileId(String profileId);
    Optional<Profile> findByName(String name);
    List<ProfileShowDto> searchCondition(ProfileSearchCondition condition); // 메인에서 조회 조건
    Long deleteProfile(String profileId);
}
