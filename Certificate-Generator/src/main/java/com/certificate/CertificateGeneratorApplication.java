package com.certificate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CertificateGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CertificateGeneratorApplication.class, args);
	}
	
    @Bean
	CommandLineRunner lookup() {
		return args -> {
			SSLCertificateValidation.disable();
		};
	}
	
}
