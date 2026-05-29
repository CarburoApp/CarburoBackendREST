package app.carburo.api.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CarburoBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(CarburoBackendApplication.class, args);
	}
}
