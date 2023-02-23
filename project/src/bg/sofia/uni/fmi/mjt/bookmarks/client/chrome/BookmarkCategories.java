package bg.sofia.uni.fmi.mjt.bookmarks.client.chrome;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public record BookmarkCategories(@SerializedName("bookmark_bar") BookmarksFolder bookmarkBar, BookmarksFolder other,
                                 BookmarksFolder synced) {
    List<ChromeBookmark> getAll() {
        var result = new ArrayList<>(bookmarkBar.children());
        result.addAll(other.children());
        result.addAll(synced.children());

        return result;
    }
}
