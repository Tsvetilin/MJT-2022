package bg.sofia.uni.fmi.mjt.bookmarks.server.services;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.InvalidBookmarkException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Service;

public interface BookmarksService extends Service {
    Bookmark generateBookmark(String url, Group group, boolean shortened, User user) throws InvalidBookmarkException;

    boolean validateUrl(String url) throws InvalidBookmarkException;
}
