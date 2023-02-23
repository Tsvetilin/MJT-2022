package bg.sofia.uni.fmi.mjt.bookmarks.server.utils;

import java.util.Arrays;
import java.util.Objects;

public abstract class Nullable {
    public static <T> T orDefault(T object, T defaultValue) {
        if (object == null) {
            return defaultValue;
        }

        return object;
    }

    public static <T> void throwIfNull(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Argument cannot be null.");
        }
    }

    public static void throwIfAnyNull(Object... objects) {
        if (Arrays.stream(objects).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }

    }
}
