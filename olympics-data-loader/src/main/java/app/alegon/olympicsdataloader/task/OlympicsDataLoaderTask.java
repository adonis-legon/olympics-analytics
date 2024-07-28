package app.alegon.olympicsdataloader.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import app.alegon.olympicsdataloader.business.OlympicEventService;
import app.alegon.olympicsdataloader.config.OlympicEventApplicationConfig;
import app.alegon.olympicsdataloader.domain.OlympicEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OlympicsDataLoaderTask {
    @Autowired
    private OlympicEventService olympicEventService;

    @Autowired
    private OlympicEventApplicationConfig olympicEventApplicationConfig;

    @Scheduled(cron = "${app.event-loader.schedule}")
    public void loadEvents() {
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
