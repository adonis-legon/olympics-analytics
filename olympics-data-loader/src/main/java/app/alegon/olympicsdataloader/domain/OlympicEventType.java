package app.alegon.olympicsdataloader.domain;

public enum OlympicEventType {
    SUMMER_OLYMPIC("Summer Olympic", 1),
    PAN_AMERICAN("Pan American", 2),
    PARA_PAN_AMERICAN("Para Pan American", 3),
    SUMMER_PARALYMPIC("Summer Paralympic", 4);

    private final String value;

    private final int rank;

    OlympicEventType(String value, int rank) {
        this.value = value;
        this.rank = rank;
    }

    public String getValue() {
        return value;
    }

    public int getRank() {
        return rank;
    }
}
