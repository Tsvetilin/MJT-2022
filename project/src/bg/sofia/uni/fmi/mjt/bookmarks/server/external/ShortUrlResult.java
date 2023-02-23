package bg.sofia.uni.fmi.mjt.bookmarks.server.external;

import com.google.gson.annotations.SerializedName;

public record ShortUrlResult(@SerializedName("created_at") String createdAt, String id, String link) {

}
