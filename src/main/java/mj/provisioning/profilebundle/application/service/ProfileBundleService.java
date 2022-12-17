package mj.provisioning.profilebundle.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.bundle.application.port.in.BundleShowListDto;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleShowDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleShowListDto;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleUseCase;
import mj.provisioning.profilebundle.application.port.out.ProfileBundleRepositoryPort;
import mj.provisioning.profilebundle.domain.ProfileBundle;
import mj.provisioning.profilecertificate.domain.ProfileCertificate;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileBundleService implements ProfileBundleUseCase {

    private final ProfileBundleRepositoryPort profileBundleRepositoryPort;
    private final AppleApi appleApi;
    private final ProfileRepositoryPort profileRepositoryPort;

    @Override
    public void saveProfileBundles(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileId(profileId).orElseThrow(()-> new RuntimeException("존재하지 않는 프로비저닝입니다."));
        // 기존 삭제
        profileBundleRepositoryPort.deleteByProfileId(profileId);
        String response = appleApi.getBundleIdFromProfile(profileId);
        // 1 to 1 mapping
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(response).getAsJsonObject();
        JsonObject data = object.getAsJsonObject("data");
        String type = data.get("type").toString().replaceAll("\"", "");
        String id = data.get("id").toString().replaceAll("\"", "");
        JsonObject attributes = data.getAsJsonObject("attributes");
        String name = attributes.get("name").toString().replaceAll("\"", "");
        String identifier = attributes.get("identifier").toString().replaceAll("\"", "");
        String seedId = attributes.get("seedId").toString().replaceAll("\"", "");
        ProfileBundle profileBundle = ProfileBundle.builder().bundleId(id)
                .type(type)
                .profile(profile)
                .identifier(identifier)
                .name(name)
                .seedId(seedId).build();
        // 오히려 프로비저닝은 번들 id가 한개고
        // 번들은 여러개의 프로비저닝을 가질수있음
        profileBundleRepositoryPort.save(profileBundle);
    }

    @Override
    public ProfileBundleShowListDto getAllBundles() {
        List<ProfileBundle> allProfileBundles = profileBundleRepositoryPort.getAllProfileBundles();
        List<ProfileBundleShowDto> profileBundleShowDtos = allProfileBundles.stream().map(ProfileBundleShowDto::of).collect(Collectors.toList());
        return ProfileBundleShowListDto.builder().data(profileBundleShowDtos).build();
    }
}
