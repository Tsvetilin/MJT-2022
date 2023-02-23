package bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository;

import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.Entity;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.observe.Observable;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileRepository<K, T extends Entity<K>> extends Observable<T> implements Repository<K, T> {


    private final FileRepositoryOptions options;

    private final Map<K, T> table;

    private final Type keyType;
    private final Type valueType;

    public FileRepository(FileRepositoryOptions options, Class<K> keyType, Class<T> valueType) {
        this.keyType = keyType;
        this.valueType = valueType;

        Nullable.throwIfNull(options);

        this.options = options;
        this.table = new HashMap<>();
    }

    @Override
    public void load() {
        table.clear();
        try (var reader = new BufferedReader(options.reader())) {
            var json = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            if (json.isEmpty() || json.isBlank()) {
                return;
            }

            table.putAll(
                options
                    .serializer()
                    .deserialize(json, TypeToken.getParameterized(Map.class, keyType, valueType).getType())
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(T object) {
        table.put(object.getKey(), object);
        notifyAdd(object);
    }

    @Override
    public Optional<T> get(K key) {
        return Optional.ofNullable(table.get(key));
    }

    @Override
    public Optional<T> find(Predicate<T> predicate) {
        return table.values().stream().filter(predicate).findFirst();
    }

    @Override
    public boolean any(Predicate<T> predicate) {
        return table.values().stream().anyMatch(predicate);
    }

    @Override
    public void remove(K key) {
        notifyRemove(table.get(key));
        table.remove(key);
    }

    @Override
    public boolean contains(K key) {
        return table.containsKey(key);
    }

    @Override
    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(table.values());
    }

    @Override
    public void persist() {
        try (var writer = options.writer()) {
            writer.write(options.serializer().serialize(table));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
