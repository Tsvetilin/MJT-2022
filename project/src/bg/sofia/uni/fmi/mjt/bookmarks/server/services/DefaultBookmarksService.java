package bg.sofia.uni.fmi.mjt.bookmarks.server.services;

import bg.sofia.uni.fmi.mjt.bookmarks.server.DIContainer;
import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.InvalidBookmarkException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.StopWordsException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.UrlShortenerException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.external.UrlShortener;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.IdGenerator;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.StemmingAlgo;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Stopwords;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultBookmarksService implements BookmarksService {
    private static final String SPLIT_REGEX = "[\\s\\p{IsPunctuation}]+";
    private static final int MAX_KEYWORDS = 25;
    private final HttpClient client;

    public DefaultBookmarksService(HttpClient client) {
        this.client = client;
    }

    @Override
    public Bookmark generateBookmark(String url, Group group, boolean shortened, User user)
        throws InvalidBookmarkException {
        Nullable.throwIfAnyNull(url, group, user);

        validateUrl(url);

        String shortenedUrl = "";

        if (shortened) {
            UrlShortener shortener = DIContainer.request(UrlShortener.class);
            try {
                shortenedUrl = shortener.shorten(url);
            } catch (UrlShortenerException e) {
                throw new InvalidBookmarkException(e);
            }
        }

        Document htmlDocument;
        try {
            htmlDocument = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new InvalidBookmarkException(e);
        }

        var htmlText = htmlDocument.body().text().split(SPLIT_REGEX);

        Set<String> stopwords;
        try {
            stopwords = Set.copyOf(Stopwords.list());
        } catch (StopWordsException e) {
            throw new InvalidBookmarkException(e);
        }


        var tags = Arrays.stream(htmlText)
            .map(String::trim)
            .map(String::toLowerCase)
            .map(StemmingAlgo::suffixStripping)
            .filter(Predicate.not(stopwords::contains))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .limit(MAX_KEYWORDS)
            .toList();

        return new Bookmark(IdGenerator.generateId(), url, shortenedUrl, htmlDocument.title(), tags, user, group);
    }

    @Override
    public boolean validateUrl(String url) throws InvalidBookmarkException {
        try {
            return
                client.send(HttpRequest.newBuilder().uri(URI.create(url)).build(), HttpResponse.BodyHandlers.ofString())
                    .statusCode() !=
                    HttpURLConnection.HTTP_NOT_FOUND;
        } catch (Exception e) {
            throw new InvalidBookmarkException(e);
        }
    }
}
