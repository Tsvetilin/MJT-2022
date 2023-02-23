package bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions;

import java.io.IOException;

public class StopWordsException extends Exception {
    public StopWordsException(Exception e) {
        super(e);
    }

    public StopWordsException(String message) {
        super(message);
    }
}
