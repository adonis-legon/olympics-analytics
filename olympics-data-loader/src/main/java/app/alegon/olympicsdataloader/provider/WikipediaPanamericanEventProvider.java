package app.alegon.olympicsdataloader.provider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import app.alegon.olympicsdataloader.domain.OlympicEvent;
import app.alegon.olympicsdataloader.domain.OlympicEventType;
import app.alegon.olympicsdataloader.exception.OlympicEventProviderException;

@Component
public class WikipediaPanamericanEventProvider extends WikipediaOlympicEventProvider {
    @Override
    public String getName() {
        return "Panamerican Games";
    }

    @Override
    public String getResource() {
        return "https://en.wikipedia.org/wiki/Pan_American_Games";
    }

    @Override
    public OlympicEvent getOlympicEvent(List<Element> eventRow) throws OlympicEventProviderException {
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
        return new OlympicEvent(OlympicEventType.PANAMERICAN, eventHostname,
                eventStartDate, eventEndDate, null);
    }

    @Override
    public String getMedalsUrl(List<Element> eventRow) throws OlympicEventProviderException {
        Element medalsElement = eventRow.get(0).select("a").first();
        if (medalsElement == null) {
            throw new OlympicEventProviderException("Missing medals element.", null);
        }

        return wikipediaWebScraper.getEventMedalsUrl(getResource(), medalsElement.attr("href"));
    }

    @Override
    public List<List<Element>> removeMainTableHeader(List<List<Element>> mainTableRows) {
        return mainTableRows.subList(1, mainTableRows.size());
    }
}
