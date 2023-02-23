package bg.sofia.uni.fmi.mjt.bookmarks.server.logging.providers;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.LoggerOperationException;

public interface FileProvider extends Provider {

    void writeError(Exception e, String traceId) throws LoggerOperationException;
}
