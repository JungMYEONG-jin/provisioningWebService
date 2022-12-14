package mj.provisioning.profile.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.common.exception.CustomException;
import mj.provisioning.common.exception.ErrorCode;
import mj.provisioning.device.application.port.in.DeviceShowDto;
import mj.provisioning.profile.application.port.in.*;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profile.domain.ProfilePlatform;
import mj.provisioning.profile.domain.ProfileState;
import mj.provisioning.profile.domain.ProfileType;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleShowDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleUseCase;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateShowDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import mj.provisioning.svn.domain.SvnRepoInfo;
import mj.provisioning.svn.dto.ProvisioningRepositoryDto;
import mj.provisioning.svn.repository.SvnRepository;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ProfileService implements ProfileUseCase {

    private final ProfileRepositoryPort profileRepositoryPort;
    private final AppleApi appleApi;

    private final ProfileBundleUseCase profileBundleUseCase;
    private final ProfileDeviceUseCase profileDeviceUseCase;
    private final ProfileCertificateUseCase profileCertificateUseCase;
    private final SvnRepository svnRepository;


    /**
     *  base64 ?????????????????? ??????????????? .mobileprovision ???????????? ???????????? ???.
     *  ?????? ????????? profile ?????????????????? content ???????????? ????????? ????????? svn??? ?????????
     *  ????????? ?????? ????????? ????????? ????????? ????????? ????????????.
     */
    @Override
    public void saveProfiles() {
        // profile ?????? ?????????
        // ????????? ?????? ???????????? ???????????? ???????????????.
        // ????????? ?????? ????????? ?????? ??? ????????? ?????? ??????????????? ????????????
        profileRepositoryPort.deleteAll();
        String response = appleApi.getProfileInfo();
        JsonParser parser = new JsonParser();
        JsonObject parse = parser.parse(response).getAsJsonObject();
        JsonArray data = parse.get("data").getAsJsonArray();
        List<Profile> profiles = new ArrayList<>();
        for (JsonElement datum : data) {
            JsonObject dd = datum.getAsJsonObject();
            JsonObject attributes = dd.getAsJsonObject("attributes");
            String profileId = dd.get("id").toString().replaceAll("\"", "");
            String type = dd.get("type").toString().replaceAll("\"", "");
            String profileName = attributes.get("name").toString().replaceAll("\"", "");
            String expirationDate = attributes.get("expirationDate").toString().replaceAll("\"", "").replaceAll("[^0-9]", "");
            expirationDate = expirationDate.substring(0, 4) + "/" + expirationDate.substring(4, 6) + "/" + expirationDate.substring(6, 8);
            String platform = attributes.get("platform").toString().replaceAll("\"", "");
            String profileState = attributes.get("profileState").toString().replaceAll("\"", "");
            String profileType = attributes.get("profileType").toString().replaceAll("\"", "");
            String profileContent = attributes.get("profileContent").toString().replaceAll("\"", "");
            String uuid = attributes.get("uuid").toString().replaceAll("\"", "");
            /**
             * ?????? ?????? ????????? ?????? ????????? ?????? ?????????!!
             * ????????? nullpointer ????????????~
             */
            Profile build = Profile.builder().profileId(profileId)
                    .name(profileName)
                    .type(type)
                    .profileState(ProfileState.get(profileState))
                    .profileType(ProfileType.get(profileType))
                    .expirationDate(expirationDate)
                    .platform(ProfilePlatform.get(platform))
                    .profileContent(profileContent)
                    .uuid(uuid)
                    .deviceList(new ArrayList<>())
                    .certificates(new ArrayList<>())
                    .build();
            // ???????????? device??? ??????
            profiles.add(build);
            }
        profileRepositoryPort.saveAll(profiles);
    }

    @Override
    public void saveProfile(Profile profile) {
        profileRepositoryPort.save(profile);
    }

    @Override
    public void updateProfile(Profile profile, JsonObject param) {
        profile.updateProfile(param);
    }

    /**
     * profileId??? ???????????? ????????? ?????? ??????.
     * @param profileId
     */
    @Override
    public void deleteProfile(String profileId) {
        Profile profile = getProfile(profileId);
        // apple ?????? ??????
        appleApi.deleteProfile(profileId);
        // ?????? ???????????? ??????
        profileDeviceUseCase.deleteByProfile(profile);
        profileCertificateUseCase.deleteByProfile(profile);
        profileBundleUseCase.deleteByProfile(profile);
        // ???????????? ??????
        profileRepositoryPort.deleteProfile(profileId);
    }
    /**
     * @param editRequestDto
     * @return new Profile
     */
    @Override
    public Profile editProvisioning(ProfileEditRequestDto editRequestDto) {
        // get Profile
        Profile prev = getProfile(editRequestDto.getProfileId());
        // get Json Param
        JsonObject attr = new JsonObject();
        attr.addProperty("name", editRequestDto.getName());
        attr.addProperty("profileType", editRequestDto.getType()); // dist, dev
        JsonObject relationships = new JsonObject();

        JsonObject profileCertificatesForUpdate = profileCertificateUseCase.getProfileCertificatesForUpdate(editRequestDto.getCertificates());
        JsonObject profileBundleForUpdate = profileBundleUseCase.getProfileBundleForUpdate(editRequestDto.getBundles());
        JsonObject deviceForUpdateProfile = null;
        // ????????? ??????????????? ?????? ??????????????? ??????
        if (!editRequestDto.getType().equals(ProfileType.IOS_APP_STORE.name())) {
            deviceForUpdateProfile = profileDeviceUseCase.getDeviceForUpdateProfile(editRequestDto.getDevices());
            relationships.add("devices", deviceForUpdateProfile);
        }
        relationships.add("bundleId", profileBundleForUpdate);
        relationships.add("certificates", profileCertificatesForUpdate);

        JsonObject param = new JsonObject();
        param.addProperty("type", "profiles");
        param.add("attributes", attr);
        param.add("relationships", relationships);

        JsonObject toPost = new JsonObject();
        toPost.add("data", param);

        System.out.println("toPost = " + toPost);
        // ?????? ??????
        String profile = appleApi.createProfileNew(appleApi.createJWT(), toPost);
        System.out.println("profile = " + profile);

        // ???????????? ??????????????????
        JsonParser parser = new JsonParser();
        JsonObject updatedResult = parser.parse(profile).getAsJsonObject();
        JsonObject data = updatedResult.getAsJsonObject("data");
        // ?????? ???????????? ???????????? ??? ????????? ?????? ??????
        profileDeviceUseCase.deleteByProfile(prev);
        profileCertificateUseCase.deleteByProfile(prev);
        profileBundleUseCase.deleteByProfile(prev);
        // ??? ???????????? ????????? ????????????
        updateProfile(prev, data);
        // ?????? ?????? ?????? ????????????
        JsonObject newRelationships = data.getAsJsonObject("relationships");
        JsonObject bundleData = newRelationships.getAsJsonObject("bundleId").getAsJsonObject("data");
        // bundle update
        String newBundleId = bundleData.get("id").toString().replaceAll("\"", "");
        profileBundleUseCase.saveUpdatedResult(prev, newBundleId);
        // certificate update
        JsonArray newCertificates = newRelationships.getAsJsonObject("certificates").getAsJsonArray("data");
        List<String> certificateIds = new ArrayList<>();
        for (JsonElement newCertificate : newCertificates) {
            String id = newCertificate.getAsJsonObject().get("id").toString().replaceAll("\"", "");
            certificateIds.add(id);
        }
        profileCertificateUseCase.saveUpdatedResult(prev, certificateIds);
        // device update
        if (!editRequestDto.getType().equals(ProfileType.IOS_APP_STORE.name())) {
            JsonArray newDevices = newRelationships.getAsJsonObject("devices").getAsJsonArray("data");
            List<String> deviceIds = new ArrayList<>();
            for (JsonElement newDevice : newDevices) {
                String id = newDevice.getAsJsonObject().get("id").toString().replaceAll("\"", "");
                deviceIds.add(id);
            }
            profileDeviceUseCase.saveUpdatedResult(prev, deviceIds);
        }
        // update
        saveProfile(prev);
        // ????????? ??????
        appleApi.deleteProfile(editRequestDto.getProfileId());
        // log
        log.info("new id {}", prev.getProfileId());
        log.info("new content {}", prev.getProfileContent());
        // ?????? ??????
        return prev;
    }

    @Override
    public List<ProfileShowDto> searchByCondition(ProfileSearchCondition condition) {
        return profileRepositoryPort.searchCondition(condition);
    }

    @Override
    public List<Profile> findAll() {
        return profileRepositoryPort.findAll();
    }

    @Override
    public Profile getProfile(String profileId) {
        return profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
    }

    @Override
    public ProfileEditShowDto getEditShow(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        List<SvnRepoInfo> svnRepoInfos = svnRepository.findAll();
        List<ProfileBundleShowDto> bundleList = profileBundleUseCase.getBundleForEdit(profileId);
        List<DeviceShowDto> deviceShowList = new ArrayList<>();
        if (!profile.getProfileType().equals(ProfileType.IOS_APP_STORE)) // ?????? ???????????? ?????? ?????? ?????? ??????
            deviceShowList = profileDeviceUseCase.getDeviceForEdit(profileId);
        List<ProfileCertificateShowDto> profileCertificateList = profileCertificateUseCase.getProfileCertificateForEdit(profileId);
        return ProfileEditShowDto.builder().name(profile.getName())
                .expires(profile.getExpirationDate())
                .type(profile.getProfileType().name())
                .status(profile.getProfileState().getValue())
                .certificates(profileCertificateList)
                .bundles(bundleList)
                .devices(deviceShowList)
                .profileId(profileId)
                .svnRepos(svnRepoInfos.stream().map(ProvisioningRepositoryDto::of).collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<Profile> findByNameLike(String name) {
        return profileRepositoryPort.findByNameLike(name);
    }



}
