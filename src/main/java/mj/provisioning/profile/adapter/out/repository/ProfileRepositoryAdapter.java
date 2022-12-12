package mj.provisioning.profile.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
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
        return null;
    }

    @Override
    public List<Profile> saveAll(List<Profile> profiles) {
        return null;
    }

    @Override
    public Optional<Profile> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Profile> findByProfileId(String profileId) {
        return Optional.empty();
    }

    @Override
    public Optional<Profile> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<Profile> findByNames(List<String> names) {
        return null;
    }

    @Override
    public List<ProfileSearchCondition> searchCondition(ProfileSearchCondition condition) {
        return null;
    }
}
