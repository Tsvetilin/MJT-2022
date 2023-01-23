package bg.sofia.uni.fmi.mjt.newsfeed.api;

import bg.sofia.uni.fmi.mjt.newsfeed.dto.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.dto.ArticleSource;
import bg.sofia.uni.fmi.mjt.newsfeed.dto.NewsFeedResponse;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedClientException;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NewsFeedHttpClientTest {

    private static NewsFeedResponse NEWS_RESPONSE;
    private static String NEWS_RESPONSE_STRING;
    private static URI REQUEST_URI;

    private static final Gson gson = new Gson();

    private final HttpClient httpClient = mock(HttpClient.class);

    private final NewsFeedHttpClient client = new NewsFeedHttpClient(httpClient);

    @BeforeAll
    static void setup() {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article(
            new ArticleSource("", ""),
            "testAuthor",
            "Test",
            "",
            "",
            "",
            "",
            ""));
        REQUEST_URI = NewsFeedRequest.newRequest("test").build().uri();
        NEWS_RESPONSE = new NewsFeedResponse(
            "ok",
            "errorCode",
            "errorMessage",
            articles.size(),
            articles);
        NEWS_RESPONSE_STRING = gson.toJson(NEWS_RESPONSE, NewsFeedResponse.class);
    }

    @Test
    @Order(1)
    void testSuccess() throws IOException, InterruptedException, NewsFeedClientException {
        var response = mock(HttpResponse.class);
        when(response.body()).thenReturn(NEWS_RESPONSE_STRING);
        when(httpClient.send(any(), any())).thenReturn(response);

        var result = client.getNewsFeed(REQUEST_URI);

        assertEquals(NEWS_RESPONSE.totalResults(), result.totalResults(), "Response should match expected.");
        assertEquals(NEWS_RESPONSE.articles().get(0).author(), result.articles().get(0).author(),
            "Content of the response should match expected.");

    }

    @Test
    @Order(2)
    void testFailExpected() throws IOException, InterruptedException {
        var response = mock(HttpResponse.class);
        when(response.body()).thenReturn(NEWS_RESPONSE_STRING.replace("ok", "error"));
        when(httpClient.send(any(), any())).thenReturn(response);

        try {
            client.getNewsFeed(REQUEST_URI);
            fail("Exception must be thrown when expected error occurs");
        } catch (NewsFeedClientException e) {
            verify(httpClient, times(1)).send(any(), any());
            assertEquals("errorCode", e.getErrorCode(), "Invalid error code passed.");
            assertEquals("errorMessage", e.getErrorMessage(), "Invalid error message passed.");
        }
    }

    @Test
    @Order(3)
    void testFailUnexpectedStatus() throws IOException, InterruptedException {
        var response = mock(HttpResponse.class);
        when(response.body()).thenReturn(NEWS_RESPONSE_STRING.replace("ok", "test"));
        when(httpClient.send(any(), any())).thenReturn(response);

        assertThrows(NewsFeedClientException.class, () -> client.getNewsFeed(REQUEST_URI),
            "Exception must be thrown when expected error occurs");

        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @Order(4)
    void testFailUnexpectedBody() throws IOException, InterruptedException {
        var response = mock(HttpResponse.class);
        when(response.body()).thenReturn(null);
        when(httpClient.send(any(), any())).thenReturn(response);

        assertThrows(NewsFeedClientException.class, () -> client.getNewsFeed(REQUEST_URI),
            "Exception must be thrown when expected error occurs");

        verify(httpClient, times(1)).send(any(), any());
    }
}