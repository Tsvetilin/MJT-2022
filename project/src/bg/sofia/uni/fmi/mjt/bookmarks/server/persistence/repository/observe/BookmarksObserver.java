package bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.observe;

import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.Repository;

public class BookmarksObserver implements Observer<Bookmark> {

    private final Repository<String, User> userRepository;
    private final Repository<String, Group> groupRepository;

    public BookmarksObserver(Repository<String, User> userRepository, Repository<String, Group> groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public void notifyAdd(Bookmark object) {
        userRepository.find(u -> u == object.getUser()).ifPresent(u -> u.bookmarksSynchronizer().add(object));
        groupRepository.find(u -> u == object.getGroup()).ifPresent(b -> b.bookmarksSynchronizer().add(object));
    }

    @Override
    public void notifyRemove(Bookmark object) {
        userRepository.find(u -> u == object.getUser()).ifPresent(u -> u.bookmarksSynchronizer().remove(object));
        groupRepository.find(u -> u == object.getGroup()).ifPresent(b -> b.bookmarksSynchronizer().remove(object));
    }
}
