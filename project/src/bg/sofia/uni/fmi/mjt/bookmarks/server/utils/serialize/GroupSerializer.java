package bg.sofia.uni.fmi.mjt.bookmarks.server.utils.serialize;

import bg.sofia.uni.fmi.mjt.bookmarks.server.DIContainer;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.DatabaseContext;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.IdGenerator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class GroupSerializer implements JsonSerializer<Group>, JsonDeserializer<Group> {

    @Override
    public Group deserialize(JsonElement jsonElement, Type type,
                             JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var jsonObject = jsonElement.getAsJsonObject();

        String id = jsonObject.get("id").getAsString();
        String userId = jsonObject.get("userId").getAsString();
        IdGenerator.addUsedId(id);

        var user = DIContainer.request(DatabaseContext.class).users().get(userId).orElseThrow();

        var group = new Group(
            id,
            jsonObject.get("name").getAsString(),
            user
        );

        user.groupsSynchronizer().add(group);

        return group;
    }

    @Override
    public JsonElement serialize(Group group, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        result.addProperty("id", group.getKey());
        result.addProperty("userId", group.getUser().getKey());
        result.addProperty("name", group.getName());

        return result;
    }
}