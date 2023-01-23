package bg.sofia.uni.fmi.mjt.newsfeed.api;

import bg.sofia.uni.fmi.mjt.newsfeed.utils.Iterator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NewsFeedRequestTest {

    private int countIterations(Iterator<URI> iterator){
        int iterationsMade = 0;
        while (iterator.hasNext()) {
            iterator.next();
            iterationsMade++;
        }

        return iterationsMade;
    }

    @Test
    @Order(1)
    void testGenerateUri() {
        var request = NewsFeedRequest.newRequest("keyword1").
            forCategory(NewsFeedCategory.GENERAL).
            fromCountry(NewsFeedCountry.ae).
            getTopNNews(20).
            withKeywords("keyword2").build();

        assertEquals(
            "https://newsapi.org/v2/top-headlines?q=keyword1,%20keyword2&country=ae&category=general&page=1&pageSize=50&",
            request.uri().toString(),
            "Url must be created accordingly.");
    }

    @Test
    @Order(2)
    void testIterator() {
        var request = NewsFeedRequest.newRequest("keyword1").
            forCategory(NewsFeedCategory.GENERAL).
            fromCountry(NewsFeedCountry.ae).
            getTopNNews(70).
            withKeywords("keyword2").build();

        var iterator = request.getIterator(80);

        assertEquals(
            1,
            countIterations(iterator),
            "Desired number of news should be properly paginated.");
    }

    @Test
    @Order(3)
    void testIteratorWithInsufficientNews() {
        var request = NewsFeedRequest.newRequest("keyword1").
            forCategory(NewsFeedCategory.GENERAL).
            fromCountry(NewsFeedCountry.ae).
            getTopNNews(70).
            withKeywords("keyword2").build();

        var iterator = request.getIterator(20);

        assertEquals(
            0,
            countIterations(iterator),
            "Desired number of news should be properly paginated.");
    }


    @Test
    @Order(4)
    void testIteratorWhenAlreadyMatched() {
        var request = NewsFeedRequest.newRequest("keyword1").
            forCategory(NewsFeedCategory.GENERAL).
            fromCountry(NewsFeedCountry.ae).
            getTopNNews(20).
            withKeywords("keyword2").build();

        var iterator = request.getIterator(80);

        assertEquals(
            0,
            countIterations(iterator),
            "Desired number of news should be properly paginated.");
    }

    @Test
    @Order(5)
    void testGenerateUriWithInvalidTopNWords() {
        var request = NewsFeedRequest.newRequest("keyword1").
            forCategory(NewsFeedCategory.GENERAL).
            fromCountry(NewsFeedCountry.ae).
            getTopNNews(-20).
            withKeywords("keyword2").build();

        var iterator = request.getIterator(80);

        assertEquals(
            1,
            countIterations(iterator),
            "Desired number of news should be properly paginated.");
    }
}
