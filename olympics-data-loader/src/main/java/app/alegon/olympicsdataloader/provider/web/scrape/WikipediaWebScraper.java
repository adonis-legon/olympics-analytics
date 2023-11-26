package app.alegon.olympicsdataloader.provider.web.scrape;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import app.alegon.olympicsdataloader.exception.OlympicEventProviderException;
import app.alegon.olympicsdataloader.exception.WebScraperException;

@Component
public class WikipediaWebScraper {
    public List<List<Element>> getWikiTableRows(String mainUrl, int tableIndex)
            throws IOException, WebScraperException {
        Document mainEventDocument = Jsoup.connect(mainUrl).get();
        Elements allWikiTableElements = mainEventDocument.getElementsByClass("wikitable");

        Element wikiTableElement = tableIndex >= 0 ? allWikiTableElements.get(tableIndex) : allWikiTableElements.last();
        if (wikiTableElement == null) {
            throw new WebScraperException("Missing wikitable element", null);
        }

        List<List<Element>> mainTable = wikiTableElement.select("tbody tr")
                .stream().map(row -> {
                    return row.select("td").stream().collect(Collectors.toList());
                }).collect(Collectors.toList());

        return mainTable;
    }

    public List<List<Element>> getWikiTableRows(String mainUrl) throws IOException, WebScraperException {
        return getWikiTableRows(mainUrl, -1);
    }

    public List<List<Element>> getMedalsTable(String eventMedalsUrl, boolean skipTableHeader, boolean skipTableFooter)
            throws IOException, OlympicEventProviderException {
        String[] metalsTableHeaders = { "Gold", "Silver", "Bronze" };

        Document participantCountriesDocument = Jsoup.connect(eventMedalsUrl).get();
        Optional<Element> medalsTableElement = participantCountriesDocument.getElementsByClass("wikitable").stream()
                .filter(t -> Arrays.stream(metalsTableHeaders).allMatch(t.select("th").text()::contains)).findFirst();
        if (medalsTableElement.isEmpty()) {
            throw new OlympicEventProviderException("No medals table found on olympic event: " + eventMedalsUrl, null);
        }

        List<List<Element>> medalsTable = medalsTableElement.get().select("tbody tr").stream()
                .map(row -> row.select("td,th").stream().collect(Collectors.toList())).collect(Collectors.toList());

        if (skipTableHeader) {
            medalsTable = medalsTable.subList(1, medalsTable.size());
        }

        if (skipTableFooter) {
            medalsTable = medalsTable.subList(0, medalsTable.size() - 1);
        }

        return medalsTable;
    }

    public String sanitizeMedalCount(String medalCount) {
        int pos = medalCount.indexOf("/", 0);
        pos = pos > 0 ? pos : medalCount.length();

        return medalCount.substring(0, pos);
    }

    public List<String> getEventDates(String eventDateWithYear) {
        String[] dateParts = eventDateWithYear.split(" ");
        String year = dateParts[dateParts.length - 1];

        return getEventDatesFromDateParts(dateParts, year);
    }

    public List<String> getEventDates(String eventDateWithoutYear, String year) {
        String[] dateParts = eventDateWithoutYear.split(" ");
        return getEventDatesFromDateParts(dateParts, year);
    }

    private List<String> getEventDatesFromDateParts(String[] dateParts, String year) {
        String startDate = "";
        String endDate = "";

        // detect start and end days in same month format
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
}
