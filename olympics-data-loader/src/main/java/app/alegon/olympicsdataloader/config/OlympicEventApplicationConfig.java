package app.alegon.olympicsdataloader.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class OlympicEventApplicationConfig {

    private List<String> olympicEvents;
}
