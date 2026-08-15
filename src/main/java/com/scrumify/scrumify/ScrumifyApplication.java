package com.scrumify.scrumify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ScrumifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrumifyApplication.class, args);
	}

}
