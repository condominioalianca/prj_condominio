package com.condominio.novaalianca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NovaaliancaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovaaliancaApplication.class, args);

		System.out.println("Iniciou");
	}

}
