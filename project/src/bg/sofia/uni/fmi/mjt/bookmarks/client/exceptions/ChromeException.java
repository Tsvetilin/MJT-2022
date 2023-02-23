package bg.sofia.uni.fmi.mjt.bookmarks.client.exceptions;

public class ChromeException extends Exception {
    public ChromeException(String message) {
        super(message);
    }

    public ChromeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChromeException(Throwable cause) {
        super(cause);
    }
}
