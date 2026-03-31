package com.guibsantos.shorterURL;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Application {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		System.out.println("POSTGRES_PASSWORD: " + System.getenv("POSTGRES_PASSWORD"));
		System.out.println("DB_PASSWORD: " + System.getenv("DB_PASSWORD"));
		System.out.println("DB_USER: " + System.getenv("DB_USER"));


		SpringApplication.run(Application.class, args);
	}

}
