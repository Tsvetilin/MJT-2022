package bg.sofia.uni.fmi.mjt.bookmarks.server.utils.datetime;

import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Service;

import java.time.LocalDateTime;

public interface DateTimeProvider extends Service {
    LocalDateTime getCurrentTime();
}
