package bg.sofia.uni.fmi.mjt.sentiment;

import java.util.List;

public interface SentimentAnalyzer {

    /**
     * @param review the text of the review
     * @return the review sentiment as a floating-point number in the interval [0.0,
     * 4.0] if known, and -1.0 if unknown.
     */
    double getReviewSentiment(String review);

    /**
     * @param review the text of the review
     * @return the review sentiment as a name: "negative", "somewhat negative",
     * "neutral", "somewhat positive", "positive" or "unknown"
     */
    String getReviewSentimentAsName(String review);

    /**
     * @param word
     * @return the review sentiment of the word as a floating-point number in the
     * interval [0.0, 4.0] if known, and -1.0 if unknown
     */
    double getWordSentiment(String word);

    /**
     * @param word
     * @return the number of occurrences of the word in all reviews.
     * If {@code word} is a stopword, the result is undefined.
     */
    int getWordFrequency(String word);

    /**
     * Returns a list of the n most frequent words found in the reviews, sorted by frequency in decreasing order.
     * Stopwords are ignored and should not be included in the result.
     *
     * @throws {@link IllegalArgumentException}, if n is negative
     */
    List<String> getMostFrequentWords(int n);

    /**
     * Returns a list of the n most positive words in the reviews, sorted by sentiment score in decreasing order
     *
     * @throws {@link IllegalArgumentException}, if n is negative
     */
    List<String> getMostPositiveWords(int n);

    /**
     * Returns a list of the n most negative words in the reviews, sorted by sentiment score in ascending order
     *
     * @throws {@link IllegalArgumentException}, if n is negative
     */
    List<String> getMostNegativeWords(int n);

    /**
     * Appends a review to the end of the data set.
     * Any information from the data set stored in memory should be automatically updated.
     *
     * @param review    The text part of the review
     * @param sentiment the given rating
     * @return true if the operation was successful and false if an issue has occurred and the review is not stored
     * @throws {@link IllegalArgumentException}, if review is null, empty or blank,
     *                or if the sentiment is not in the [0.0, 4.0] range
     */
    boolean appendReview(String review, int sentiment);

    /**
     * Returns the total number of words with known sentiment score
     */
    int getSentimentDictionarySize();

    /**
     * Returns whether a word is a stopword
     */
    boolean isStopWord(String word);

}