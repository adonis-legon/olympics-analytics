package app.alegon.olympicsdataloader.dao.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.alegon.olympicsdataloader.dao.entity.OlympicEventEntity;

public interface OlympicEventRepository extends JpaRepository<OlympicEventEntity, Integer> {
    List<OlympicEventEntity> findByHostCityAndStartDateAndEndDate(String hostCity, Date startDate, Date endDate);
}