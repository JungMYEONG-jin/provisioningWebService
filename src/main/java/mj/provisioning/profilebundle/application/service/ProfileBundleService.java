package mj.provisioning.profilebundle.application.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.bundle.application.port.out.BundleRepositoryPort;
import mj.provisioning.bundle.domain.Bundle;
import mj.provisioning.common.exception.CustomException;
import mj.provisioning.common.exception.ErrorCode;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilebundle.application.data.ProfileBundleShowDto;
import mj.provisioning.profilebundle.application.data.ProfileBundleShowListDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleUseCase;
import mj.provisioning.profilebundle.application.port.out.ProfileBundleDeletePort;
import mj.provisioning.profilebundle.application.port.out.ProfileBundleFindPort;
import mj.provisioning.profilebundle.application.port.out.ProfileBundleSavePort;
import mj.provisioning.profilebundle.domain.ProfileBundle;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static mj.provisioning.util.RegexParsing.*;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileBundleService implements ProfileBundleUseCase {

    private final ProfileBundleFindPort profileBundleFindPort;
    private final ProfileBundleSavePort profileBundleSavePort;
    private final ProfileBundleDeletePort profileBundleDeletePort;
    private final AppleApi appleApi;
    private final ProfileRepositoryPort profileRepositoryPort;
    private final BundleRepositoryPort bundleRepositoryPort;

    @Override
    public void saveProfileBundles(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new CustomException(ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        // 기존 삭제
        profileBundleDeletePort.deleteByProfileId(profileId);
        String response = appleApi.getBundleIdFromProfile(profileId);
        // 1 to 1 mapping
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(response).getAsJsonObject();
        JsonObject data = object.getAsJsonObject("data");
        String type = parseQuotations(data.get("type").toString());
        String id = parseQuotations(data.get("id").toString());
        JsonObject attributes = data.getAsJsonObject("attributes");
        String name = parseQuotations(attributes.get("name").toString());
        String identifier = parseQuotations(attributes.get("identifier").toString());
        String seedId = parseQuotations(attributes.get("seedId").toString());
        ProfileBundle profileBundle = ProfileBundle.builder().bundleId(id)
                .type(type)
                .profile(profile)
                .identifier(identifier)
                .name(name)
                .seedId(seedId).build();
        // 오히려 프로비저닝은 번들 id가 한개고
        // 번들은 여러개의 프로비저닝을 가질수있음
        profile.insertBundle(profileBundle); // profile 삭제시 전부 날리기 위해
        profileBundleSavePort.save(profileBundle);
    }

    @Override
    public void deleteByProfile(Profile profile) {
        profileBundleDeletePort.deleteByProfile(profile);
    }

    @Override
    public ProfileBundleShowListDto getBundleList(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new CustomException(ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        List<Bundle> all = bundleRepositoryPort.findAll();

        return ProfileBundleShowListDto.builder().bundleData(all.stream().map(bundle -> ProfileBundleShowDto.of(bundle, profileBundleFindPort.isExist(bundle.getBundleId(), profile))).collect(Collectors.toList())).build();
    }

    @Override
    public List<ProfileBundleShowDto> getBundleForEdit(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileIdFetchJoin(profileId).orElseThrow(()-> new CustomException(ErrorCode.PROFILE_NOT_EXIST.getMessage(), ErrorCode.PROFILE_NOT_EXIST));
        List<Bundle> all = bundleRepositoryPort.findAll();

        return all.stream().map(bundle -> ProfileBundleShowDto.of(bundle, profileBundleFindPort.isExist(bundle.getBundleId(), profile))).collect(Collectors.toList());
    }

    @Override
    public JsonObject getProfileBundleForUpdate(String profileId) {
        ProfileBundle byProfileId = profileBundleFindPort.findByProfileId(profileId);
        JsonObject object = new JsonObject();
        object.addProperty("type", byProfileId.getType());
        object.addProperty("id", byProfileId.getBundleId());
        JsonObject bundle = new JsonObject();
        bundle.add("data", object);
        return bundle;
    }

    /**
     * request로 부터 select된 번들만 분리하여 json object로 변경
     * @param bundle
     * @return
     */
    @Override
    public JsonObject getProfileBundleForUpdate(List<ProfileBundleShowDto> bundle) {
        JsonObject object = new JsonObject();
        ProfileBundleShowDto matchedBundle = bundle.stream().filter(profileBundleShowDto -> profileBundleShowDto.isChosen()).findFirst().orElseThrow(() -> new CustomException(ErrorCode.BUNDLE_NOT_EXIST.getMessage(), ErrorCode.BUNDLE_NOT_EXIST));
        object.addProperty("id", matchedBundle.getBundleId());
        object.addProperty("type",matchedBundle.getType());
        JsonObject param = new JsonObject();
        param.add("data", object);
        return param;
    }

    @Override
    public void saveUpdatedResult(Profile profile, String bundleId) {
        Bundle byBundle = bundleRepositoryPort.findByBundle(bundleId);
        ProfileBundle update = ProfileBundle.update(profile, byBundle);
        // 오히려 프로비저닝은 번들 id가 한개고
        // 번들은 여러개의 프로비저닝을 가질수있음
        profileBundleSavePort.save(update);
    }
}
