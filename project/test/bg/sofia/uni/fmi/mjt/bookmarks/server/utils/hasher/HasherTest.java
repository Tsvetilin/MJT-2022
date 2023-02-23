package bg.sofia.uni.fmi.mjt.bookmarks.server.utils.hasher;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.PasswordHasherException;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HasherTest {

    String passwordHash =
        "b93586da03e0d781602654254e42e19c:327069e885dd10154703a4264fbd451095d88fa406e36a33cca45a096acb0a35c7176648133f5673dcd0be2cdd4b4fc95c93b95ac9e689a9d0ab73bf3b3b49b";

    String password = "Abcdef1.";

    PasswordHasher hasher = new DefaultPasswordHasher(new SecureRandom());

    @Test
    void testVerify() throws PasswordHasherException {
        assertTrue(hasher.verify(password, passwordHash),
            "Passwords should match.");
    }

    @Test
    void testHash() throws PasswordHasherException {
        assertTrue(hasher.verify(password, hasher.hash(password)), "Invalid hashing");
    }


}
