package bg.sofia.uni.fmi.mjt.bookmarks.server.utils.serialize;

import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.IdGenerator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class UserSerializer implements JsonSerializer<User>, JsonDeserializer<User> {

    @Override
    public User deserialize(JsonElement jsonElement, Type type,
                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var jsonObject = jsonElement.getAsJsonObject();

        String id = jsonObject.get("id").getAsString();

        IdGenerator.addUsedId(id);

        return new User(
            id,
            jsonObject.get("username").getAsString(),
            jsonObject.get("password").getAsString()
        );
    }

    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        result.addProperty("id", user.getKey());
        result.addProperty("username", user.getUsername());
        result.addProperty("password", user.getPassword());

        return result;
    }
}