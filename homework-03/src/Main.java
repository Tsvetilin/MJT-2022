import bg.sofia.uni.fmi.mjt.newsfeed.NewsFeedClient;
import bg.sofia.uni.fmi.mjt.newsfeed.api.NewsFeedHttpClient;
import bg.sofia.uni.fmi.mjt.newsfeed.api.NewsFeedRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.api.NewsFeedResult;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import bg.sofia.uni.fmi.mjt.newsfeed.utils.TimedCache;

import java.net.http.HttpClient;

public class Main {

    public static void main(String... args) throws NewsFeedClientException {
        final int timeout = 20;
        NewsFeedHttpClient httpClient = new NewsFeedHttpClient(HttpClient.newHttpClient());
        TimedCache<NewsFeedRequest, NewsFeedResult> cache = new TimedCache<>(timeout);
        NewsFeedClient client = new NewsFeedClient(httpClient, cache);
        var result = client.execute(NewsFeedRequest.newRequest("tesla").build());
    }
}
