package bg.sofia.uni.fmi.mjt.bookmarks.server.persistence;

import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;

public class Entity<K> {
    private final K key;

    public Entity(K key) {
        this.key = key;
        Nullable.throwIfNull(key);
    }

    public K getKey() {
        return key;
    }
}
