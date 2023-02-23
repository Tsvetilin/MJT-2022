package bg.sofia.uni.fmi.mjt.bookmarks.server.command;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;

public abstract class AuthenticatedCommand extends CommandBase {

    protected User user;

    protected abstract Response authenticatedExecute();

    @Override
    public final Response execute() {
        if (!sessionStore.hasSession(session)) {
            logger.logInfo("User tried to execute authentication required command.");
            return new Response("You must log in in order to execute such commands.", ResponseStatus.ERROR);
        }

        user = sessionStore.getUser(session);

        return authenticatedExecute();
    }
}
