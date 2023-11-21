package app.alegon.olympicsdataloader.exception;

public class OlympicEventServiceException extends Exception {
    public OlympicEventServiceException(String message, Throwable reason) {
        super("Error in Olympic Event service. Message: " + message, reason);
    }
}
