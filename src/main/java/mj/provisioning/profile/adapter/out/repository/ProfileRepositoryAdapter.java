package mj.provisioning.profile.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.device.domain.Device;
import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryAdapter implements ProfileRepositoryPort {

    private final ProfileRepository profileRepository;

    @Override
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    public List<Profile> saveAll(List<Profile> profiles) {
        // id가 이미 존재하기 때문에 기존에 있다면 update 할것임.
//        List<Profile> notContained = profiles.stream().filter(profile -> !profileRepository.existsByProfileId(profile.getProfileId())).collect(Collectors.toList());
        return profileRepository.saveAll(profiles);
    }

    @Override
    public List<Profile> findByNameLike(String name) {
        return profileRepository.findByNameLike(name);
    }

    @Override
    public Optional<Profile> findByProfileId(String profileId) {
        return profileRepository.findByProfileId(profileId);
    }

    @Override
    public Optional<Profile> findByProfileIdFetchJoin(String profileId) {
        return Optional.of(profileRepository.findByFetchJoin(profileId));
    }

    @Override
    public Optional<Profile> findByName(String name) {
        return profileRepository.findByName(name);
    }

    @Override
    public List<ProfileShowDto> searchCondition(ProfileSearchCondition condition) {
        return profileRepository.getSearchByCondition(condition);
    }

    @Override
    public void deleteProfile(String profileId) {
        profileRepository.deleteAllByProfileId(profileId);
    }

    /**
     * cascade까지 날려야 해서 deleteAll이 최선인듯...
     */
    @Override
    public void deleteAll() {
        profileRepository.deleteAll();
    }

    @Override
    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    @Override
    public boolean isExist(String profileId) {
        return profileRepository.existsByProfileId(profileId);
    }
}
