package bg.sofia.uni.fmi.mjt.bookmarks.server.sessions;

import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;

import java.nio.channels.SocketChannel;

public record Session(SocketChannel key, User user) {
}
