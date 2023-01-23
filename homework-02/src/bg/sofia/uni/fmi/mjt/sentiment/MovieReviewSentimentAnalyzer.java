package bg.sofia.uni.fmi.mjt.sentiment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer {

    private final Writer reviewsOut;
    private final Set<String> stopWords;
    private final Map<String, List<Integer>> wordScores;
    private final Map<String, Integer> wordOccurrences;

    private static final int NEGATIVE_SENTIMENT = 0;
    private static final int SOMEWHAT_NEGATIVE_SENTIMENT = 1;
    private static final int NEUTRAL_SENTIMENT = 2;
    private static final int SOMEWHAT_POSITIVE_SENTIMENT = 3;
    private static final int POSITIVE_SENTIMENT = 4;
    private static final int UNKNOWN_SENTIMENT = -1;
    private static final HashMap<Integer, String> SENTIMENT_NAMES;

    static {
        SENTIMENT_NAMES = new HashMap<>();
        SENTIMENT_NAMES.put(NEGATIVE_SENTIMENT, "negative");
        SENTIMENT_NAMES.put(SOMEWHAT_NEGATIVE_SENTIMENT, "somewhat negative");
        SENTIMENT_NAMES.put(NEUTRAL_SENTIMENT, "neutral");
        SENTIMENT_NAMES.put(SOMEWHAT_POSITIVE_SENTIMENT, "somewhat positive");
        SENTIMENT_NAMES.put(POSITIVE_SENTIMENT, "positive");
        SENTIMENT_NAMES.put(UNKNOWN_SENTIMENT, "unknown");
    }

    public MovieReviewSentimentAnalyzer(Reader stopwordsIn, Reader reviewsIn, Writer reviewsOut) {
        stopWords = new HashSet<>();
        wordScores = new HashMap<>();
        wordOccurrences = new HashMap<>();

        this.reviewsOut = reviewsOut;
        readStopwords(stopwordsIn);
        readReviews(reviewsIn);
    }

    private void readStopwords(Reader reader) {
        try (var r = new BufferedReader(reader)) {
            String word;
            while ((word = r.readLine()) != null) {
                stopWords.add(word);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readReviews(Reader reader) {
        try (var r = new BufferedReader(reader)) {
            String line;
            while ((line = r.readLine()) != null) {
                int sentiment = Integer.parseInt(line.substring(0, line.indexOf(" ")));
                String review = line.substring(line.indexOf(" ")).toLowerCase();
                calculateRating(review, sentiment);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void throwIfNegative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be a positive integer.");
        }
    }

    @Override
    public double getReviewSentiment(String review) {
        return getWords(review)
            .stream()
            .filter(wordScores::containsKey)
            .mapToDouble(x -> wordScores.get(x).stream().mapToInt(e -> e).average().getAsDouble())
            .average()
            .orElse(-1);
    }

    @Override
    public String getReviewSentimentAsName(String review) {
        return SENTIMENT_NAMES.getOrDefault((int) Math.round(getReviewSentiment(review)), "unknown");
    }

    @Override
    public double getWordSentiment(String word) {
        return wordScores.containsKey(word.toLowerCase()) ?
            wordScores.get(word.toLowerCase()).stream().mapToInt(e -> e).average().getAsDouble() : -1;
    }

    @Override
    public int getWordFrequency(String word) {
        return wordOccurrences.getOrDefault(word.toLowerCase(), 0);
    }

    @Override
    public List<String> getMostFrequentWords(int n) {
        throwIfNegative(n);
        return wordOccurrences.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(n)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private List<String> getTopWords(int n, boolean reverse) {
        var result = wordScores.entrySet().stream();

        if (reverse) {
            result = result.sorted(
                Comparator.comparing(e -> e.getValue().stream().mapToInt(x -> x).average().getAsDouble(),
                    Comparator.reverseOrder()));
        } else {
            result = result.sorted(
                Comparator.comparing(e -> e.getValue().stream().mapToInt(x -> x).average().getAsDouble()));
        }

        return result.limit(n)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getMostPositiveWords(int n) {
        throwIfNegative(n);
        return getTopWords(n, true);
    }

    @Override
    public List<String> getMostNegativeWords(int n) {
        throwIfNegative(n);
        return getTopWords(n, false);
    }

    @Override
    public boolean appendReview(String review, int sentiment) {
        if (review == null || review.isEmpty() || review.isBlank()) {
            throw new IllegalArgumentException("Review must be a valid string.");
        }

        if (sentiment < NEGATIVE_SENTIMENT || sentiment > POSITIVE_SENTIMENT) {
            throw new IllegalArgumentException("Sentiment must be between 0 and 4.");
        }

        try {
            reviewsOut.write(sentiment + " " + review + System.lineSeparator());
        } catch (Exception e) {
            return false;
        }

        calculateRating(review, sentiment);

        return true;
    }

    @Override
    public int getSentimentDictionarySize() {
        return wordScores.size();
    }

    @Override
    public boolean isStopWord(String word) {
        return stopWords.contains(word.toLowerCase());
    }

    private List<String> getWords(String str) {
        return Pattern.compile("[a-z0-9']{2,}")
            .matcher(str.toLowerCase())
            .results()
            .map(MatchResult::group)
            .filter(e -> !isStopWord(e))
            .collect(Collectors.toList());
    }

    private void calculateRating(String str, int sentiment) {
        var words = getWords(str);

        words.stream().distinct().forEach(w -> {
            wordScores.putIfAbsent(w, new ArrayList<>());
            wordScores.get(w).add(sentiment);
        });

        words.forEach(w -> {
            wordOccurrences.putIfAbsent(w, 0);
            wordOccurrences.put(w, wordOccurrences.get(w) + 1);
        });
    }

}
