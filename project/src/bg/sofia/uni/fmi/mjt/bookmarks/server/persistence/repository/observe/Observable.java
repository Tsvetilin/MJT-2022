package bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.observe;

import java.util.ArrayList;
import java.util.List;

public class Observable<K> {

    private final List<Observer<K>> observers;

    public Observable() {
        this.observers = new ArrayList<>();
    }

    public void attach(Observer<K> observer) {
        observers.add(observer);
    }

    public void detach(Observer<K> observer) {
        observers.remove(observer);
    }

    public void notifyAdd(K object) {
        observers.forEach(observer->observer.notifyAdd(object));
    }

    public void notifyRemove(K object) {
        observers.forEach(observer->observer.notifyRemove(object));
    }
}
