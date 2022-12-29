package mj.provisioning;

import mj.provisioning.bundle.application.port.in.BundleUseCase;
import mj.provisioning.certificate.application.port.in.CertificateUseCase;
import mj.provisioning.device.application.port.in.DeviceUseCase;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleUseCase;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import mj.provisioning.svn.repository.SvnRepository;
import mj.provisioning.test.TestDataInit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class ProvisioningApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProvisioningApplication.class, args);
	}
	/**
	 * for test data insert
	 private final ProfileUseCase profileUseCase;
	 private final DeviceUseCase deviceUseCase;
	 private final CertificateUseCase certificateUseCase;
	 private final BundleUseCase bundleUseCase;
	 private final ProfileDeviceUseCase profileDeviceUseCase;
	 private final ProfileCertificateUseCase profileCertificateUseCase;
	 private final ProfileBundleUseCase profileBundleUseCase;
	 * @return
	 */
	@Bean
//	@Profile("aws")
//	@Profile("dev")
//	@Profile("local2")
	@Profile("test")
	public TestDataInit testDataInit(ProfileUseCase profileUseCase, DeviceUseCase deviceUseCase, CertificateUseCase certificateUseCase, BundleUseCase bundleUseCase,
									 ProfileDeviceUseCase profileDeviceUseCase, ProfileCertificateUseCase profileCertificateUseCase, ProfileBundleUseCase profileBundleUseCase, SvnRepository svnRepository){
		return new TestDataInit(profileUseCase, deviceUseCase, certificateUseCase, bundleUseCase, profileDeviceUseCase, profileCertificateUseCase, profileBundleUseCase, svnRepository);
	}

}
