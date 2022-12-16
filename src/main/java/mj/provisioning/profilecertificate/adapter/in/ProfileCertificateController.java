package mj.provisioning.profilecertificate.adapter.in;

import lombok.RequiredArgsConstructor;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateShowListDto;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileCertificateController {
    private final ProfileCertificateUseCase profileCertificateUseCase;

    @GetMapping("/test/profile/{id}/certificate")
    public ResponseEntity<ProfileCertificateShowListDto> getList(@PathVariable("id") String profileId){
        ProfileCertificateShowListDto list = profileCertificateUseCase.getProfileCertificateList(profileId);
        return ResponseEntity.ok(list);
    }
}
