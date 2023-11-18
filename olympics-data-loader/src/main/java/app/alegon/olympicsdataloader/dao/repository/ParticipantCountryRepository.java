package app.alegon.olympicsdataloader.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.alegon.olympicsdataloader.dao.entity.OlympicEventEntity;
import app.alegon.olympicsdataloader.dao.entity.ParticipantCountryEntity;

public interface ParticipantCountryRepository extends JpaRepository<ParticipantCountryEntity, Integer> {
    List<ParticipantCountryEntity> findByNameAndOlympicEvent(String name, OlympicEventEntity olympicEventEntity);
}