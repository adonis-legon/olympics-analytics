package app.alegon.olympicsdataloader.exception;

public class WebScraperException extends Exception {
    public WebScraperException(String message, Throwable reason) {
        super("Error in Web Scraper. Message: " + message, reason);
    }
}