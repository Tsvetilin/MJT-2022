package bg.sofia.uni.fmi.mjt.netflix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class NetflixRecommender {

    private static final double SENSITIVITY_THRESHOLD = 10_000;


    private static final int ID_INDEX = 0;
    private static final int TITLE_INDEX = 1;
    private static final int TYPE_INDEX = 2;
    private static final int DESC_INDEX = 3;
    private static final int RELEASE_YEAR_INDEX = 4;
    private static final int RUNTIME_INDEX = 5;
    private static final int GENRES_INDEX = 6;
    private static final int SEASONS_INDEX = 7;
    private static final int IMDB_ID_INDEX = 8;
    private static final int IMDB_SCORE_INDEX = 9;
    private static final int IMDB_VOTES_INDEX = 10;

    private static final int TOTAL_FIELDS_COUNT = 11;
    private final List<Content> movies;

    /**
     * Loads the dataset from the given {@code reader}.
     *
     * @param reader Reader from which the dataset can be read.
     */
    public NetflixRecommender(Reader reader) {
        try (var bufferedReader = new BufferedReader(reader)) {
            movies = bufferedReader.lines()
                    .skip(1)
                    .map(line -> line.split(","))
                    .filter(x -> x.length == TOTAL_FIELDS_COUNT)
                    .map(x -> new Content(
                            x[ID_INDEX].trim(),
                            x[TITLE_INDEX].trim(),
                            x[TYPE_INDEX].equals("MOVIE") ? ContentType.MOVIE : ContentType.SHOW,
                            x[DESC_INDEX].trim(),
                            Integer.parseInt(x[RELEASE_YEAR_INDEX].trim()),
                            Integer.parseInt(x[RUNTIME_INDEX].trim()),
                            Arrays.stream(x[GENRES_INDEX]
                                            .substring(
                                                    x[GENRES_INDEX].indexOf("[") + 1,
                                                    x[GENRES_INDEX].indexOf("]") - 1)
                                            .split(";"))
                                    .map(String::trim)
                                    .map(m -> m.replace("'", ""))
                                    .toList(),
                            Integer.parseInt(x[SEASONS_INDEX].trim()),
                            x[IMDB_ID_INDEX].trim(),
                            Double.parseDouble(x[IMDB_SCORE_INDEX].trim()),
                            Double.parseDouble(x[IMDB_VOTES_INDEX].trim())
                    ))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns all movies and shows from the dataset in undefined order as an unmodifiable List.
     * If the dataset is empty, returns an empty List.
     *
     * @return the list of all movies and shows.
     */
    public List<Content> getAllContent() {
        return movies.stream().collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns a list of all unique genres of movies and shows in the dataset in undefined order.
     * If the dataset is empty, returns an empty List.
     *
     * @return the list of all genres
     */
    public List<String> getAllGenres() {
        return movies.stream().flatMap(m -> m.genres().stream()).distinct().toList();
    }

    /**
     * Returns the movie with the longest duration / run time. If there are two or more movies
     * with equal maximum run time, returns any of them. Shows in the dataset are not considered by this method.
     *
     * @return the movie with the longest run time
     * @throws NoSuchElementException in case there are no movies in the dataset.
     */
    public Content getTheLongestMovie() {
        return movies.stream()
                .filter(m -> m.type() == ContentType.MOVIE).max(Comparator.comparingInt(Content::runtime))
                .orElseThrow();
    }

    /**
     * Returns a breakdown of content by type (movie or show).
     *
     * @return a Map with key: a ContentType and value: the set of movies or shows on the dataset, in undefined order.
     */
    public Map<ContentType, Set<Content>> groupContentByType() {
        return movies.stream().collect(Collectors.groupingBy(
                Content::type,
                HashMap::new,
                Collectors.toSet()
        ));
    }

    /**
     * Returns the top N movies and shows sorted by weighed IMDB rating in descending order.
     * If there are fewer movies and shows than {@code n} in the dataset, return all of them.
     * If {@code n} is zero, returns an empty list.
     * <p>
     * The weighed rating is calculated by the following formula:
     * Weighted Rating (WR) = (v ÷ (v + m)) × R + (m ÷ (v + m)) × C
     * where
     * R is the content's own average rating across all votes. If it has no votes, its R is 0.
     * C is the average rating of content across the dataset
     * v is the number of votes for a content
     * m is a tunable parameter: sensitivity threshold. In our algorithm, it's a constant equal to 10_000.
     * <p>
     * Check https://stackoverflow.com/questions/1411199/what-is-a-better-way-to-sort-by-a-5-star-rating for details.
     *
     * @param n the number of the top-rated movies and shows to return
     * @return the list of the top-rated movies and shows
     * @throws IllegalArgumentException if {@code n} is negative.
     */
    public List<Content> getTopNRatedContent(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Cannot have negative top count.");
        }

        final double c = movies.stream()
                .mapToDouble(Content::imdbScore)
                .average()
                .orElse(0);

        return movies.stream()
                // .sorted(Comparator.comparing(this::getWeightedRating))
                .sorted((f1, f2) -> Double.compare(getWeightedRating(f2, c), getWeightedRating(f1, c)))
                .limit(n)
                .collect(Collectors.toList());
/*
                 (WR) = (v ÷ (v + m)) × R + (m ÷ (v + m)) × C
                * R is the content's own average rating across all votes. If it has no votes, its R is 0.
                * C is the average rating of content across the dataset
                * v is the number of votes for a content
                * m is a tunable parameter: sensitivity threshold. In our algorithm, it's a constant equal to 10_000
     */
    }

    private double getWeightedRating(Content movie, double c) {
        var v = movie.imdbVotes();
        var r = movie.imdbScore();
        return ((v / (v + SENSITIVITY_THRESHOLD)) * r + (SENSITIVITY_THRESHOLD / (v + SENSITIVITY_THRESHOLD)) * c);
    }

    /**
     * Returns a list of content similar to the specified one sorted by similarity is descending order.
     * Two contents are considered similar, only if they are of the same type (movie or show).
     * The used measure of similarity is the number of genres two contents share.
     * If two contents have equal number of common genres with the specified one, their mutual oder
     * in the result is undefined.
     *
     * @param content the specified movie or show.
     * @return the sorted list of content similar to the specified one.
     */
    public List<Content> getSimilarContent(Content content) {
        return movies.stream()
                .filter(m -> m.type() == content.type())
                .sorted((m1, m2) -> -(int) (getSimilarCount(m1, content) - getSimilarCount(m2, content)))
                .collect(Collectors.toList());
    }

    private long getSimilarCount(Content m, Content content) {
        return content.genres().stream().filter(g -> m.genres().contains(g)).count();
    }

    /**
     * Searches content by keywords in the description (case-insensitive).
     *
     * @param keywords the keywords to search for
     * @return an unmodifiable set of movies and shows whose description contains all specified keywords.
     */
    public Set<Content> getContentByKeywords(String... keywords) {
        return movies.stream()
                .filter(movie ->
                        Arrays.stream(keywords)
                                .map(String::toLowerCase)
                                .allMatch(word ->
                                        Arrays.stream(movie
                                                        .description()
                                                        .toLowerCase()
                                                        .split("[\\p{IsPunctuation}\\s]+"))
                                                .toList()
                                                .contains(word)))
                .collect(Collectors.toUnmodifiableSet());
    }

}