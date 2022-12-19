package mj.provisioning.profilecertificate.application.port.in;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface ProfileCertificateUseCase {
    void saveProfileCertificate(String profileId);
    ProfileCertificateShowListDto getProfileCertificateList(String profileId);
    JsonArray getProfileCertificates(String profileId);
    JsonObject getProfileCertificatesForUpdate(String profileId);
}
