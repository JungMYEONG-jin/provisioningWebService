package mj.provisioning.profilebundle.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.profilebundle.application.port.out.ProfileBundleRepositoryPort;
import mj.provisioning.profilebundle.domain.ProfileBundle;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProfileBundleRepositoryAdapter implements ProfileBundleRepositoryPort {
    private final ProfileBundleRepository profileBundleRepository;

    @Override
    public Long deleteByProfileId(String profileId) {
        return profileBundleRepository.deleteByProfile_ProfileId(profileId);
    }

    @Override
    public ProfileBundle findByProfileId(String profileId) {
        return profileBundleRepository.findByProfile_ProfileId(profileId).orElseThrow(()->new RuntimeException("해당 프로비저닝에 일치하는 번들 정보가 존재하지 않습니다."));
    }

    @Override
    public List<ProfileBundle> getAllProfileBundles() {
        return profileBundleRepository.findAll();
    }

    @Override
    public ProfileBundle save(ProfileBundle profileBundle) {
        return profileBundleRepository.save(profileBundle);
    }
}
