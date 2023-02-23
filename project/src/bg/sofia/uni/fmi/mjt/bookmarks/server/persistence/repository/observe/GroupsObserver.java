package bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.observe;

import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.Repository;

public class GroupsObserver implements Observer<Group> {

    private final Repository<String, User> userRepository;

    public GroupsObserver(Repository<String, User> userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void notifyAdd(Group object) {
        userRepository.find(u -> u == object.getUser()).ifPresent(u -> u.groupsSynchronizer().add(object));
    }

    @Override
    public void notifyRemove(Group object) {
        userRepository.find(u -> u == object.getUser()).ifPresent(u -> u.groupsSynchronizer().remove(object));
    }
}
