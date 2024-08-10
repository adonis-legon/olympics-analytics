package app.alegon.olympicsdataloader.dao.entity;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "participant_country")
public class ParticipantCountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "name")
    private String name;

    @Column(name = "gold_medals")
    private Integer goldMedals;

    @Column(name = "silver_medals")
    private Integer silverMedals;

    @Column(name = "bronze_medals")
    private Integer bronzeMedals;

    @ManyToOne
    @JoinColumn(name = "olympic_event_id")
    private OlympicEventEntity olympicEvent;
}
