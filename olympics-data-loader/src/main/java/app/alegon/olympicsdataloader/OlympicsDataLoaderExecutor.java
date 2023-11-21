package app.alegon.olympicsdataloader;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import app.alegon.olympicsdataloader.business.OlympicEventService;
import app.alegon.olympicsdataloader.config.OlympicEventApplicationConfig;
import app.alegon.olympicsdataloader.domain.OlympicEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("!test")
@Component
public class OlympicsDataLoaderExecutor implements CommandLineRunner {
    @Autowired
    private OlympicEventService olympicEventService;

    @Autowired
    private OlympicEventApplicationConfig olympicEventApplicationConfig;

    @Override
    public void run(String... args) throws Exception {
        try {
            for (String olympicEventType : olympicEventApplicationConfig.getOlympicEvents()) {
                log.info("Processing Olympic event type: " + olympicEventType + "...");

                List<OlympicEvent> olympicEvents = olympicEventService.loadEvents(olympicEventType);
                olympicEventService.storeEvents(olympicEvents);

                log.info("Finished processing Olympic event type: " + olympicEventType);
            }

        } catch (Exception e) {
            log.error("Error in Olympic data loader application. Message: " + e.getMessage(), e);
        }
    }

}
