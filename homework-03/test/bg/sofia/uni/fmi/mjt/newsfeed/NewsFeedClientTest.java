package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.api.NewsFeedHttpClient;
import bg.sofia.uni.fmi.mjt.newsfeed.api.NewsFeedRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.api.NewsFeedResult;
import bg.sofia.uni.fmi.mjt.newsfeed.dto.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.dto.ArticleSource;
import bg.sofia.uni.fmi.mjt.newsfeed.dto.NewsFeedArticles;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import bg.sofia.uni.fmi.mjt.newsfeed.utils.Iterator;
import bg.sofia.uni.fmi.mjt.newsfeed.utils.TimedCache;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NewsFeedClientTest {

    private static List<Article> ARTICLE_LIST;
    private static NewsFeedRequest NEWS_REQUEST;
    private static URI REQUEST_URI;
    private static NewsFeedArticles NEWS_ARTICLES;

    private static NewsFeedResult NEWS_RESULT;

    @Mock
    private NewsFeedHttpClient newsFeedHttpClient;

    @Mock
    private TimedCache<NewsFeedRequest, NewsFeedResult> timedCache;

    @InjectMocks
    private NewsFeedClient client;

    @BeforeAll
    static void setup() {
        ARTICLE_LIST = new ArrayList<>();
        ARTICLE_LIST.add(new Article(
            new ArticleSource("", ""),
            "testAuthor",
            "Test",
            "",
            "",
            "",
            "",
            ""));
        NEWS_REQUEST = NewsFeedRequest.newRequest("test").build();
        REQUEST_URI = NEWS_REQUEST.uri();
        NEWS_ARTICLES = new NewsFeedArticles(ARTICLE_LIST.size(), ARTICLE_LIST);
        NEWS_RESULT = new NewsFeedResult(ARTICLE_LIST);
    }

    @Test
    @Order(1)
    void testGetSinglePage() throws NewsFeedClientException {
        when(newsFeedHttpClient.getNewsFeed(REQUEST_URI)).thenReturn(NEWS_ARTICLES);
        when(timedCache.get(NEWS_REQUEST)).thenReturn(null);

        NewsFeedResult result = client.execute(NEWS_REQUEST);

        verify(timedCache, times(1)).get(NEWS_REQUEST);
        verify(newsFeedHttpClient, times(1)).getNewsFeed(REQUEST_URI);
        verify(timedCache, times(1)).put(NEWS_REQUEST, NEWS_RESULT);

        assertEquals(ARTICLE_LIST.size(), result.articles().size(), "Elements count should match.");
        assertEquals(ARTICLE_LIST.get(0).author(), result.articles().get(0).author(),
            "Invalid result by news feed http client.");
    }

    @Test
    @Order(2)
    void testCached() throws NewsFeedClientException {
        when(timedCache.get(NEWS_REQUEST)).thenReturn(NEWS_RESULT);

        NewsFeedResult result = client.execute(NEWS_REQUEST);

        verify(timedCache, times(2)).get(NEWS_REQUEST);
        verify(newsFeedHttpClient, times(0)).getNewsFeed(REQUEST_URI);
        verify(timedCache, times(0)).put(NEWS_REQUEST, NEWS_RESULT);

        assertEquals(ARTICLE_LIST.size(), result.articles().size(), "Elements count should match.");
        assertEquals(ARTICLE_LIST.get(0).author(), result.articles().get(0).author(),
            "Invalid result by news feed http client.");
    }

    @Test
    @Order(3)
    void testMultiplePages() throws NewsFeedClientException {
        var request = mock(NewsFeedRequest.class);
        var secondUri = mock(URI.class);
        var iterator = mock(Iterator.class);
        when(request.uri()).thenReturn(REQUEST_URI);
        when(request.getIterator(anyInt())).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true).thenReturn(false);
        when(iterator.next()).thenReturn(secondUri);
        when(newsFeedHttpClient.getNewsFeed(secondUri)).thenReturn(NEWS_ARTICLES);
        when(newsFeedHttpClient.getNewsFeed(REQUEST_URI)).thenReturn(NEWS_ARTICLES);
        when(timedCache.get(request)).thenReturn(null);

        NewsFeedResult result = client.execute(request);

        verify(iterator, times(1)).next();
        verify(timedCache, times(1)).get(request);
        verify(newsFeedHttpClient, times(1)).getNewsFeed(REQUEST_URI);
        verify(newsFeedHttpClient, times(1)).getNewsFeed(secondUri);
        verify(timedCache, times(1)).put(eq(request), any());

        assertEquals(ARTICLE_LIST.size(), result.articles().size(), "Elements count should match.");
        assertEquals(ARTICLE_LIST.get(0).author(), result.articles().get(0).author(),
            "Invalid result by news feed http client.");
    }
}