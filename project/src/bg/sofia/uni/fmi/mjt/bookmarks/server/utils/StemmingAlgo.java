package bg.sofia.uni.fmi.mjt.bookmarks.server.utils;

import java.util.HashSet;
import java.util.Set;

public class StemmingAlgo {
    private static final Set<String> SUFFIX = new HashSet<>();

    static {
        SUFFIX.add("ed");
        SUFFIX.add("ing");
        SUFFIX.add("ly");
    }

    public static String suffixStripping(String word) {
        for (String suffix : SUFFIX) {
            if (word.endsWith(suffix)) {
                return word.substring(0, word.lastIndexOf(suffix));
            }
        }

        return word;
    }
}
