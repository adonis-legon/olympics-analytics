package app.alegon.olympicsdataloader.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OlympicEvent {
    private OlympicEventType eventType;

    private String hostCity;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<ParticipantCountry> participantCountries = new ArrayList<>();

    public void updateRanking() {
        Collections.sort(participantCountries, (p1, p2) -> Integer.compare(p1.getRank(), p2.getRank()));
    }

    public List<ParticipantCountry> getTop(int amount) {
        updateRanking();

        int toIndex = (amount <= participantCountries.size() ? amount : participantCountries.size()) - 1;
        return participantCountries.subList(0, toIndex);
    }

    @Override
    public String toString() {
        return "{" +
                " eventType='" + getEventType() + "'" +
                ", hostCity='" + getHostCity() + "'" +
                ", startDate='" + getStartDate() + "'" +
                ", endDate='" + getEndDate() + "'" +
                "}";
    }
}
