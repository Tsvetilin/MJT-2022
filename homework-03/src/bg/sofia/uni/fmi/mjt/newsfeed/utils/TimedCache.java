package bg.sofia.uni.fmi.mjt.newsfeed.utils;

import java.util.HashMap;
import java.util.Map;

public class TimedCache<K, V> implements Cache<K, V> {

    private final long seconds;
    private final Map<K, TimedObject<V>> map;

    public TimedCache(long seconds) {
        this.seconds = seconds;
        this.map = new HashMap<>();
    }

    @Override
    public V get(K key) {
        TimedObject<V> timedObject = map.get(key);
        if (timedObject == null) {
            return null;
        }

        return timedObject.secondsSinceCreation() > this.seconds ? null : timedObject.getObject();
    }

    @Override
    public void put(K key, V value) {
        map.put(key, new TimedObject<V>(value));
    }
}
