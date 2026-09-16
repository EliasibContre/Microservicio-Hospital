package com.cesar.ms_pacientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.cesar.ms_pacientes", "com.cesar.commons"})
public class MsPacientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsPacientesApplication.class, args);
	}

}
