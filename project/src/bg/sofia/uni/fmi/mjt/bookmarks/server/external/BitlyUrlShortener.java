package bg.sofia.uni.fmi.mjt.bookmarks.server.external;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.UrlShortenerException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BitlyUrlShortener implements UrlShortener {

    private static final String BITLY_API_URL = "https://api-ssl.bitly.com/v4/shorten";
    private static final String AUTH_HEADER_VALUE = "INSERT_API_KEY_HERE";
    private static final String REQUEST_BODY_PATTERN = "{\"long_url\":\"%s\"}";

    private final Gson gson;
    private final HttpClient client;
    private final String authToken;

    public BitlyUrlShortener(HttpClient client, String authToken) {
        this.client = client;
        this.gson = new Gson();
        this.authToken = authToken;
    }

    public BitlyUrlShortener(HttpClient client) {
        this(client, AUTH_HEADER_VALUE);
    }

    @Override
    public String shorten(String url) throws UrlShortenerException {
        Nullable.throwIfNull(url);

        String body = String.format(REQUEST_BODY_PATTERN, url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BITLY_API_URL))
            .setHeader("Content-Type", "application/json")
            .setHeader("Authorization", authToken)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new UrlShortenerException("Unable to complete the request.", e);
        }

        if (response.statusCode() != HttpURLConnection.HTTP_CREATED &&
            response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new UrlShortenerException("Cannot create shortened url.");
        }

        ShortUrlResult shorten = gson.fromJson(response.body(), ShortUrlResult.class);

        return shorten.link();
    }
}
