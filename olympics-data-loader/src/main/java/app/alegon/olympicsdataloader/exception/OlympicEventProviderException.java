package app.alegon.olympicsdataloader.exception;

public class OlympicEventProviderException extends Exception {
    public OlympicEventProviderException(String message, Throwable reason) {
        super("Error in Olympic Event provider. Message: " + message, reason);
    }
}
