package app.alegon.olympicsdataloader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import app.alegon.olympicsdataloader.business.OlympicEventService;
import app.alegon.olympicsdataloader.domain.OlympicEvent;
import app.alegon.olympicsdataloader.domain.OlympicEventType;
import app.alegon.olympicsdataloader.domain.ParticipantCountry;
import app.alegon.olympicsdataloader.provider.web.scrape.WikipediaWebScraper;

@ActiveProfiles("test")
@SpringBootTest
class OlympicsDataLoaderApplicationTests {

	@Autowired
	private OlympicEventService olympicEventService;

	@Autowired
	private WikipediaWebScraper wikipediaWebScraper;

	@Test
	void onTheFirstPanAmericanGamesTheWinnerAndHostMustBeTheSame() {
		List<ParticipantCountry> participantCountries = new ArrayList<>();

		OlympicEvent panamericanGames = new OlympicEvent(OlympicEventType.PAN_AMERICAN, "Argentina",
				LocalDate.of(1951, 2, 5), LocalDate.of(1951, 3, 9), null);

		participantCountries.add(new ParticipantCountry(2, "United States of America", 46, 34, 21, panamericanGames));
		participantCountries.add(new ParticipantCountry(1, "Argentina", 63, 43, 36, panamericanGames));
		participantCountries.add(new ParticipantCountry(3, "Chile", 8, 19, 12, panamericanGames));
		panamericanGames.setParticipantCountries(participantCountries);

		List<ParticipantCountry> topParticipantCountries = panamericanGames.getTop(5);

		assertTrue(topParticipantCountries.get(0).getName().equals(panamericanGames.getHostCity()));
	}

	@Test
	void whenLoadingPanAmericanEventsFromResourcesOnConfigItShouldWork() {
		assertDoesNotThrow(() -> olympicEventService.loadEvents("Pan American Games"));
	}

	@Test
	void whenLoadingSummerOlympicEventsFromResourcesOnConfigItShouldWork() {
		assertDoesNotThrow(() -> olympicEventService.loadEvents("Summer Olympic Games"));
	}

	@Test
	void whenLoadingParapanAmericanEventsFromResourcesOnConfigItShouldWork() {
		assertDoesNotThrow(() -> olympicEventService.loadEvents("Parapan American Games"));
	}

	@Test
	void whenLoadingSummerParalympicEventsFromResourcesOnConfigItShouldWork() {
		assertDoesNotThrow(() -> olympicEventService.loadEvents("Summer Paralympic Games"));
	}

	@Test
	void whenWikipediaOlympicEventDateFormatsParsedItShouldBeCorrect() {
		List<String> eventDatesFormat1 = wikipediaWebScraper.getEventDates("12–21 June 1896");
		assertTrue(eventDatesFormat1.get(0).equalsIgnoreCase("June 12, 1896")
				&& eventDatesFormat1.get(1).equalsIgnoreCase("June 21, 1896"));

		List<String> eventDatesFormat2 = wikipediaWebScraper
				.getEventDates("1 June - 1 July 1900");
		assertTrue(eventDatesFormat2.get(0).equalsIgnoreCase("June 1, 1900")
				&& eventDatesFormat2.get(1).equalsIgnoreCase("July 1, 1900"));

		List<String> eventDatesFormat3 = wikipediaWebScraper.getEventDates("4–11 November", "1999");
		assertTrue(eventDatesFormat3.get(0).equalsIgnoreCase("November 4, 1999")
				&& eventDatesFormat3.get(1).equalsIgnoreCase("November 11, 1999"));

		List<String> eventDatesFormat4 = wikipediaWebScraper
				.getEventDates("23 August – 1 September", "2019");
		assertTrue(eventDatesFormat4.get(0).equalsIgnoreCase("August 23, 2019")
				&& eventDatesFormat4.get(1).equalsIgnoreCase("September 1, 2019"));
	}

	@Test
	void whenStoringOlympicEventsItShouldWork() {
		List<OlympicEvent> olympicEvents = new ArrayList<>();

		olympicEvents.add(new OlympicEvent(OlympicEventType.PAN_AMERICAN, "Santiago", LocalDate.of(2023, 10, 20),
				LocalDate.of(2023, 11, 5), null));
		olympicEvents.add(new OlympicEvent(OlympicEventType.PAN_AMERICAN, "Buenos Aires", LocalDate.of(1951, 2, 25),
				LocalDate.of(1951, 3, 9), null));

		assertDoesNotThrow(() -> olympicEventService.storeEvents(olympicEvents));
	}

	@Test
	void whenStoringParticipantCountriesOfAnOlympicEventItShouldWork() {
		List<OlympicEvent> olympicEvents = new ArrayList<>();

		List<ParticipantCountry> participantCountries = new ArrayList<>();

		OlympicEvent panamericanGames = new OlympicEvent(OlympicEventType.PAN_AMERICAN, "Argentina",
				LocalDate.of(1951, 2, 5), LocalDate.of(1951, 3, 9), null);

		participantCountries.add(new ParticipantCountry(2, "United States of America", 46, 34, 21, panamericanGames));
		participantCountries.add(new ParticipantCountry(1, "Argentina", 63, 43, 36, panamericanGames));
		participantCountries.add(new ParticipantCountry(3, "Chile", 8, 19, 12, panamericanGames));
		panamericanGames.setParticipantCountries(participantCountries);

		olympicEvents.add(panamericanGames);

		assertDoesNotThrow(() -> olympicEventService.storeEvents(olympicEvents));
	}
}
