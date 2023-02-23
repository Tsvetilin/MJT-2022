package bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository;

import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.Entity;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public interface Repository<K, T extends Entity<K>> {

    void add(T object);

    Optional<T> get(K key);

    Optional<T> find(Predicate<T> predicate);

    boolean any(Predicate<T> predicate);

    void remove(K key);

    boolean contains(K key);

    Collection<T> getAll();

    void persist();

    void load();
}
