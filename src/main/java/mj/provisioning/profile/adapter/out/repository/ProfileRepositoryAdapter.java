package mj.provisioning.profile.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        return profileRepository.saveAll(profiles);
    }

    @Override
    public Optional<Profile> findById(Long id) {
        return profileRepository.findById(id);
    }

    @Override
    public Optional<Profile> findByProfileId(String profileId) {
        return profileRepository.findByProfileId(profileId);
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
    public Long deleteProfile(String profileId) {
        return profileRepository.deleteByProfileId(profileId);
    }
}
