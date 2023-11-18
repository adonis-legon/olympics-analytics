package app.alegon.olympicsdataloader.provider;

import java.util.List;

import app.alegon.olympicsdataloader.domain.OlympicEvent;
import app.alegon.olympicsdataloader.exception.OlympicEventProviderException;

public interface OlympicEventProvider {
    String getName();

    String getResource();

    List<OlympicEvent> buildEvents() throws OlympicEventProviderException;
}
