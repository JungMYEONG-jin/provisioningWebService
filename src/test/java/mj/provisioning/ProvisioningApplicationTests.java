package mj.provisioning;

import mj.provisioning.device.application.port.in.DeviceUseCase;
import mj.provisioning.device.application.service.DeviceService;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;

import java.util.List;

@SpringBootTest
class ProvisioningApplicationTests {

	@Autowired
	ProfileDeviceUseCase profileDeviceUseCase;
	@Autowired
	ProfileUseCase profileUseCase;
	@Autowired
	DeviceUseCase deviceUseCase;

	@Description("통합 테스트")
	@Test
	void contextLoads() {
		profileUseCase.saveProfiles();
		deviceUseCase.saveDevices();
		List<Profile> all = profileUseCase.findAll();
		all.stream().forEach(profile -> profileDeviceUseCase.saveProfileDevice(profile.getProfileId()));
	}

}
