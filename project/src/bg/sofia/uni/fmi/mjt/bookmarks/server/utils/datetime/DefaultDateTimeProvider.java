package bg.sofia.uni.fmi.mjt.bookmarks.server.utils.datetime;

import java.time.LocalDateTime;

public class DefaultDateTimeProvider implements DateTimeProvider {

    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}
