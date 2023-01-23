package bg.sofia.uni.fmi.mjt.newsfeed.api;

import bg.sofia.uni.fmi.mjt.newsfeed.dto.NewsFeedArticles;
import bg.sofia.uni.fmi.mjt.newsfeed.dto.NewsFeedResponse;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NewsFeedHttpClient {

    private static final String API_KEY = "a9df89c0ac364dd49bfb43497438fe0c";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String OK_STATUS = "ok";
    private static final String ERROR_STATUS = "error";

    private static final Gson GSON = new Gson();

    private final HttpClient httpClient;
    private final String apiKey;

    public NewsFeedHttpClient(HttpClient httpClient) {
        this(httpClient, API_KEY);
    }

    public NewsFeedHttpClient(HttpClient httpClient, String apiKey) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
    }

    public NewsFeedArticles getNewsFeed(URI requestUri) throws NewsFeedClientException {
        HttpResponse<String> response;

        try {
            HttpRequest httpRequest =
                HttpRequest.newBuilder().uri(requestUri).setHeader(AUTHORIZATION_HEADER, apiKey).build();
            response = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new NewsFeedClientException("Could not retrieve news feed.", e);
        }

        var parsedResponse = GSON.fromJson(response.body(), NewsFeedResponse.class);

        if (parsedResponse == null) {
            throw new NewsFeedClientException("Unexpected response from news feed service.");
        }

        if (parsedResponse.status().equals(ERROR_STATUS)) {
            throw new NewsFeedClientException(parsedResponse.code(), parsedResponse.message());
        } else if (parsedResponse.status().equals(OK_STATUS)) {
            return new NewsFeedArticles(parsedResponse.totalResults(), parsedResponse.articles());
        }

        throw new NewsFeedClientException("Unexpected response code from news feed service.");
    }


}
