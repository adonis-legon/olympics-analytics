package app.alegon.olympicsdataloader.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantCountry {
    private int rank;

    private String name;

    private int goldMedals;

    private int silverMedals;

    private int bronzeMedals;

    private OlympicEvent olympicEvent;

    public int getTotalMedals() {
        return goldMedals + silverMedals + bronzeMedals;
    }
}
