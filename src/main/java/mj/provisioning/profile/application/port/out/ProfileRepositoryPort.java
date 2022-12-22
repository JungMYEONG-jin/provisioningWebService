package mj.provisioning.profile.application.port.out;

import mj.provisioning.profile.application.port.in.ProfileEditShowDto;
import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
import mj.provisioning.profile.domain.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileRepositoryPort {
    Profile save(Profile profile);
    List<Profile> saveAll(List<Profile> profiles);
    List<Profile> findByNameLike(String name);
    Optional<Profile> findByProfileId(String profileId);
    Optional<Profile> findByProfileIdFetchJoin(String profileId);
    Optional<Profile> findByName(String name);
    List<ProfileShowDto> searchCondition(ProfileSearchCondition condition); // 메인에서 조회 조건
    void deleteProfile(String profileId);
    void deleteAll();
    List<Profile> findAll();
    boolean isExist(String profileId);
}
