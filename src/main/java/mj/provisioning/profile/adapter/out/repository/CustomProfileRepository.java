package mj.provisioning.profile.adapter.out.repository;

import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
import mj.provisioning.profile.domain.Profile;

import java.util.List;

public interface CustomProfileRepository {
    List<ProfileShowDto> getSearchByCondition(ProfileSearchCondition condition);
    Profile findByFetchJoin(String profileId);
    List<Profile> findByNameLike(String name);
}
