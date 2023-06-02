package mj.provisioning.profilebundle.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilebundle.application.port.out.ProfileBundleDeletePort;
import mj.provisioning.profilebundle.application.port.out.ProfileBundleFindPort;
import mj.provisioning.profilebundle.application.port.out.ProfileBundleSavePort;
import mj.provisioning.profilebundle.domain.ProfileBundle;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileBundleFindAdapter implements ProfileBundleFindPort, ProfileBundleSavePort, ProfileBundleDeletePort {
    private final ProfileBundleRepository profileBundleRepository;

    @Override
    public Long deleteByProfileId(String profileId) {
        return profileBundleRepository.deleteByProfile_ProfileId(profileId);
    }

    @Override
    public void deleteByProfile(Profile profile) {
        profileBundleRepository.deleteByProfile(profile);
    }

    @Override
    public ProfileBundle findByProfileId(String profileId) {
        return profileBundleRepository.findByProfile_ProfileId(profileId).orElseThrow(()->new RuntimeException("해당 프로비저닝에 일치하는 번들 정보가 존재하지 않습니다."));
    }

    @Override
    public ProfileBundle save(ProfileBundle profileBundle) {
        return profileBundleRepository.save(profileBundle);
    }

    @Override
    public boolean isExist(String bundleId, Profile profile) {
        return profileBundleRepository.existsByBundleIdAndProfile(bundleId, profile);
    }
}
