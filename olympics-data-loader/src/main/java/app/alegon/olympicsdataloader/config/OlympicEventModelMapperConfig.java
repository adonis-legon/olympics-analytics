package app.alegon.olympicsdataloader.config;

import java.sql.Date;
import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.modelmapper.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import app.alegon.olympicsdataloader.dao.entity.OlympicEventEntity;
import app.alegon.olympicsdataloader.domain.OlympicEvent;
import app.alegon.olympicsdataloader.domain.OlympicEventType;

@Configuration
public class OlympicEventModelMapperConfig {
        @Bean
        public ModelMapper modelMapper() {
                ModelMapper modelMapper = new ModelMapper();
                Converter<OlympicEventType, Integer> olympicEventTypeToInConverter = c -> c.getSource().getRank();
                Converter<LocalDate, Date> localDateToDateConverter = c -> Date.valueOf(c.getSource());

                modelMapper.typeMap(OlympicEvent.class,
                                OlympicEventEntity.class).addMappings(mapper -> {
                                        mapper.using(olympicEventTypeToInConverter).map(OlympicEvent::getEventType,
                                                        OlympicEventEntity::setEventType);
                                        mapper.map(src -> src.getHostCity(), OlympicEventEntity::setHostCity);
                                        mapper.using(localDateToDateConverter).map(OlympicEvent::getStartDate,
                                                        OlympicEventEntity::setStartDate);
                                        mapper.using(localDateToDateConverter).map(OlympicEvent::getEndDate,
                                                        OlympicEventEntity::setEndDate);
                                });

                return modelMapper;
        }

}
