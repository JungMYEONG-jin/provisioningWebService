package mj.provisioning;

import mj.provisioning.certificate.application.port.in.CertificateUseCase;
import mj.provisioning.device.application.port.in.DeviceUseCase;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleUseCase;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import mj.provisioning.test.TestDataInit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
	 private final ProfileDeviceUseCase profileDeviceUseCase;
	 private final ProfileCertificateUseCase profileCertificateUseCase;
	 private final ProfileBundleUseCase profileBundleUseCase;
	 * @return
	 */
	@Bean
//	@Profile("aws")
	@Profile("dev")
	public TestDataInit testDataInit(ProfileUseCase profileUseCase, DeviceUseCase deviceUseCase, CertificateUseCase certificateUseCase,
									 ProfileDeviceUseCase profileDeviceUseCase, ProfileCertificateUseCase profileCertificateUseCase, ProfileBundleUseCase profileBundleUseCase){
		return new TestDataInit(profileUseCase, deviceUseCase, certificateUseCase, profileDeviceUseCase, profileCertificateUseCase, profileBundleUseCase);
	}
}
