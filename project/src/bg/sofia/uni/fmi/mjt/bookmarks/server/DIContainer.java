package bg.sofia.uni.fmi.mjt.bookmarks.server;

import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Service;

import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DIContainer {
    private static final Map<String, Object> CONTAINER;

    static {
        CONTAINER = new HashMap<>();
    }

    private DIContainer() {
    }

    public static <T> T request(Class<T> tClass) {
        var result = CONTAINER.get(tClass.getName());
        if (result == null) {
            throw new IllegalArgumentException("Not such service registered");
        }

        return (T) result;
    }

    public static void register(Type type, Service object) {

        Type[] interfaces = object.getClass().getInterfaces();
        if (Arrays.stream(interfaces).noneMatch(x -> x == type)) {
            throw new RuntimeException("Cannot add mismatching type definitions.");
        }

        CONTAINER.put(type.getTypeName(), object);
    }

    public static void clear() {
        CONTAINER.clear();
    }
}
