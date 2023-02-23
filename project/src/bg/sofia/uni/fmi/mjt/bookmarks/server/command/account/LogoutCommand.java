package bg.sofia.uni.fmi.mjt.bookmarks.server.command.account;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandBase;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandType;

public class LogoutCommand extends CommandBase {

    @Override
    public Response execute() {

        if (!sessionStore.hasSession(session)) {
            logger.logInfo("Logout attempt for not logged user.");
            return new Response("User not logged in.", ResponseStatus.ERROR);
        }

        logger.logInfo("User logged out: " + sessionStore.getUser(session).getUsername());

        sessionStore.remove(session);

        return new Response("Logged out successfully.", ResponseStatus.OK);
    }


    @Override
    public CommandType getType() {
        return CommandType.LOGOUT;
    }
}