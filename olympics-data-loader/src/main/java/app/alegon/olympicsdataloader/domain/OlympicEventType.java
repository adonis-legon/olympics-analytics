package app.alegon.olympicsdataloader.domain;

public enum OlympicEventType {
    SUMMER_OLYMPIC("Summer Olympic", 1),
    PANAMERICAN("Panamerican", 2);

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
