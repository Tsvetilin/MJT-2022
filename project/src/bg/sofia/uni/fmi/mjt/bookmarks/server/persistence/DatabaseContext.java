package bg.sofia.uni.fmi.mjt.bookmarks.server.persistence;

import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.Repository;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Service;

public interface DatabaseContext extends Service {

    Repository<String, User> users();

    Repository<String, Bookmark> bookmarks();

    Repository<String, Group> groups();

    void persist();

    void shutdown();

    void load();
}
