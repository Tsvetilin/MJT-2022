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
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.hasher.PasswordHasher;

import java.util.Optional;

public class LoginCommand extends CommandBase {

    private final String username;
    private final String password;

    public LoginCommand(String username, String password) {
        this.username = username;
        this.password = password;

        Nullable.throwIfAnyNull(username, password);
    }

    @Override
    public Response execute() {
        if (sessionStore.hasSession(session)) {
            logger.logInfo("User already logged in: " + username);
            return new Response("User already logged in.", ResponseStatus.ERROR);
        }

        Optional<User> userOpt = context.users().find(user -> user.getUsername().equals(username));

        if (userOpt.isEmpty()) {
            logger.logInfo("Invalid username when logging: " + username);
            return new Response("Invalid username or password.", ResponseStatus.ERROR);
        }

        User user = userOpt.get();


        try {
            if (!DIContainer.request(PasswordHasher.class).verify(password, user.getPassword())) {
                logger.logInfo("Invalid password when logging: " + username);
                return new Response("Invalid username or password.", ResponseStatus.ERROR);
            }
        } catch (PasswordHasherException e) {
            String traceId = IdGenerator.generateId();
            logger.logException(e, traceId);
            logger.logError("Server error on login request. Trace id: " + traceId);
            return new Response("Internal server error. Trace id: " + traceId, ResponseStatus.ERROR);
        }

        sessionStore.register(new Session(session.key(), user));

        logger.logInfo("User logged successfully: " + username);
        return new Response("User logged successfully", ResponseStatus.OK);
    }


    @Override
    public CommandType getType() {
        return CommandType.LOGIN;
    }
}