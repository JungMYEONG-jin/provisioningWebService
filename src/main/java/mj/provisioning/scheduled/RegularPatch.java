package mj.provisioning.scheduled;

import lombok.RequiredArgsConstructor;
import mj.provisioning.bundle.application.port.in.BundleUseCase;
import mj.provisioning.certificate.application.port.in.CertificateUseCase;
import mj.provisioning.device.application.port.in.DeviceUseCase;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleUseCase;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegularPatch {

    private final ProfileUseCase profileUseCase;
    private final ProfileBundleUseCase profileBundleUseCase;
    private final ProfileDeviceUseCase profileDeviceUseCase;
    private final ProfileCertificateUseCase profileCertificateUseCase;
    private final DeviceUseCase deviceUseCase;
    private final CertificateUseCase certificateUseCase;
    private final BundleUseCase bundleUseCase;

    // 매월 매일 새벽 1시에 정보 동기화를 진행한다.
    @Scheduled(cron = "0 0 1 * * *")
    void initRegularPatch(){
        profileUseCase.saveProfiles();
        deviceUseCase.saveDevices();
        certificateUseCase.saveCertificates();
        bundleUseCase.saveBundles();

        profileUseCase.findAll().forEach(profile -> {
            profileDeviceUseCase.saveProfileDevice(profile.getProfileId());
            profileCertificateUseCase.saveProfileCertificate(profile.getProfileId());
            profileBundleUseCase.saveProfileBundles(profile.getProfileId());
        });
    }



}
