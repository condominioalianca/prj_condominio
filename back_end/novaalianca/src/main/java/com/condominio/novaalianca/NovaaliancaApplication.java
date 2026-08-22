package com.condominio.novaalianca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NovaaliancaApplication {

	public static void main(String[] args) {
		loadDotEnv();
		SpringApplication.run(NovaaliancaApplication.class, args);

		System.out.println("Iniciou");
	}

	private static void loadDotEnv() {
		try {
			java.io.File envFile = findDotEnv(new java.io.File(".").getAbsoluteFile());
			if (envFile != null && envFile.exists()) {
				System.out.println("Carregando variaveis do arquivo .env encontrado em: " + envFile.getAbsolutePath());
				java.nio.file.Files.lines(envFile.toPath())
					.map(String::trim)
					.filter(line -> !line.isEmpty() && !line.startsWith("#") && line.contains("="))
					.forEach(line -> {
						int idx = line.indexOf('=');
						String key = line.substring(0, idx).trim();
						String value = line.substring(idx + 1).trim();
						if (value.startsWith("\"") && value.endsWith("\"")) {
							value = value.substring(1, value.length() - 1);
						} else if (value.startsWith("'") && value.endsWith("'")) {
							value = value.substring(1, value.length() - 1);
						}
						if (System.getProperty(key) == null && System.getenv(key) == null) {
							System.setProperty(key, value);
						}
					});
			}
		} catch (Exception e) {
			System.err.println("Erro ao tentar ler o arquivo .env: " + e.getMessage());
		}
	}

	private static java.io.File findDotEnv(java.io.File currentDir) {
		int depth = 0;
		while (currentDir != null && depth < 4) {
			java.io.File env = new java.io.File(currentDir, ".env");
			if (env.exists()) {
				return env;
			}
			currentDir = currentDir.getParentFile();
			depth++;
		}
		return null;
	}

}
