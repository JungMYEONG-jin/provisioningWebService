package mj.provisioning.profilecertificate.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import mj.provisioning.profile.application.port.out.ProfileRepositoryPort;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profile.domain.ProfileType;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import mj.provisioning.profilecertificate.application.port.out.ProfileCertificateRepositoryPort;
import mj.provisioning.profilecertificate.domain.ProfileCertificate;
import mj.provisioning.profiledevice.domain.ProfileDevice;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileCertificateService implements ProfileCertificateUseCase {

    private final AppleApi appleApi;
    private final ProfileCertificateRepositoryPort profileCertificateRepositoryPort;
    private final ProfileRepositoryPort profileRepositoryPort;

    @Override
    public void saveProfileCertificate(String profileId) {
        Profile profile = profileRepositoryPort.findByProfileId(profileId).orElseThrow(()-> new RuntimeException("존재하지 않는 프로비저닝입니다."));
        // 기존 삭제
        profileCertificateRepositoryPort.deleteByProfileId(profileId);
        List<ProfileCertificate> profileCertificates = new ArrayList<>();
        String response = appleApi.getProfileCertificate(profileId);

        JsonParser parser = new JsonParser();
        JsonObject parse = parser.parse(response).getAsJsonObject();
        JsonArray data = parse.getAsJsonArray("data");
        if (data!=null) {
            for (JsonElement datum : data) {
                JsonObject dd = datum.getAsJsonObject();
                String certificateId = dd.get("id").toString().replaceAll("\"","");
                String type = dd.get("type").toString().replaceAll("\"","");
                ProfileCertificate profileCertificate = ProfileCertificate.builder().certificateId(certificateId)
                        .type(type).build();
                profileCertificates.add(profileCertificate);
            }
            profile.insertAllCertificate(profileCertificates);
            profileCertificateRepositoryPort.saveAll(profileCertificates);
        }
    }
}
