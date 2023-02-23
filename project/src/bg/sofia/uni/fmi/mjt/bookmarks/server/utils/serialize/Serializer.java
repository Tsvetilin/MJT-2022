package bg.sofia.uni.fmi.mjt.bookmarks.server.utils.serialize;

import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Service;

import java.lang.reflect.Type;

public interface Serializer extends Service {
    <T> String serialize(T object);

    <T> String serialize(T object, Type type);

    <T> T deserialize(String object, Type type);
}
