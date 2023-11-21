package app.alegon.olympicsdataloader.provider.web.scrape;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import app.alegon.olympicsdataloader.exception.OlympicEventProviderException;
import app.alegon.olympicsdataloader.exception.WebScraperException;

@Component
public class WikipediaWebScraper {
    public List<List<Element>> getMainTable(String mainUrl) throws IOException, WebScraperException {
        Document mainEventDocument = Jsoup.connect(mainUrl).get();

        Element wikiTableElement = mainEventDocument.getElementsByClass("wikitable").last();
        if (wikiTableElement == null) {
            throw new WebScraperException("Missing wikitable element", null);
        }

        List<List<Element>> mainTable = wikiTableElement.select("tbody tr")
                .stream().map(row -> {
                    return row.select("td").stream().collect(Collectors.toList());
                }).collect(Collectors.toList());

        return mainTable;
    }

    public String getEventMedalsUrl(String mainUrl, String eventUrl) {
        return mainUrl.substring(0, mainUrl.indexOf("/", 8)) + eventUrl + "_medal_table";
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
}
