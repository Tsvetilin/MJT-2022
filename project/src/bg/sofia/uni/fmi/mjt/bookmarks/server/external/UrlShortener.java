package bg.sofia.uni.fmi.mjt.bookmarks.server.external;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.UrlShortenerException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Service;

public interface UrlShortener extends Service {

    String shorten(String url) throws UrlShortenerException;
}
