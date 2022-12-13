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
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class ProfileService implements ProfileUseCase {

    private final ProfileRepositoryPort profileRepositoryPort;
    private final AppleApi appleApi;

    /**
     *  base64 인코딩된거를 디코드하고 .mobileprovision 형식으로 저장하면 됨.
     *  추후 요청시 profile 업데이트하고 content 가져와서 파일로 저장후 svn에 올리자
     */
    @Override
    public void saveProfiles() {
        String response = appleApi.getProfileInfo();
        JsonParser parser = new JsonParser();
        JsonObject parse = parser.parse(response).getAsJsonObject();
        JsonArray data = parse.get("data").getAsJsonArray();
        System.out.println("data.size() = " + data.size());
        List<Profile> profiles = new ArrayList<>();
        for (JsonElement datum : data) {
            JsonObject dd = datum.getAsJsonObject();
            JsonObject attributes = dd.getAsJsonObject("attributes");
            String profileId = dd.get("id").toString().replaceAll("\"", "");
            String profileName = attributes.get("name").toString().replaceAll("\"", "");
            String expirationDate = attributes.get("expirationDate").toString().replaceAll("\"", "");
            String platform = attributes.get("platform").toString().replaceAll("\"", "");
            String profileState = attributes.get("profileState").toString().replaceAll("\"", "");
            String profileType = attributes.get("profileType").toString().replaceAll("\"", "");
            String profileContent = attributes.get("profileContent").toString().replaceAll("\"", "");
            String uuid = attributes.get("uuid").toString().replaceAll("\"", "");


        }

    }

    @Override
    public void saveProfile(Profile profile) {

    }

    @Override
    public void updateProfile(String profileId) {

    }

    @Override
    public void deleteProfile(String profileId) {

    }

    @Override
    public List<ProfileShowDto> searchByCondition(ProfileSearchCondition condition) {
        return null;
    }
}
