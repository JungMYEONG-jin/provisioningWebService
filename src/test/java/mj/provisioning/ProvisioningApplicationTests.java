package mj.provisioning;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mj.provisioning.bundle.application.port.in.BundleUseCase;
import mj.provisioning.certificate.application.port.in.CertificateUseCase;
import mj.provisioning.device.application.port.in.DeviceUseCase;
import mj.provisioning.device.application.service.DeviceService;
import mj.provisioning.profile.application.port.in.ProfileUseCase;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.profilebundle.application.port.in.ProfileBundleUseCase;
import mj.provisioning.profilecertificate.application.port.in.ProfileCertificateUseCase;
import mj.provisioning.profiledevice.application.port.in.ProfileDeviceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.annotation.Commit;

import java.util.List;

@SpringBootTest
class ProvisioningApplicationTests {

	@Autowired
	ProfileDeviceUseCase profileDeviceUseCase;
	@Autowired
	ProfileUseCase profileUseCase;
	@Autowired
	DeviceUseCase deviceUseCase;
	@Autowired
	ProfileCertificateUseCase profileCertificateUseCase;
	@Autowired
	CertificateUseCase certificateUseCase;
	@Autowired
	ProfileBundleUseCase profileBundleUseCase;
	@Autowired
	BundleUseCase bundleUseCase;

	@Commit
	@Description("통합 테스트")
	@Test
	void contextLoads() {
		profileUseCase.saveProfiles();
		deviceUseCase.saveDevices();
		certificateUseCase.saveCertificates();
		bundleUseCase.saveBundles();

		List<Profile> all = profileUseCase.findByNameLike("test");
		all.forEach(profile -> {
			profileDeviceUseCase.saveProfileDevice(profile.getProfileId());
			profileCertificateUseCase.saveProfileCertificate(profile.getProfileId());
			profileBundleUseCase.saveProfileBundles(profile.getProfileId());
		});
	}


	@Test
	void createProfileTest() {
		String name = "test_todat_hh";
		String profileId = "2BV6CUSYMK";
		Profile profile = profileUseCase.getProfile(profileId);
		JsonObject attr = new JsonObject();
		attr.addProperty("name", name);
		attr.addProperty("profileType", profile.getProfileType().name());

		JsonObject relationship = new JsonObject();
		JsonObject profileBundleForUpdate = profileBundleUseCase.getProfileBundleForUpdate(profileId);
		JsonObject deviceForUpdateProfile = profileDeviceUseCase.getDeviceForUpdateProfile(profileId);
		JsonObject profileCertificatesForUpdate = profileCertificateUseCase.getProfileCertificatesForUpdate(profileId);

		relationship.add("bundleId", profileBundleForUpdate);
		relationship.add("certificates", profileCertificatesForUpdate);
		relationship.add("devices", deviceForUpdateProfile);

		JsonObject param = new JsonObject();
		param.addProperty("type", profile.getType());
		param.add("attributes", attr);
		param.add("relationships", relationship);

		JsonObject toPostData = new JsonObject();
		toPostData.add("data", param);
		System.out.println("toPostData = " + toPostData);
	}

	@Test
	void jsonDoubleQuote() {
		String a = "{\n" +
				"    \"name\": \"testeaddev4554ice3\",\n" +
				"    \"type\": \"IOS_APP_DEVELOPMENT\",\n" +
				"    \"profileId\": \"H335K28HD2\",\n" +
				"    \"bundles\": [\n" +
				"        {\n" +
				"            \"key\": 597,\n" +
				"            \"appId\": \"XC crashlytuc (66UW797TRG.crashlytuc)\",\n" +
				"            \"bundleId\": \"Z696DHDP3Y\",\n" +
				"            \"type\": \"bundleIds\",\n" +
				"            \"selected\": true\n" +
				"        }\n" +
				"    ],\n" +
				"    \"certificates\": [\n" +
				"        {\n" +
				"            \"key\": 0,\n" +
				"            \"displayName\": \"SOOJIN CHOI\",\n" +
				"            \"expirationDate\": \"2023/11/25\",\n" +
				"            \"certificateId\": \"26RLS77PFS\",\n" +
				"            \"type\": \"certificates\",\n" +
				"            \"selected\": true\n" +
				"        }\n" +
				"    ],\n" +
				"    \"devices\": [\n" +
				"        {\n" +
				"            \"key\": 0,\n" +
				"            \"name\": \"O2O_개인_배성진_iPhone7\",\n" +
				"            \"deviceId\": \"28H5YM8L7M\",\n" +
				"            \"type\": \"devices\",\n" +
				"            \"selected\": true\n" +
				"        },\n" +
				"        {\n" +
				"            \"key\": 1,\n" +
				"            \"name\": \"SMART_핑거_정현아_iPhone11\",\n" +
				"            \"deviceId\": \"28UF688C87\",\n" +
				"            \"type\": \"devices\",\n" +
				"            \"selected\": true\n" +
				"        }\n" +
				"    ]\n" +
				"}";
		JsonParser parser = new JsonParser();
		String s = parser.parse(a).toString();
		System.out.println("s = " + s);

	}
}
