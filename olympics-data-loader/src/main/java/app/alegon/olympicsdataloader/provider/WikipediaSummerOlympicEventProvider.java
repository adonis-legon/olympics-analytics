package app.alegon.olympicsdataloader.provider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.springframework.stereotype.Component;

import app.alegon.olympicsdataloader.domain.OlympicEvent;
import app.alegon.olympicsdataloader.domain.OlympicEventType;
import app.alegon.olympicsdataloader.exception.OlympicEventProviderException;
import app.alegon.olympicsdataloader.exception.WebScraperException;

@Component
public class WikipediaSummerOlympicEventProvider extends WikipediaOlympicEventProvider {

    @Override
    public String getName() {
        return "Summer Olympic Games";
    }

    @Override
    public String getResource() {
        return "https://en.wikipedia.org/wiki/Summer_Olympic_Games";
    }

    @Override
    protected OlympicEvent getOlympicEvent(List<Element> eventRow) throws OlympicEventProviderException {
        if (eventRow.size() < 4) {
            throw new OlympicEventProviderException("Invalid Olympic event data. Less that 4 cell on row.", null);
        }

        Element eventHostNameElement = eventRow.get(2).select("a").last();
        if (eventHostNameElement == null) {
            throw new OlympicEventProviderException("Missing event hostName element.", null);
        }

        String eventHostname = eventHostNameElement.attr("title");

        String eventDatesStr = "";
        Node eventDatesNode = eventRow.get(3).firstChild();
        if (eventDatesNode == null) {
            throw new OlympicEventProviderException("Missing event dates node.", null);
        }

        if (eventDatesNode instanceof TextNode) {
            eventDatesStr = eventDatesNode.toString();
        } else if (eventDatesNode instanceof Element) {
            eventDatesNode = eventDatesNode.firstChild();
            if (eventDatesNode == null) {
                throw new OlympicEventProviderException("Missing event dates node.", null);
            }

            eventDatesStr = eventDatesNode.toString();
        } else {
            throw new OlympicEventProviderException("Invalid event dates format", null);
        }

        List<String> eventDates = getOlympicEventDates(eventDatesStr);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, u", Locale.ENGLISH);
        LocalDate eventStartDate = LocalDate.parse(eventDates.get(0), dateFormatter);
        LocalDate eventEndDate = LocalDate.parse(eventDates.get(1), dateFormatter);

        return new OlympicEvent(OlympicEventType.SUMMER_OLYMPIC, eventHostname, eventStartDate, eventEndDate, null);
    }

    @Override
    protected String getMedalsUrl(List<Element> eventRow) throws OlympicEventProviderException {
        Element medalsElement = eventRow.get(1).select("a").first();
        if (medalsElement == null) {
            throw new OlympicEventProviderException("Missing medals element.", null);
        }

        return getResource().substring(0, getResource().indexOf("/", 8)) + medalsElement.attr("href") + "_medal_table";
    }

    public List<String> getOlympicEventDates(String wikipediaOlympicEventDate) {
        String[] dateParts = wikipediaOlympicEventDate.split(" ");
        String year = dateParts[dateParts.length - 1];

        String startDate = "";
        String endDate = "";

        // detect date/month format
        if (dateParts[0].indexOf("–", 0) > 0) {
            String[] days = dateParts[0].split("–");
            startDate = dateParts[1] + " " + days[0] + ", " + year;
            endDate = dateParts[1] + " " + days[1] + ", " + year;
        } else {
            startDate = dateParts[1] + " " + dateParts[0] + ", " + year;
            endDate = dateParts[4] + " " + dateParts[3] + ", " + year;
        }

        return Arrays.asList(startDate, endDate);
    }

    @Override
    protected List<List<Element>> getEventsTableRows(String mainResource) throws IOException, WebScraperException {
        List<List<Element>> wikiTableRows = wikipediaWebScraper.getWikiTableRows(mainResource, -1);
        return wikiTableRows.subList(2, wikiTableRows.size());
    }
}
