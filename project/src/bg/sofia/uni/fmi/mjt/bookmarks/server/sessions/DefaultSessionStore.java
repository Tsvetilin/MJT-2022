package bg.sofia.uni.fmi.mjt.bookmarks.server.sessions;

import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class DefaultSessionStore implements SessionStore {

    private final Map<SocketChannel, User> sessions;

    public DefaultSessionStore() {
        sessions = new HashMap<>();
    }

    @Override
    public boolean hasSession(Session session) {
        return sessions.containsKey(session.key());
    }

    @Override
    public User getUser(Session session) {
        return sessions.get(session.key());
    }

    @Override
    public void register(Session session) {
        sessions.put(session.key(), session.user());
    }

    @Override
    public void remove(Session session) {
        sessions.remove(session.key());
    }
}
