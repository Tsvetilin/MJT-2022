package bg.sofia.uni.fmi.mjt.netflix;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NetflixRecommenderTest {

    private static final String HEADER_LINE = "id,title,type,description,release_year,runtime,genres,seasons,imdb_id,imdb_score,imdb_votes";
    private static final String EXAMPLE_DATA = HEADER_LINE + System.lineSeparator() +
            "tm84618,Taxi Driver,MOVIE,A mentally unstable Vietnam War veteran works as a night-time taxi driver in New York City where the perceived decadence and sleaze feed his urge for violent action.,1976,114,['drama'; 'crime'],-1,tt0075314,8.2,808582.0" + System.lineSeparator() +
            "tm154986,Deliverance,MOVIE,Intent on seeing the Cahulawassee River before it's turned into one huge lake; outdoor fanatic Lewis Medlock takes his friends on a river-rafting trip they'll never forget into the dangerous American back-country.,1972,109,['drama'; 'action'; 'thriller'; 'european'],-1,tt0068473,7.7,107673.0";


    private static final String EXAMPLE_DATA_2 = HEADER_LINE + System.lineSeparator()+
            "ts54028,Case,SHOW,A dramatic thriller about a broken lawyer who seeks redemption through his investigation into the suspicious suicide of a teenage ballet prodigy.,2015,46,['drama'; 'crime'; 'thriller'],1,tt7875988,7.0,2755.0"+ System.lineSeparator()+
            "ts128895,Midnight Diner: Tokyo Stories,SHOW,An anthology of human relationship stories connected by the only open in the wee hours diner the characters frequent. Resolutions are often facilitated by the owner/chef.,2016,25,['drama'],1,tt6150576,8.4,4149.0";

    @Test
    void testConstructorShouldNotThrow() {
        assertDoesNotThrow(() -> new NetflixRecommender(new StringReader(EXAMPLE_DATA)));
    }

    @Test
    void testGetAllContentShouldNotThrow() {
        var recommender = new NetflixRecommender(new StringReader(EXAMPLE_DATA));

        var result = recommender.getAllContent();

        assertEquals(2, result.size());
        assertThrows(UnsupportedOperationException.class, () -> result.remove("x"));
    }

    @Test
    void testGetAllGenresShouldNotThrow() {
        var recommender = new NetflixRecommender(new StringReader(EXAMPLE_DATA));

        var result = recommender.getAllGenres();

        var expected = List.of("drama", "crime", "action", "thriller", "european");
        assertEquals(expected.size(), result.size());
        assertTrue(result.containsAll(expected));
        assertTrue(expected.containsAll(result));
    }

    @Test
    void testGetAllGenresShouldNotThrowIfEmptyList() {
        var recommender = new NetflixRecommender(new StringReader(HEADER_LINE));

        var result = recommender.getAllGenres();

        assertEquals(0, result.size());
    }

    @Test
    void testGetTheLongestMovieShouldThrowIfEmptyDataset() {
        var recommender = new NetflixRecommender(new StringReader(HEADER_LINE));

        assertThrows(NoSuchElementException.class, () -> recommender.getTheLongestMovie());
    }

    @Test
    void testGetTheLongestMovieShouldWorkProperly() {
        var recommender = new NetflixRecommender(new StringReader(EXAMPLE_DATA));

        var result = recommender.getTheLongestMovie();

        assertEquals("Taxi Driver", result.title());
    }

    @Test
    void testGroupContentByTypeShouldWorkProperly() {
        var recommender = new NetflixRecommender(new StringReader(EXAMPLE_DATA));

        var all = recommender.getAllContent();
        var result = recommender.groupContentByType();
        assertEquals(1, result.size());
        var movies = result.get(ContentType.MOVIE);
        assertEquals(2,movies.size());
    }

    @Test
    void testGetTopNRatedContentShouldWorkProperly() {
        var recommender = new NetflixRecommender(new StringReader(EXAMPLE_DATA));

        var result = recommender.getTopNRatedContent(2);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(x -> Objects.equals(x.title(), "Taxi Driver")));
    }

    @Test
    void testGetTopNRatedContentShouldWorkProperlyWithAnotherDataset() {
        var recommender = new NetflixRecommender(new StringReader(EXAMPLE_DATA_2));

        var result = recommender.getTopNRatedContent(1);

        assertEquals(1, result.size());
        assertEquals("ts128895", result.get(0).id());
    }

    @Test
    void testGetSimilarContentShouldWorkProperly() {
        var recommender = new NetflixRecommender(new StringReader(EXAMPLE_DATA));

        var content = recommender.getAllContent();
        var result = recommender.getSimilarContent(content.get(0));

        assertEquals(2, result.size());
        assertEquals(result.get(0).title(), content.get(0).title());
    }

    @Test
    void testGetContentByKeywordsShouldWorkProperly() {
        var recommender = new NetflixRecommender(new StringReader(EXAMPLE_DATA));

        var content = recommender.getAllContent();
        var result = recommender.getContentByKeywords("unstable", "war");

        assertEquals(1, result.size());
        assertTrue(result.contains(content.get(0)));
    }
}