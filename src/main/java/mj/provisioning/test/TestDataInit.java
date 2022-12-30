package mj.provisioning.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.bundle.application.port.in.BundleUseCase;
import mj.provisioning.certificate.application.port.in.CertificateUseCase;
import mj.provisioning.device.application.port.in.DeviceUseCase;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleUseCase;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import mj.provisioning.svn.domain.ProvisioningRepository;
import mj.provisioning.svn.domain.SvnRepoInfo;
import mj.provisioning.svn.repository.SvnRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final ProfileUseCase profileUseCase;
    private final DeviceUseCase deviceUseCase;
    private final CertificateUseCase certificateUseCase;
    private final BundleUseCase bundleUseCase;
    private final ProfileDeviceUseCase profileDeviceUseCase;
    private final ProfileCertificateUseCase profileCertificateUseCase;
    private final ProfileBundleUseCase profileBundleUseCase;
    private final SvnRepository svnRepository;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init() throws InterruptedException {
        List<SvnRepoInfo> res = new ArrayList<>();
        for (ProvisioningRepository value : ProvisioningRepository.values()) {
            res.add(SvnRepoInfo.builder()
                    .provisioningName(value.name())
                    .uri(value.getUri())
                    .build());
        }
        svnRepository.saveAll(res);
        profileUseCase.saveProfiles();
        deviceUseCase.saveDevices();
        certificateUseCase.saveCertificates();
        bundleUseCase.saveBundles();

        List<Profile> all = profileUseCase.findAll();

        all.forEach(profile -> {
            profileDeviceUseCase.saveProfileDevice(profile.getProfileId());
            profileCertificateUseCase.saveProfileCertificate(profile.getProfileId());
            profileBundleUseCase.saveProfileBundles(profile.getProfileId());
        });

    }
}
