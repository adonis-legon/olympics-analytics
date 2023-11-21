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
import app.alegon.olympicsdataloader.provider.WikipediaSummerOlympicEventProvider;

@ActiveProfiles("test")
@SpringBootTest
class OlympicsDataLoaderApplicationTests {

	@Autowired
	private OlympicEventService olympicEventService;

	@Autowired
	private WikipediaSummerOlympicEventProvider wikipediaSummerOlympicEventProvider;

	@Test
	void onTheFirstPanamGamesTheWinnerAndHostMustBeTheSame() {
		List<ParticipantCountry> participantCountries = new ArrayList<>();

		OlympicEvent panamericanGames = new OlympicEvent(OlympicEventType.PANAMERICAN, "Argentina",
				LocalDate.of(1951, 2, 5), LocalDate.of(1951, 3, 9), null);

		participantCountries.add(new ParticipantCountry(2, "United States of America", 46, 34, 21, panamericanGames));
		participantCountries.add(new ParticipantCountry(1, "Argentina", 63, 43, 36, panamericanGames));
		participantCountries.add(new ParticipantCountry(3, "Chile", 8, 19, 12, panamericanGames));
		panamericanGames.setParticipantCountries(participantCountries);

		List<ParticipantCountry> topParticipantCountries = panamericanGames.getTop(5);

		assertTrue(topParticipantCountries.get(0).getName().equals(panamericanGames.getHostCity()));
	}

	@Test
	void whenLoadingPanamericanEventsFromResourcesOnConfigItShouldWork() {
		assertDoesNotThrow(() -> olympicEventService.loadEvents("Panamerican Games"));
	}

	@Test
	void whenLoadingSummerOlympicEventsFromResourcesOnConfigItShouldWork() {
		assertDoesNotThrow(() -> olympicEventService.loadEvents("Summer Olympic Games"));
	}

	@Test
	void whenWikipediaOlympicEventDateFormatParseShouldBeCorrect() {
		List<String> eventDatesFormat1 = wikipediaSummerOlympicEventProvider.getOlympicEventDates("12–21 June 1896");
		assertTrue(eventDatesFormat1.get(0).equalsIgnoreCase("June 12, 1896")
				&& eventDatesFormat1.get(1).equalsIgnoreCase("June 21, 1896"));

		List<String> eventDatesFormat2 = wikipediaSummerOlympicEventProvider
				.getOlympicEventDates("1 June - 1 July 1900");
		assertTrue(eventDatesFormat2.get(0).equalsIgnoreCase("June 1, 1900")
				&& eventDatesFormat2.get(1).equalsIgnoreCase("July 1, 1900"));
	}

	@Test
	void whenStoringOlympicEventsItShouldWork() {
		List<OlympicEvent> olympicEvents = new ArrayList<>();

		olympicEvents.add(new OlympicEvent(OlympicEventType.PANAMERICAN, "Santiago", LocalDate.of(2023, 10, 20),
				LocalDate.of(2023, 11, 5), null));
		olympicEvents.add(new OlympicEvent(OlympicEventType.PANAMERICAN, "Buenos Aires", LocalDate.of(1951, 2, 25),
				LocalDate.of(1951, 3, 9), null));

		assertDoesNotThrow(() -> olympicEventService.storeEvents(olympicEvents));
	}

	@Test
	void whenStoringParticipantCountriesOfAnOlympicEventItShouldWork() {
		List<OlympicEvent> olympicEvents = new ArrayList<>();

		List<ParticipantCountry> participantCountries = new ArrayList<>();

		OlympicEvent panamericanGames = new OlympicEvent(OlympicEventType.PANAMERICAN, "Argentina",
				LocalDate.of(1951, 2, 5), LocalDate.of(1951, 3, 9), null);

		participantCountries.add(new ParticipantCountry(2, "United States of America", 46, 34, 21, panamericanGames));
		participantCountries.add(new ParticipantCountry(1, "Argentina", 63, 43, 36, panamericanGames));
		participantCountries.add(new ParticipantCountry(3, "Chile", 8, 19, 12, panamericanGames));
		panamericanGames.setParticipantCountries(participantCountries);

		olympicEvents.add(panamericanGames);

		assertDoesNotThrow(() -> olympicEventService.storeEvents(olympicEvents));
	}
}
