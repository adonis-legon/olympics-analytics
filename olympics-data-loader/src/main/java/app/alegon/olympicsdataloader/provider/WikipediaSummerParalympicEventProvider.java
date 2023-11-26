package app.alegon.olympicsdataloader.provider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.springframework.stereotype.Component;

import app.alegon.olympicsdataloader.domain.OlympicEvent;
import app.alegon.olympicsdataloader.domain.OlympicEventType;
import app.alegon.olympicsdataloader.exception.OlympicEventProviderException;
import app.alegon.olympicsdataloader.exception.WebScraperException;

@Component
public class WikipediaSummerParalympicEventProvider extends WikipediaOlympicEventProvider {

    @Override
    public String getName() {
        return "Summer Paralympic Games";
    }

    @Override
    public String getResource() {
        return "https://en.wikipedia.org/wiki/Summer_Paralympic_Games";
    }

    @Override
    protected List<List<Element>> getEventsTableRows(String mainResource) throws IOException, WebScraperException {
        List<List<Element>> wikiTableRows = wikipediaWebScraper.getWikiTableRows(mainResource, -1);
        List<List<Element>> wikiTableRowsWithoutHeader = wikiTableRows.subList(2, wikiTableRows.size());

        // keep first city only, in case of a multi-city event
        List<List<Element>> wikiTableRowsWithoutHeaderAndOtherCities = new ArrayList<>();
        for (List<Element> wikiTableRowElements : wikiTableRowsWithoutHeader) {
            Element eventNumberLink = wikiTableRowElements.get(0).select("a").first();
            if (eventNumberLink != null && NumberUtils.isDigits(eventNumberLink.text())) {
                wikiTableRowsWithoutHeaderAndOtherCities.add(wikiTableRowElements);
            }
        }

        return wikiTableRowsWithoutHeaderAndOtherCities;
    }

    @Override
    protected OlympicEvent getOlympicEvent(List<Element> eventRow) throws OlympicEventProviderException {
        if (eventRow.size() < 5) {
            throw new OlympicEventProviderException("Invalid Olympic event data. Less that 5 cell on row.", null);
        }

        Element eventHostNameElement = eventRow.get(2).select("a").get(1);
        if (eventHostNameElement == null) {
            throw new OlympicEventProviderException("Missing event hostName element.", null);
        }

        String eventHostname = eventHostNameElement.attr("title");

        String eventDatesStr = "";
        Node eventDatesNode = eventRow.get(4).firstChild();
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

        List<String> eventDates = wikipediaWebScraper.getEventDates(eventDatesStr);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, u", Locale.ENGLISH);
        LocalDate eventStartDate = LocalDate.parse(eventDates.get(0), dateFormatter);
        LocalDate eventEndDate = LocalDate.parse(eventDates.get(1), dateFormatter);

        return new OlympicEvent(OlympicEventType.SUMMER_PARALYMPIC, eventHostname, eventStartDate, eventEndDate, null);
    }

    @Override
    protected String getMedalsUrl(List<Element> eventRow) throws OlympicEventProviderException {
        Element medalsElement = eventRow.get(0).select("a").first();
        if (medalsElement == null) {
            throw new OlympicEventProviderException("Missing medals element.", null);
        }

        return getResource().substring(0, getResource().indexOf("/", 8)) + medalsElement.attr("href") + "_medal_table";
    }

}
