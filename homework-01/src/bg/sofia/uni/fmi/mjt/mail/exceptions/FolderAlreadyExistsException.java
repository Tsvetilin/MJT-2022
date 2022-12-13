package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class FolderAlreadyExistsException extends RuntimeException {
    public FolderAlreadyExistsException(String message) {
        super(message);
    }
}
