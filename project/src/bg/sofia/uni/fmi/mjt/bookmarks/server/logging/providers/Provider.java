package bg.sofia.uni.fmi.mjt.bookmarks.server.logging.providers;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.LoggerOperationException;

public interface Provider {
    void write(String str) throws LoggerOperationException;
}
