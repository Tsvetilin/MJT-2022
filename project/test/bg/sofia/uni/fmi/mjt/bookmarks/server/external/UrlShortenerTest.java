package bg.sofia.uni.fmi.mjt.bookmarks.server.external;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.UrlShortenerException;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlShortenerTest {

    HttpClient client = mock(HttpClient.class);
    HttpResponse<String> response = mock(HttpResponse.class);
    UrlShortener shortener = new BitlyUrlShortener(client);
    ShortUrlResult result = new ShortUrlResult("now", "id", "url");

    @BeforeEach
    void setup() throws IOException, InterruptedException {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        when(response.body()).thenReturn(new Gson().toJson(result));
    }

    @Test
    void testSuccessRequest() throws UrlShortenerException {
        when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_CREATED);
        var res = shortener.shorten("url");
        assertEquals("url", res, "Invalid url returned.");
    }

    @Test
    void testFailRequest() {
        when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_CONFLICT);
        assertThrows(UrlShortenerException.class, () -> shortener.shorten("url"));
    }

    @Test
    void testFailRequestForClient() throws IOException, InterruptedException {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(
            IOException.class);
        assertThrows(UrlShortenerException.class, () -> shortener.shorten("url"));
    }

}