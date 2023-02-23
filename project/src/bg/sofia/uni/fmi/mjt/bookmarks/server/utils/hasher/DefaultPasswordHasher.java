package bg.sofia.uni.fmi.mjt.bookmarks.server.utils.hasher;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.PasswordHasherException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HexFormat;

public class DefaultPasswordHasher implements PasswordHasher {
    private static final int SALT_SIZE = 16;
    private static final int HASH_ITERATIONS = 2000;
    private static final int KEY_LENGTH = 512;
    private static final int HEX = 16;
    private static final String ALGO_NAME = "PBKDF2WithHmacSHA1";
    private static final String SALT_SEPARATOR = ":";
    private final SecureRandom secureRandom;

    public DefaultPasswordHasher(SecureRandom random) {
        this.secureRandom = random;
    }

    @Override
    public boolean verify(String password, String expectedHash) throws PasswordHasherException {
        try {
            var split = expectedHash.split(SALT_SEPARATOR);
            String hexSalt = split[0];
            String hexHash = split[1];

            if (hexSalt.length() % 2 == 1) {
                hexSalt = "0" + hexSalt;
            }

            byte[] salt = HexFormat.of().parseHex(hexSalt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGO_NAME);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            String actual = new BigInteger(1, hash).toString(HEX);

            return actual.equals(hexHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new PasswordHasherException(e);
        }
    }

    @Override
    public String hash(String password) throws PasswordHasherException {
        try {
            byte[] salt = new byte[SALT_SIZE];
            secureRandom.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGO_NAME);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            String saltHex = new BigInteger(1, salt).toString(HEX);
            String hashHex = new BigInteger(1, hash).toString(HEX);

            return saltHex + SALT_SEPARATOR + hashHex;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new PasswordHasherException(e);
        }
    }
}

