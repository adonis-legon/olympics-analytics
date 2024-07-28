package app.alegon.olympicsdataloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OlympicsDataLoaderApplication {
	public static void main(String[] args) {
		SpringApplication.run(OlympicsDataLoaderApplication.class, args);
	}

}
