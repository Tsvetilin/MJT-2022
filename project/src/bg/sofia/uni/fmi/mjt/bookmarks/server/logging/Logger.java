package bg.sofia.uni.fmi.mjt.bookmarks.server.logging;

import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Service;

public interface Logger extends Service {
    void logInfo(String message);

    void logWarning(String message);

    void logError(String message);

    void logException(Exception e, String id);

    void log(Severity severity, String message);
}
