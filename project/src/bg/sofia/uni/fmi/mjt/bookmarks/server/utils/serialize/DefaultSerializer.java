package bg.sofia.uni.fmi.mjt.bookmarks.server.utils.serialize;

import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class DefaultSerializer implements Serializer {
    private final Gson gson;

    public DefaultSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(User.class, new UserSerializer());
        gsonBuilder.registerTypeAdapter(Group.class, new GroupSerializer());
        gsonBuilder.registerTypeAdapter(Bookmark.class, new BookmarkSerializer());

        gson = gsonBuilder.create();
    }

    @Override
    public <T> String serialize(T object) {
        return gson.toJson(object);
    }

    @Override
    public <T> String serialize(T object, Type type) {
        return gson.toJson(object, type);
    }

    @Override
    public <T> T deserialize(String object, Type type) {
        return gson.fromJson(object, type);
    }
}
