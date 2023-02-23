package bg.sofia.uni.fmi.mjt.bookmarks.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.AuthenticatedCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;

public class RemoveFromCommand extends AuthenticatedCommand {

    private final String group;
    private final String url;

    public RemoveFromCommand(String group, String url) {
        this.group = group;
        this.url = url;

        Nullable.throwIfAnyNull(url, group);
    }

    @Override
    protected Response authenticatedExecute() {

        if (user.getGroups().stream().noneMatch(x -> x.getName().equals(group))) {
            logger.logInfo("Invalid group specified " + group + " for user " + user.getUsername());
            return new Response("Invalid group specified.", ResponseStatus.ERROR);
        }

        if (user.getBookmarks().stream().noneMatch(x -> x.getUrl().equals(url))) {
            logger.logInfo("User " + user.getUsername() + " tried to remove non-existing bookmark " + url);
            return new Response("Bookmark does not exists.", ResponseStatus.ERROR);
        }

        Bookmark bookmark = user.getBookmarks().stream().filter(x -> x.getUrl().equals(url)).findFirst().orElseThrow();

        context.bookmarks().remove(bookmark.getKey());

        logger.logInfo("User " + user.getUsername() + " removed bookmark " + url + " from group " + group);
        return new Response("Bookmark removed successfully.", ResponseStatus.OK);
    }


    @Override
    public CommandType getType() {
        return CommandType.REMOVE_FROM;
    }
}