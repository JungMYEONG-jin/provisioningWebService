package mj.provisioning;

import mj.provisioning.device.application.port.in.DeviceUseCase;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
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
	 * @param profileUseCase
	 * @param profileDeviceUseCase
	 * @param deviceUseCase
	 * @return
	 */
	@Bean
	@Profile("aws")
	public TestDataInit testDataInit(ProfileUseCase profileUseCase, ProfileDeviceUseCase profileDeviceUseCase, DeviceUseCase deviceUseCase){
		return new TestDataInit(profileUseCase, profileDeviceUseCase, deviceUseCase);
	}
}
