package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class FolderNotFoundException extends RuntimeException {
    public FolderNotFoundException(String message) {
        super(message);
    }
}
