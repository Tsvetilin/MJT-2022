package bg.sofia.uni.fmi.mjt.bookmarks.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.DIContainer;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.AuthenticatedCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.InvalidBookmarkException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.services.BookmarksService;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.IdGenerator;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;

public class AddToCommand extends AuthenticatedCommand {

    private final String group;
    private final String url;
    private final boolean isShortened;

    public AddToCommand(String url, String group, boolean isShortened) {
        this.group = group;
        this.url = url;
        this.isShortened = isShortened;

        Nullable.throwIfAnyNull(group, url);
    }

    @Override
    protected Response authenticatedExecute() {

        var actualGroup = user.getGroups()
            .stream()
            .filter(x -> x.getName().equals(group))
            .findFirst()
            .orElse(null);

        if (actualGroup == null) {
            logger.logInfo("Invalid group specified " + group + " for user " + user.getUsername());
            return new Response("Invalid group specified.", ResponseStatus.ERROR);
        }

        if (user.getBookmarks().stream().anyMatch(x -> x.getUrl().equals(url))) {
            logger.logInfo("User " + user.getUsername() + " tried to add existing bookmark " + url);
            return new Response("Bookmark already exists.", ResponseStatus.ERROR);
        }

        Bookmark bookmark;
        try {
            bookmark =
                DIContainer.request(BookmarksService.class).generateBookmark(url, actualGroup, isShortened, user);
        } catch (InvalidBookmarkException e) {
            String traceId = IdGenerator.generateId();
            logger.logError("Server error on saving bookmark. Trace id: " + traceId);
            logger.logException(e, traceId);
            return new Response("Invalid url provided. Trace id: " + traceId, ResponseStatus.ERROR);
        }

        context.bookmarks().add(bookmark);

        logger.logInfo("Bookmark " + url + " created for user " + user.getUsername());
        return new Response("Bookmark added successfully.", ResponseStatus.OK);
    }


    @Override
    public CommandType getType() {
        return CommandType.ADD_TO;
    }
}