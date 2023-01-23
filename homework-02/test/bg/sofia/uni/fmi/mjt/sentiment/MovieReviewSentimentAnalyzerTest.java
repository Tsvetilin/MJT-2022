package bg.sofia.uni.fmi.mjt.sentiment;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MovieReviewSentimentAnalyzerTest {
    private static final String REVIEWS_FIRST =
        "4 Entertaining and independent, worth seeing." + System.lineSeparator() +
            "1 Aggressive and manipulative whitewash, not worth seeing.";

    private static final String REVIEWS_SECOND =
        "3 Entertaining and independent, worth seeing." + System.lineSeparator() +
            "1 Aggressive and manipulative whitewash, not worth seeing." + System.lineSeparator() +
            "4 Entertaining" + System.lineSeparator() +
            "3 independent" + System.lineSeparator() +
            "0 Aggressive." + System.lineSeparator();

    private static final String REVIEWS_THIRD =
        "4 Love love love is all you need. " + System.lineSeparator() +
            "2 Love and hate coexist." + System.lineSeparator() +
            "0 Hate is bad." + System.lineSeparator();

    private static final String STOP_WORDS =
        "of" + System.lineSeparator() +
            "the" + System.lineSeparator() +
            "is" + System.lineSeparator() +
            "for" + System.lineSeparator() +
            "also" + System.lineSeparator() +
            "some" + System.lineSeparator() +
            "but" + System.lineSeparator() +
            "and" + System.lineSeparator();

    @Test
    void testIsStopWord() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        assertTrue(analyzer.isStopWord("of"));
        assertTrue(analyzer.isStopWord("the"));
        assertFalse(analyzer.isStopWord("when"));
    }

    @Test
    void testGetSentimentDictionarySize() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        assertEquals(8, analyzer.getSentimentDictionarySize());
    }

    @Test
    void testGetSentimentDictionarySizeWithRepeatingWords() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_THIRD),
            new StringWriter());

        assertEquals(7, analyzer.getSentimentDictionarySize());
    }

    @Test
    void testGetWordFrequency() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        assertEquals(2, analyzer.getWordFrequency("seeing"));
        assertEquals(2, analyzer.getWordFrequency("worth"));
        assertEquals(2, analyzer.getWordFrequency("WoRTh"));
        assertEquals(1, analyzer.getWordFrequency("Entertaining"));
        assertEquals(0, analyzer.getWordFrequency("seek"));
    }

    @Test
    void testGetWordFrequencyWithRepeatingWords() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_THIRD),
            new StringWriter());

        assertEquals(4, analyzer.getWordFrequency("love"));
        assertEquals(2, analyzer.getWordFrequency("hate"));
        assertEquals(0, analyzer.getWordFrequency("seek"));
    }

    @Test
    void testGetMostFrequentWords() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        var result = analyzer.getMostFrequentWords(2);
        assertEquals(2, result.size());
        assertTrue(result.contains("seeing"));
        assertTrue(result.contains("worth"));
    }

    @Test
    void testGetMostFrequentWordsWithMoreRequested() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        var result = analyzer.getMostFrequentWords(20);
        assertEquals(8, result.size());
    }

    @Test
    void testGetWordSentiment() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        assertEquals(2.5, analyzer.getWordSentiment("seeing"));
        assertEquals(1, analyzer.getWordSentiment("aggressive"));
        assertEquals(4, analyzer.getWordSentiment("entertaining"));
        assertEquals(4, analyzer.getWordSentiment("EntErtAinInG"));
        assertEquals(-1, analyzer.getWordSentiment("seek"));
    }

    @Test
    void testGetWordSentimentWithRepeatingWords() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_THIRD),
            new StringWriter());


        assertEquals(3, analyzer.getWordSentiment("love"));
        assertEquals(1, analyzer.getWordSentiment("hate"));
        assertEquals(-1, analyzer.getWordSentiment("seek"));
    }

    @Test
    void testGetReviewSentiment() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        assertEquals(2.5, analyzer.getReviewSentiment("seeing"));
        assertEquals(1, analyzer.getReviewSentiment("aggressive"));
        assertEquals(4, analyzer.getReviewSentiment("entertaining"));
        assertEquals(4, analyzer.getReviewSentiment("EntErtAinInG"));
        assertEquals(-1, analyzer.getReviewSentiment("seek"));
    }

    @Test
    void testGetReviewSentimentAsName() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        assertEquals("somewhat positive", analyzer.getReviewSentimentAsName("seeing"));
        assertEquals("somewhat negative", analyzer.getReviewSentimentAsName("aggressive"));
        assertEquals("positive", analyzer.getReviewSentimentAsName("entertaining"));
        assertEquals("positive", analyzer.getReviewSentimentAsName("EntErtAinInG"));
        assertEquals("unknown", analyzer.getReviewSentimentAsName("seek"));
    }

    @Test
    void testGetMostPositiveWordsShouldThrow() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        assertThrows(IllegalArgumentException.class, () -> analyzer.getMostPositiveWords(-1));
    }

    @Test
    void testGetMostPositiveWords() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_SECOND),
            new StringWriter());

        var result = analyzer.getMostPositiveWords(2);
        assertEquals(2, result.size());
        assertTrue(result.contains("entertaining"));
        assertTrue(result.contains("independent"));
    }

    @Test
    void testGetMostNegativeWordsShouldThrow() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        assertThrows(IllegalArgumentException.class, () -> analyzer.getMostNegativeWords(-1));
    }

    @Test
    void testGetMostNegativeWords() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_SECOND),
            new StringWriter());

        var result = analyzer.getMostNegativeWords(1);
        assertEquals(1, result.size());
        assertTrue(result.contains("aggressive"));
    }

    @Test
    void testAddReviewShouldThrowWithInvalidReview() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        assertThrows(IllegalArgumentException.class, () -> analyzer.appendReview(null, 2));
        assertThrows(IllegalArgumentException.class, () -> analyzer.appendReview("", 2));
        assertThrows(IllegalArgumentException.class, () -> analyzer.appendReview("  ", 2));
    }

    @Test
    void testAddReviewShouldThrowWithInvalidSentiment() {
        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            new StringWriter());

        assertThrows(IllegalArgumentException.class, () -> analyzer.appendReview("review", -1));
        assertThrows(IllegalArgumentException.class, () -> analyzer.appendReview("review", 5));
    }

    @Test
    void testAddReview() {
        var writer = new StringWriter();

        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            writer);

        var review = "Some review to append.";
        var sentiment = 3;
        assertTrue(analyzer.appendReview(review, sentiment));
        assertEquals(sentiment + " " + review + System.lineSeparator(), writer.toString());
    }

    @Test
    void testAddReviewFails() {
        var writer = mock(StringWriter.class);
        doThrow(IOException.class).when(writer).write(anyString());

        var analyzer = new MovieReviewSentimentAnalyzer(
            new StringReader(STOP_WORDS),
            new StringReader(REVIEWS_FIRST),
            writer);

        var review = "Some review to append.";
        var sentiment = 3;
        assertFalse(analyzer.appendReview(review, sentiment));
        verify(writer, times(1)).write(anyString());
    }
}