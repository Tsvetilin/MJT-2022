package bg.sofia.uni.fmi.mjt.newsfeed.utils;

import java.time.Duration;
import java.time.Instant;

public class TimedObject<V> {

    private final V object;
    private final Instant timeOfCreation;

    public TimedObject(V object) {
        this.object = object;
        this.timeOfCreation = Instant.now();
    }

    public V getObject() {
        return object;
    }

    public long secondsSinceCreation() {
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(timeOfCreation, end);
        return timeElapsed.getSeconds();
    }

}
