package bg.sofia.uni.fmi.mjt.bookmarks.server.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class IdGenerator {
    private static final Set<String> IDS;

    static {
        IDS = new HashSet<>();
    }

    public static String generateId() {
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (IDS.contains(id));

        return id;
    }

    public static void addUsedId(String id) {
        IDS.add(id);
    }

    public static void clear() {
        IDS.clear();
    }

}
