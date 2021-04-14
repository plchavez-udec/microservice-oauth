package co.edu.ierdminayticha.sgd.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@EnableEurekaClient
@SpringBootApplication
public class OauthApplication implements CommandLineRunner {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(OauthApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Override
	public void run(String... args) throws Exception {
		String password = "1234";

		for (int i = 0; i < 4; i++) {
			String passwordBCryp = passwordEncoder.encode(password);
			System.out.println(passwordBCryp);
		}

	}

}
