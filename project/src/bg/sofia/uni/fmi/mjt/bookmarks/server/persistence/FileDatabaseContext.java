package bg.sofia.uni.fmi.mjt.bookmarks.server.persistence;

import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.FileRepository;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.Repository;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.observe.BookmarksObserver;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.observe.GroupsObserver;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;

public class FileDatabaseContext implements DatabaseContext {

    private static final int AUTOSAVE_INTERVAL = 15 * 60 * 1000;
    private final Repository<String, User> usersRepository;
    private final Repository<String, Bookmark> bookmarksRepository;
    private final Repository<String, Group> groupsRepository;
    private final AutoSaveContextDaemon saveContextDaemon;

    public FileDatabaseContext(FileRepository<String, User> usersRepository,
                               FileRepository<String, Bookmark> bookmarksRepository,
                               FileRepository<String, Group> groupsRepository) {
        this(usersRepository, bookmarksRepository, groupsRepository, AUTOSAVE_INTERVAL);
    }

    public FileDatabaseContext(FileRepository<String, User> usersRepository,
                               FileRepository<String, Bookmark> bookmarksRepository,
                               FileRepository<String, Group> groupsRepository,
                               int autoSaveInterval) {

        Nullable.throwIfAnyNull(usersRepository, bookmarksRepository, groupsRepository);
        this.usersRepository = usersRepository;
        this.bookmarksRepository = bookmarksRepository;
        this.groupsRepository = groupsRepository;

        ((FileRepository<String, Bookmark>) this.bookmarksRepository).attach(
            new BookmarksObserver(usersRepository, groupsRepository));
        ((FileRepository<String, Group>) this.groupsRepository).attach(
            new GroupsObserver(usersRepository));

        autoSaveInterval = autoSaveInterval > 0 ? autoSaveInterval : AUTOSAVE_INTERVAL;
        saveContextDaemon = new AutoSaveContextDaemon(this, autoSaveInterval);
        saveContextDaemon.start();
    }

    @Override
    public Repository<String, User> users() {
        return usersRepository;
    }

    @Override
    public Repository<String, Bookmark> bookmarks() {
        return bookmarksRepository;
    }

    @Override
    public Repository<String, Group> groups() {
        return groupsRepository;
    }

    @Override
    public synchronized void persist() {
        this.usersRepository.persist();
        this.groupsRepository.persist();
        this.bookmarksRepository.persist();
    }

    @Override
    public void shutdown() {
        saveContextDaemon.stopDaemon();

        try {
            saveContextDaemon.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        persist();
    }

    @Override
    public void load() {
        this.usersRepository.load();
        this.groupsRepository.load();
        this.bookmarksRepository.load();
    }
}
