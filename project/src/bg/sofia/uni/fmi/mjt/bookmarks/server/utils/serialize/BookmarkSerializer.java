package bg.sofia.uni.fmi.mjt.bookmarks.server.utils.serialize;

import bg.sofia.uni.fmi.mjt.bookmarks.server.DIContainer;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
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
import java.util.Arrays;

public class BookmarkSerializer implements JsonSerializer<Bookmark>, JsonDeserializer<Bookmark> {

    @Override
    public Bookmark deserialize(JsonElement jsonElement, Type type,
                                JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        var jsonObject = jsonElement.getAsJsonObject();

        String id = jsonObject.get("id").getAsString();
        String userId = jsonObject.get("userId").getAsString();
        String groupId = jsonObject.get("groupId").getAsString();
        IdGenerator.addUsedId(id);

        var user = DIContainer.request(DatabaseContext.class).users().get(userId).orElseThrow();
        var group = DIContainer.request(DatabaseContext.class).groups().get(groupId).orElseThrow();

        var bookmark = new Bookmark(
            id,
            jsonObject.get("url").getAsString(),
            jsonObject.get("shortened").getAsString(),
            jsonObject.get("title").getAsString(),
            Arrays.stream(jsonObject.get("keywords").getAsString().split(",")).toList(),
            user,
            group
        );

        group.bookmarksSynchronizer().add(bookmark);
        user.bookmarksSynchronizer().add(bookmark);

        return bookmark;
    }

    @Override
    public JsonElement serialize(Bookmark bookmark, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        result.addProperty("id", bookmark.getKey());
        result.addProperty("url", bookmark.getUrl());
        result.addProperty("title", bookmark.getTitle());
        result.addProperty("shortened", bookmark.getShortened());
        result.addProperty("keywords", String.join(",", bookmark.getKeywords()));
        result.addProperty("userId", bookmark.getUser().getKey());
        result.addProperty("groupId", bookmark.getGroup().getKey());

        return result;
    }
}