package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.api.NewsFeedHttpClient;
import bg.sofia.uni.fmi.mjt.newsfeed.api.NewsFeedRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.api.NewsFeedResult;
import bg.sofia.uni.fmi.mjt.newsfeed.dto.NewsFeedArticles;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import bg.sofia.uni.fmi.mjt.newsfeed.utils.Cache;

public class NewsFeedClient {
    private final NewsFeedHttpClient client;
    private final Cache<NewsFeedRequest, NewsFeedResult> cache;

    public NewsFeedClient(NewsFeedHttpClient client, Cache<NewsFeedRequest, NewsFeedResult> cache) {

        this.client = client;
        this.cache = cache;
    }

    public NewsFeedResult execute(NewsFeedRequest request) throws NewsFeedClientException {
        if (cache.get(request) != null) {
            return cache.get(request);
        }

        NewsFeedArticles articles = client.getNewsFeed(request.uri());
        NewsFeedResult result = new NewsFeedResult(articles.articles());

        var iterator = request.getIterator(articles.totalResults());
        while (iterator.hasNext()) {
            result.articles().addAll(client.getNewsFeed(iterator.next()).articles());
        }

        cache.put(request, result);

        return result;
    }
}
