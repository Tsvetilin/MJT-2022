package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class NewsFeedClientException extends Exception {

    private String errorCode;
    private String errorMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public NewsFeedClientException(String code, String message) {
        super(code + ": " + message);
        this.errorCode = code;
        this.errorMessage = message;
    }

    public NewsFeedClientException(String message) {
        super(message);
    }

    public NewsFeedClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
