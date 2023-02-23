package bg.sofia.uni.fmi.mjt.bookmarks.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.AuthenticatedCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;

import java.util.stream.Collectors;

public class ListCommand extends AuthenticatedCommand {

    @Override
    protected Response authenticatedExecute() {
        var result = user.getBookmarks().stream().map(Bookmark::listPreview)
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