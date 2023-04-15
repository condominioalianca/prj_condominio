package com.condominio.novaalianca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(basePackages={"com.condominio.novaalianca.*"})
public class NovaaliancaApplication {



	public static void main(String[] args) {
		SpringApplication.run(NovaaliancaApplication.class, args);

		System.out.println("Start");
	}

}
