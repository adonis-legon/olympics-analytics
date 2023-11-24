package app.alegon.olympicsdataloader.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

import app.alegon.olympicsdataloader.domain.OlympicEvent;
import app.alegon.olympicsdataloader.domain.ParticipantCountry;
import app.alegon.olympicsdataloader.exception.OlympicEventProviderException;
import app.alegon.olympicsdataloader.exception.WebScraperException;
import app.alegon.olympicsdataloader.provider.web.scrape.WikipediaWebScraper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class WikipediaOlympicEventProvider implements OlympicEventProvider {
    private final int EVENTS_PARALLEL_PROCESSING = 4;

    @Autowired
    protected WikipediaWebScraper wikipediaWebScraper;

    @Override
    public List<OlympicEvent> buildEvents() throws OlympicEventProviderException {
        List<OlympicEvent> olympicEvents = new ArrayList<>();
        ForkJoinPool eventsProcessingPool = new ForkJoinPool(EVENTS_PARALLEL_PROCESSING);

        try {
            final List<List<Element>> eventsTableRows = getEventsTableRows(getResource());

            eventsProcessingPool.submit(() -> eventsTableRows.parallelStream().forEach(eventRow -> {
                String eventMedalsUrl = "missing";
                OlympicEvent olympicEvent = new OlympicEvent();

                try {
                    olympicEvent = getOlympicEvent(eventRow);
                    eventMedalsUrl = getMedalsUrl(eventRow);

                    log.info("Processing event: " + olympicEvent.toString() + "...");

                    List<List<Element>> medalsTableRows = wikipediaWebScraper.getMedalsTable(eventMedalsUrl, true,
                            true);
                    List<ParticipantCountry> participantCountries = new ArrayList<>();

                    int currentRank = 1;
                    for (List<Element> participantCountryRow : medalsTableRows) {
                        int participantRank = currentRank;
                        int dataOffset = 1;

                        if (participantCountryRow.size() == 6) {
                            participantRank = Integer.parseInt(participantCountryRow.get(0).text());
                            currentRank = participantRank;
                            dataOffset = 0;
                        }

                        Element participantCountryElement = participantCountryRow.get(1 - dataOffset).select("a")
                                .first();
                        if (participantCountryElement == null) {
                            throw new OlympicEventProviderException("Missing participant country element.", null);
                        }
                        String participantCountryName = participantCountryElement.text();
                        int participantGoldMedals = Integer.parseInt(wikipediaWebScraper
                                .sanitizeMedalCount(participantCountryRow.get(2 - dataOffset).text()));
                        int participantSilverMedals = Integer.parseInt(wikipediaWebScraper
                                .sanitizeMedalCount(participantCountryRow.get(3 - dataOffset).text()));
                        int participantBronzeMedals = Integer.parseInt(wikipediaWebScraper
                                .sanitizeMedalCount(participantCountryRow.get(4 - dataOffset).text()));

                        participantCountries.add(new ParticipantCountry(participantRank, participantCountryName,
                                participantGoldMedals, participantSilverMedals, participantBronzeMedals, olympicEvent));
                    }

                    olympicEvent.setParticipantCountries(participantCountries);
                    olympicEvents.add(olympicEvent);
                } catch (HttpStatusException statusEx) {
                    if (statusEx.getStatusCode() == java.net.HttpURLConnection.HTTP_NOT_FOUND) {
                        log.warn("There is no medals table for the event:" + olympicEvent.toString());
                    } else {
                        log.error("Error scraping event page: " + eventMedalsUrl, statusEx);
                    }
                } catch (OlympicEventProviderException olympicEx) {
                    log.error(olympicEx.getMessage());
                } catch (Exception e) {
                    log.error("Error scraping event page: " + eventMedalsUrl, e);
                } finally {
                    log.info("Finished processing event: " + olympicEvent.toString());
                }
            })).get();
        } catch (Exception e) {
            throw new OlympicEventProviderException(
                    "Error scraping resource: " + getResource() + ". Message: " + e.getMessage(), e);
        } finally {
            eventsProcessingPool.shutdown();
        }

        sortEvents(olympicEvents);
        return olympicEvents;
    }

    private void sortEvents(List<OlympicEvent> olympicEvents) {
        Collections.sort(olympicEvents, (e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()));
    }

    protected abstract List<List<Element>> getEventsTableRows(String mainResource)
            throws IOException, WebScraperException;

    protected abstract OlympicEvent getOlympicEvent(List<Element> eventRow) throws OlympicEventProviderException;

    protected abstract String getMedalsUrl(List<Element> eventRow) throws OlympicEventProviderException;
}
