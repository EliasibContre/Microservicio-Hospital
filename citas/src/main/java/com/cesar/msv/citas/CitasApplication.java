package com.cesar.msv.citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.cesar.msv.citas", "com.cesar.commons"})
public class CitasApplication {

	public static void main(String[] args) {
		SpringApplication.run(CitasApplication.class, args);
	}

}
