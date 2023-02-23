package bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.observe;

public interface Observer<K> {

    void notifyAdd(K object);
    void notifyRemove(K object);
}
