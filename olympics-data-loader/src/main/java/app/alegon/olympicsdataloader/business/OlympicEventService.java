package app.alegon.olympicsdataloader.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.alegon.olympicsdataloader.dao.entity.OlympicEventEntity;
import app.alegon.olympicsdataloader.dao.entity.ParticipantCountryEntity;
import app.alegon.olympicsdataloader.dao.repository.OlympicEventRepository;
import app.alegon.olympicsdataloader.dao.repository.ParticipantCountryRepository;
import app.alegon.olympicsdataloader.domain.OlympicEvent;
import app.alegon.olympicsdataloader.domain.ParticipantCountry;
import app.alegon.olympicsdataloader.exception.OlympicEventProviderException;
import app.alegon.olympicsdataloader.exception.OlympicEventServiceException;
import app.alegon.olympicsdataloader.provider.OlympicEventProvider;

@Service
public class OlympicEventService {
    @Autowired
    private List<OlympicEventProvider> olympicEventProviders;

    @Autowired
    private OlympicEventRepository olympicEventRepository;

    @Autowired
    private ParticipantCountryRepository participantCountryRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<OlympicEvent> loadEvents(String olympicEventName) throws OlympicEventProviderException {
        OlympicEventProvider olympicEventProvider = olympicEventProviders.stream()
                .filter(p -> p.getName().equals(olympicEventName)).findFirst().get();
        if (olympicEventProvider == null) {
            throw new OlympicEventProviderException(
                    "There is no Olympic event provider with the name: " + olympicEventName, null);
        }

        List<OlympicEvent> olympicEvents = olympicEventProvider.buildEvents();
        Collections.sort(olympicEvents, (e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()));

        return olympicEvents;
    }

    public void storeEvents(List<OlympicEvent> olympicEvents) throws OlympicEventServiceException {
        try {
            Collections.sort(olympicEvents, (o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
            olympicEvents.forEach(olympicEvent -> storeEvent(olympicEvent));
        } catch (Exception e) {
            throw new OlympicEventServiceException("Error storing events. Message: " + e.getMessage(), e);
        }
    }

    @Transactional
    private void storeEvent(OlympicEvent olympicEvent) {
        OlympicEventEntity olympicEventEntity = modelMapper.map(olympicEvent, OlympicEventEntity.class);
        List<OlympicEventEntity> storedOlympicEventEntities = olympicEventRepository
                .findByHostCityAndStartDateAndEndDate(olympicEventEntity.getHostCity(),
                        olympicEventEntity.getStartDate(), olympicEventEntity.getEndDate());

        if (storedOlympicEventEntities.size() == 0) {
            olympicEventEntity = olympicEventRepository.save(olympicEventEntity);
        } else {
            olympicEventEntity = storedOlympicEventEntities.get(0);
        }

        if (olympicEvent.getParticipantCountries() != null) {
            storeParticipants(olympicEvent.getParticipantCountries(), olympicEventEntity);
        }
    }

    private void storeParticipants(List<ParticipantCountry> participantCountries,
            OlympicEventEntity olympicEventEntity) {
        List<ParticipantCountryEntity> participantCountryEntities = participantCountries.stream()
                .map(participantCountry -> modelMapper.map(participantCountry, ParticipantCountryEntity.class))
                .collect(Collectors.toList());

        List<ParticipantCountryEntity> participantCountryEntitiesToStore = new ArrayList<>();
        for (ParticipantCountryEntity participantCountryEntity : participantCountryEntities) {
            participantCountryEntity.getOlympicEvent().setId(olympicEventEntity.getId());

            List<ParticipantCountryEntity> storedParticipantCountryEntities = participantCountryRepository
                    .findByNameAndOlympicEvent(participantCountryEntity.getName(),
                            participantCountryEntity.getOlympicEvent());

            if (storedParticipantCountryEntities.size() == 0) {
                participantCountryEntitiesToStore.add(participantCountryEntity);
            }
        }

        if (participantCountryEntitiesToStore.size() > 0) {
            participantCountryRepository.saveAll(participantCountryEntitiesToStore);
        }
    }
}
