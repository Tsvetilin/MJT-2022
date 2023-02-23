package bg.sofia.uni.fmi.mjt.bookmarks.server.utils.hasher;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.PasswordHasherException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Service;

public interface PasswordHasher extends Service {

    boolean verify(String password, String expectedHash) throws PasswordHasherException;

    String hash(String password) throws PasswordHasherException;
}
