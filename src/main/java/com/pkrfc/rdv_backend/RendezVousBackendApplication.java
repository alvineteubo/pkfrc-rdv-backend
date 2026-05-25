package com.pkrfc.rdv_backend;

import com.pkrfc.rdv_backend.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class RendezVousBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RendezVousBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner start(AppProperties appProperties, Environment env) {
		return args -> {
			String swaggerPath = env.getProperty("springdoc.swagger-ui.path", "/swagger-ui.html");
			log.info("Swagger UI → {}:{}{}", appProperties.address(), appProperties.port(), swaggerPath);

		};
	}
}