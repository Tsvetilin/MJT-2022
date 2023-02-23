package bg.sofia.uni.fmi.mjt.bookmarks.server.services;

import bg.sofia.uni.fmi.mjt.bookmarks.server.DIContainer;
import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.InvalidBookmarkException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.StopWordsException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.UrlShortenerException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.external.ShortUrlResult;
import bg.sofia.uni.fmi.mjt.bookmarks.server.external.UrlShortener;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Stopwords;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookmarkServiceTest {

    HttpClient client = mock(HttpClient.class);
    HttpResponse<String> response = mock(HttpResponse.class);
    UrlShortener shortener = mock(UrlShortener.class);
    User user = mock(User.class);
    Group group = mock(Group.class);
    ShortUrlResult result = new ShortUrlResult("now", "id", "url");

    BookmarksService service = new DefaultBookmarksService(client);

    @BeforeEach
    void setup() throws IOException, InterruptedException, StopWordsException {
        Stopwords.load();
        DIContainer.clear();
        DIContainer.register(UrlShortener.class, shortener);
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(new Gson().toJson(result));
    }

    @Test
    void testValidateUrl() throws InvalidBookmarkException {
        assertTrue(service.validateUrl("https://google.com"), "Should be valid when page is reachable.");
    }

    @Test
    void testGenerateBookmark() throws InvalidBookmarkException {
        assertEquals("https://google.com", service.generateBookmark("https://google.com", group, false, user).getUrl(),
            "asd");
    }

    @Test
    void testGenerateBookmarkShort() throws InvalidBookmarkException, UrlShortenerException {
        when(shortener.shorten("https://google.com")).thenReturn("asd");
        assertEquals("asd", service.generateBookmark("https://google.com", group, true, user).getShortened(),
            "asd");
    }
}