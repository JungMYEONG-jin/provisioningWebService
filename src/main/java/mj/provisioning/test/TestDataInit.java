package mj.provisioning.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.certificate.application.port.in.CertificateUseCase;
import mj.provisioning.device.application.port.in.DeviceUseCase;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final ProfileUseCase profileUseCase;
    private final ProfileDeviceUseCase profileDeviceUseCase;
    private final DeviceUseCase deviceUseCase;
    private final CertificateUseCase certificateUseCase;
    private final ProfileCertificateUseCase profileCertificateUseCase;

//A2BFWL3C73
    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init(){
        profileUseCase.saveProfiles(); // 모든 profiles 업데이트
        deviceUseCase.saveDevices(); // 모든 device 업데이트
        certificateUseCase.saveCertificates(); // 모든 certificate 업데이트

        List<Profile> all = profileUseCase.findAll();
        all.forEach(profile -> {
            profileDeviceUseCase.saveProfileDevice(profile.getProfileId());
            profileCertificateUseCase.saveProfileCertificate(profile.getProfileId());
        });
    }

}
