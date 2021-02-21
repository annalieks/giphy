package com.bsa.bsa_giphy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BsaGiphyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BsaGiphyApplication.class, args);
	}

}
