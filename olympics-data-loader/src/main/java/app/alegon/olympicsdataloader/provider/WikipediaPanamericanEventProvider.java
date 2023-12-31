package app.alegon.olympicsdataloader.provider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import app.alegon.olympicsdataloader.domain.OlympicEvent;
import app.alegon.olympicsdataloader.domain.OlympicEventType;
import app.alegon.olympicsdataloader.exception.OlympicEventProviderException;
import app.alegon.olympicsdataloader.exception.WebScraperException;

@Component
public class WikipediaPanAmericanEventProvider extends WikipediaOlympicEventProvider {
    @Override
    public String getName() {
        return "Pan American Games";
    }

    @Override
    public String getResource() {
        return "https://en.wikipedia.org/wiki/Pan_American_Games";
    }

    @Override
    protected OlympicEvent getOlympicEvent(List<Element> eventRow) throws OlympicEventProviderException {
        String eventYear = eventRow.get(1).text().trim();

        Element eventHosElement = eventRow.get(2).select("a").first();
        if (eventHosElement == null) {
            throw new OlympicEventProviderException("Missing event host name element.", null);
        }

        String eventHostname = eventHosElement.attr("title");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, u", Locale.ENGLISH);
        LocalDate eventStartDate = LocalDate.parse(eventRow.get(5).text() + ", " + eventYear,
                dateFormatter);
        LocalDate eventEndDate = LocalDate.parse(eventRow.get(6).text() + ", " + eventYear, dateFormatter);
        return new OlympicEvent(OlympicEventType.PAN_AMERICAN, eventHostname,
                eventStartDate, eventEndDate, null);
    }

    @Override
    protected String getMedalsUrl(List<Element> eventRow) throws OlympicEventProviderException {
        Element medalsElement = eventRow.get(0).select("a").first();
        if (medalsElement == null) {
            throw new OlympicEventProviderException("Missing medals element.", null);
        }

        return getResource().substring(0, getResource().indexOf("/", 8)) + medalsElement.attr("href") + "_medal_table";
    }

    @Override
    protected List<List<Element>> getEventsTableRows(String mainResource) throws IOException, WebScraperException {
        List<List<Element>> wikiTableRows = wikipediaWebScraper.getWikiTableRows(mainResource, -1);
        return wikiTableRows.subList(1, wikiTableRows.size());
    }
}
