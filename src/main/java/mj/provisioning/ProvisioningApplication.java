package mj.provisioning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ProvisioningApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProvisioningApplication.class, args);
	}

}
