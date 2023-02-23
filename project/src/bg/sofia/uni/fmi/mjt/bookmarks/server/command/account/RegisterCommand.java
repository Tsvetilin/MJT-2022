package bg.sofia.uni.fmi.mjt.bookmarks.server.command.account;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.DIContainer;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandBase;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.PasswordHasherException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.Session;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.IdGenerator;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.PasswordUtils;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.hasher.PasswordHasher;

public class RegisterCommand extends CommandBase {

    private final String username;
    private final String password;

    public RegisterCommand(String username, String password) {
        this.username = username;
        this.password = password;
        Nullable.throwIfAnyNull(username, password);
    }

    @Override
    public Response execute() {

        if (sessionStore.hasSession(session)) {
            logger.logInfo("User tried to register when logged: " + username);
            return new Response("Already logged in.", ResponseStatus.ERROR);
        }

        if (context.users().any(user -> user.getUsername().equals(username))) {
            logger.logInfo("User tried to register existing username: " + username);
            return new Response("Username already exists.", ResponseStatus.ERROR);
        }

        if (!PasswordUtils.validate(password)) {
            logger.logInfo("User tried to register with invalid password: " + username);
            return new Response("Invalid password! " + PasswordUtils.getPasswordRequirements(), ResponseStatus.ERROR);
        }

        String hashedPassword;

        try {
            hashedPassword = DIContainer.request(PasswordHasher.class).hash(password);
        } catch (PasswordHasherException e) {
            String traceId = IdGenerator.generateId();
            logger.logError("Server error in register request. Trace id: " + traceId);
            logger.logException(e, traceId);
            return new Response("Internal server error. Trace id: " + traceId, ResponseStatus.ERROR);
        }

        User user = new User(IdGenerator.generateId(), username, hashedPassword);

        context.users().add(user);
        sessionStore.register(new Session(session.key(), user));

        logger.logInfo("User registered: " + username);
        return new Response("User registered successfully.", ResponseStatus.OK);
    }

    @Override
    public CommandType getType() {
        return CommandType.REGISTER;
    }
}