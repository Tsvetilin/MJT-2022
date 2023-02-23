package bg.sofia.uni.fmi.mjt.bookmarks.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.AuthenticatedCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;

import java.util.stream.Collectors;

public class ListByGroupCommand extends AuthenticatedCommand {

    private final String group;

    public ListByGroupCommand(String group) {
        this.group = group;
        Nullable.throwIfNull(group);
    }

    @Override
    protected Response authenticatedExecute() {
        if (user.getGroups().stream().noneMatch(x -> x.getName().equals(group))) {
            logger.logInfo(
                "User " + user.getUsername() + " tried to list all bookmarks for non-existing group: " + group);
            return new Response("No such group exists.", ResponseStatus.ERROR);
        }

        var result =
            user.getBookmarks()
                .stream()
                .filter(x -> x.getGroup().getName().equals(group))
                .map(Bookmark::listPreview)
                .collect(Collectors.joining(System.lineSeparator()));

        if (result.isBlank() || result.isEmpty()) {
            result = "No bookmarks found.";
        }

        logger.logInfo("Successfully listed all bookmarks for user " + user.getUsername());
        return new Response(result, ResponseStatus.OK);
    }


    @Override
    public CommandType getType() {
        return CommandType.LIST;
    }
}
