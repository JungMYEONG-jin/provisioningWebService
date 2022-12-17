package mj.provisioning.profilebundle.application.port.in;

public interface ProfileBundleUseCase {
    void saveProfileBundles(String profileId);
    ProfileBundleShowListDto getAllBundles();
}
