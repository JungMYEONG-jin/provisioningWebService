package mj.provisioning.profile.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import mj.provisioning.profile.application.port.in.ProfileSearchCondition;
import mj.provisioning.profile.application.port.in.ProfileShowDto;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profile.domain.ProfilePlatform;
import mj.provisioning.profile.domain.ProfileState;
import mj.provisioning.profile.domain.ProfileType;
import mj.provisioning.profiledevice.adapter.out.repository.ProfileDeviceRepository;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import mj.provisioning.profiledevice.domain.ProfileDevice;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ProfileService implements ProfileUseCase {

    private final ProfileRepositoryPort profileRepositoryPort;
    private final AppleApi appleApi;

    /**
     *  base64 인코딩된거를 디코드하고 .mobileprovision 형식으로 저장하면 됨.
     *  추후 요청시 profile 업데이트하고 content 가져와서 파일로 저장후 svn에 올리자
     *  스프링 배치 돌려서 새벽에 돌도록 나중에 설정하자.
     */
    @Override
    public void saveProfiles() {
        // profile 생성 전략이
        // 수정을 해도 모든값이 변경되어 저장이된다.
        // 어차피 값도 작으니 그냥 싹 삭제후 다시 받아오는게 최선인듯
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
            String profileName = attributes.get("name").toString().replaceAll("\"", "");
            String expirationDate = attributes.get("expirationDate").toString().replaceAll("\"", "").replaceAll("[^0-9]", "");
            expirationDate = expirationDate.substring(0, 4) + "/" + expirationDate.substring(4, 6) + "/" + expirationDate.substring(6, 8);
            String platform = attributes.get("platform").toString().replaceAll("\"", "");
            String profileState = attributes.get("profileState").toString().replaceAll("\"", "");
            String profileType = attributes.get("profileType").toString().replaceAll("\"", "");
            String profileContent = attributes.get("profileContent").toString().replaceAll("\"", "");
            String uuid = attributes.get("uuid").toString().replaceAll("\"", "");
            /**
             * 빌더 패턴 사용시 기본 생성자 전략 무시함!!
             * 따라서 nullpointer 조심하자~
             */
            Profile build = Profile.builder().profileId(profileId)
                    .name(profileName)
                    .profileState(ProfileState.get(profileState))
                    .profileType(ProfileType.get(profileType))
                    .expirationDate(expirationDate)
                    .platform(ProfilePlatform.get(platform))
                    .profileContent(profileContent)
                    .uuid(uuid)
                    .deviceList(new ArrayList<>())
                    .build();
            // 개발용만 device가 존재
            profiles.add(build);
        }
        profileRepositoryPort.saveAll(profiles);
    }

    @Override
    public void saveProfile(Profile profile) {
        profileRepositoryPort.save(profile);
    }

    @Override
    public void updateProfile(String profileId) {
        // 새 이름으로 업데이트? 생성느낌
        Profile profile = profileRepositoryPort.findByProfileId(profileId).orElseThrow(() -> new RuntimeException("해당 조건에 맞는 프로비저닝이 존재하지 않습니다."));

//        String id = profile.getProfileId(); // 올드 id
//        JSONObject bundleIdFromProfile = getBundleIdFromProfile(jwt, id);
//        System.out.println("bundleIdFromProfile = " + bundleIdFromProfile);
//        JSONObject certificateFromProfile = getCertificateFromProfile(jwt, id);
//        System.out.println("certificateFromProfile = " + certificateFromProfile);
//        JSONArray devices = getDeviceInfoFromProfile(jwt, id);
//        System.out.println("devices = " + devices);
//        String profile = createProfile(jwt, obj.get("name").toString(), obj.get("type").toString(),
//                bundleIdFromProfile.get("id").toString(), certificateFromProfile.get("id").toString(), devices);
//        JSONParser parser = new JSONParser();
//        try {
//            JSONObject parse = (JSONObject)parser.parse(profile);
//            JSONObject data = (JSONObject)parse.get("data");
//            String newId = data.get("id").toString();
//            int code = deleteProfile(jwt, id);// 기존 삭제
//            return code;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return -1; // error

        // 기존꺼 삭제
        appleApi.deleteProfile(profileId);
        profileRepositoryPort.deleteProfile(profileId);
        // prfoileDevice에서도 삭제

        //profileCertificate에서도 삭제

        // bundle에서도 삭제


    }

    /**
     * profileId는 고유하기 때문에 문제 없음.
     * @param profileId
     */
    @Override
    public void deleteProfile(String profileId) {
        appleApi.deleteProfile(profileId);
        profileRepositoryPort.deleteProfile(profileId);
    }

    @Override
    public List<ProfileShowDto> searchByCondition(ProfileSearchCondition condition) {
        return profileRepositoryPort.searchCondition(condition);
    }

    @Override
    public List<Profile> findAll() {
        return profileRepositoryPort.findAll();
    }

}
