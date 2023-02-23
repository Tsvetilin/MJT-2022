package bg.sofia.uni.fmi.mjt.bookmarks.server.utils;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.StopWordsException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Stopwords {

    private static final String STOPWORDS_FILE_PATH = "resources/stopwords.txt";
    private static final Set<String> STOPWORDS;
    private static boolean isLoaded;

    static {
        STOPWORDS = new HashSet<>();
        isLoaded = false;
    }

    public static Collection<String> list() throws StopWordsException {
        if (!isLoaded) {
            throw new StopWordsException("Stopwords not loaded.");
        }
        return STOPWORDS;
    }

    public static boolean isStopword(String word) throws StopWordsException {
        if (!isLoaded) {
            throw new StopWordsException("Stopwords not loaded.");
        }

        return STOPWORDS.contains(word.trim().toLowerCase());
    }


    public static void add(String word) {
        STOPWORDS.add(word);
        isLoaded = true;
    }

    public static void load() throws StopWordsException {
        load(STOPWORDS_FILE_PATH);
    }

    public static void load(String path) throws StopWordsException {
        Nullable.throwIfNull(path);

        try (var reader = new FileReader(path)) {
            load(reader);
        } catch (IOException e) {
            throw new StopWordsException(e);
        }
    }

    public static void load(Reader reader) throws StopWordsException {
        Nullable.throwIfNull(reader);

        STOPWORDS.clear();
        isLoaded = false;

        try (var bufferedReader = new BufferedReader(reader)) {
            STOPWORDS.addAll(bufferedReader.lines().collect(Collectors.toSet()));
            isLoaded = true;
        } catch (IOException e) {
            throw new StopWordsException(e);
        }
    }
}
