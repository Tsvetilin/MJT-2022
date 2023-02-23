package bg.sofia.uni.fmi.mjt.bookmarks.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.AuthenticatedCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;

import java.util.List;
import java.util.stream.Collectors;

public abstract class SearchCommand extends AuthenticatedCommand {
    protected abstract List<Bookmark> searchByCriteria();

    @Override
    protected final Response authenticatedExecute() {

        var result =
            searchByCriteria().stream().map(Bookmark::listPreview).collect(Collectors.joining(System.lineSeparator()));

        if (result.isEmpty() || result.isBlank()) {
            result = "No bookmarks match the criteria.";
        }

        logger.logInfo("Extracted bookmarks for user " + user.getUsername());
        return new Response(result, ResponseStatus.OK);
    }


    @Override
    public CommandType getType() {
        return CommandType.SEARCH;
    }
}