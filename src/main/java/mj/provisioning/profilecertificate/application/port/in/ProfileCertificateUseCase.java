package mj.provisioning.profilecertificate.application.port.in;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mj.provisioning.profile.application.port.in.ProfileEditRequestDto;
import mj.provisioning.profile.domain.Profile;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ProfileCertificateUseCase {
    void saveProfileCertificate(String profileId);
    void deleteByProfile(Profile profile);
    // 새로 선택된 인증서와 프로비저닝 연관관계 맺어줌.
    void saveUpdatedResult(Profile profile, List<String> certificateIds);
    ProfileCertificateShowListDto getProfileCertificateList(String profileId);
    List<ProfileCertificateShowDto> getProfileCertificateForEdit(String profileId);
    JsonArray getProfileCertificates(String profileId);
    JsonObject getProfileCertificatesForUpdate(String profileId);
    JsonObject getProfileCertificatesForUpdate(List<ProfileCertificateShowDto> certificateData);
}
