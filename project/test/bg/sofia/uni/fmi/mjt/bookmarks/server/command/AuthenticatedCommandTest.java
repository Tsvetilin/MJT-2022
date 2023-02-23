package bg.sofia.uni.fmi.mjt.bookmarks.server.command;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.DIContainer;
import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.InvalidBookmarkException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.external.UrlShortener;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.Logger;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.DatabaseContext;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.Repository;
import bg.sofia.uni.fmi.mjt.bookmarks.server.services.BookmarksService;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.Session;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.SessionStore;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.hasher.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthenticatedCommandTest {

    private final SessionStore sessionStore = mock(SessionStore.class);
    private final DatabaseContext context = mock(DatabaseContext.class);
    private final Logger logger = mock(Logger.class);
    private final SocketChannel socketChannel = mock(SocketChannel.class);
    private final CommandExecutor executor = CommandExecutor.configure(sessionStore, context, logger);
    private final User user = mock(User.class);
    private final Group group = mock(Group.class);
    private final Bookmark bookmark = mock(Bookmark.class);
    private final Session session = new Session(socketChannel, null);

    private final Repository<String, User> userRepository = mock(Repository.class);
    private final Repository<String, Group> groupRepository = mock(Repository.class);
    private final Repository<String, Bookmark> bookmarkRepository = mock(Repository.class);
    private final PasswordHasher passwordHasher = mock(PasswordHasher.class);
    private final UrlShortener urlShortener = mock(UrlShortener.class);
    private final BookmarksService bookmarksService = mock(BookmarksService.class);


    private static final String NAME = "name";
    private static final String URL = "url";

    @BeforeEach
    void setup() {
        when(context.users()).thenReturn(userRepository);
        when(context.bookmarks()).thenReturn(bookmarkRepository);
        when(context.groups()).thenReturn(groupRepository);
        when(sessionStore.hasSession(any())).thenReturn(true);
        when(sessionStore.getUser(any())).thenReturn(user);
        when(group.getName()).thenReturn(NAME);
        when(bookmark.getUrl()).thenReturn(URL);
        DIContainer.clear();
        DIContainer.register(PasswordHasher.class, passwordHasher);
        DIContainer.register(UrlShortener.class, urlShortener);
        DIContainer.register(BookmarksService.class, bookmarksService);
    }

    @Test
    void testFailUnauthenticated() {
        when(sessionStore.hasSession(session)).thenReturn(false);

        var result = executor.execute("add-to group url", session);

        verify(sessionStore, times(0)).getUser(session);
        assertEquals(ResponseStatus.ERROR, result.status(), "Not authenticated.");
    }

    @Test
    void testAddToCommandSuccessful() throws InvalidBookmarkException {
        when(user.getGroups()).thenReturn(List.of(group));
        when(user.getBookmarks()).thenReturn(new ArrayList<>());
        when(bookmarksService.generateBookmark(any(), any(), anyBoolean(), any())).thenReturn(bookmark);

        var result = executor.execute("add-to " + NAME + " url", session);

        verify(bookmarkRepository, times(1)).add(any());
        verify(bookmarksService, times(1)).generateBookmark(any(), any(), anyBoolean(), any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.OK, result.status(), "Invalid status");
    }

    @Test
    void testAddToCommandNoGroup() throws InvalidBookmarkException {
        when(user.getGroups()).thenReturn(List.of(group));
        when(bookmarksService.generateBookmark(any(), any(), anyBoolean(), any())).thenReturn(bookmark);

        var result = executor.execute("add-to nonexisitinggroup url", session);

        verify(bookmarkRepository, times(0)).add(any());
        verify(bookmarksService, times(0)).generateBookmark(any(), any(), anyBoolean(), any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }

    @Test
    void testAddToCommandBookmarkExists() throws InvalidBookmarkException {
        when(user.getGroups()).thenReturn(List.of(group));
        when(user.getBookmarks()).thenReturn(List.of(bookmark));

        var result = executor.execute("add-to " + NAME + " " + URL, session);

        verify(bookmarkRepository, times(0)).add(any());
        verify(bookmarksService, times(0)).generateBookmark(any(), any(), anyBoolean(), any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }

    @Test
    void testAddToCommandError() throws InvalidBookmarkException {
        when(user.getGroups()).thenReturn(List.of(group));
        when(user.getBookmarks()).thenReturn(new ArrayList<>());
        when(bookmarksService.generateBookmark(any(), any(), anyBoolean(), any())).thenThrow(
            InvalidBookmarkException.class);

        var result = executor.execute("add-to " + NAME + " url", session);

        verify(bookmarkRepository, times(0)).add(any());
        verify(bookmarksService, times(1)).generateBookmark(any(), any(), anyBoolean(), any());
        verify(logger, times(1)).logError(any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }

    @Test
    void testCleanupCommandSuccessful() throws InvalidBookmarkException {
        when(user.getGroups()).thenReturn(List.of(group));
        when(user.getBookmarks()).thenReturn(List.of(bookmark));
        when(bookmarksService.validateUrl(any())).thenReturn(false);

        var result = executor.execute("cleanup", session);

        verify(bookmarkRepository, times(1)).remove(any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.OK, result.status(), "Invalid status");
    }


    @Test
    void testCleanupCommandSuccessfulWithError() throws InvalidBookmarkException {
        when(user.getGroups()).thenReturn(List.of(group));
        when(user.getBookmarks()).thenReturn(List.of(bookmark));
        when(bookmarksService.validateUrl(any())).thenThrow(InvalidBookmarkException.class);

        var result = executor.execute("cleanup", session);

        verify(bookmarkRepository, times(0)).remove(any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.OK, result.status(), "Invalid status");
    }

    @Test
    void testImportChromeCommandSuccessful() throws InvalidBookmarkException {
        when(user.getGroups()).thenReturn(List.of(group));
        when(user.getBookmarks()).thenReturn(List.of(bookmark));
        when(bookmarksService.validateUrl(any())).thenReturn(false);

        var result = executor.execute("import-from-chrome url", session);

        verify(bookmarkRepository, times(1)).add(any());
        verify(logger, times(0)).logInfo(any());
        assertEquals(ResponseStatus.OK, result.status(), "Invalid status");
    }


    @Test
    void testRemoveFromCommandSuccessful() {
        when(user.getGroups()).thenReturn(List.of(group));
        when(user.getBookmarks()).thenReturn(List.of(bookmark));

        var result = executor.execute("remove-from " + NAME + " " + URL, session);

        verify(bookmarkRepository, times(1)).remove(any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.OK, result.status(), "Invalid status");
    }

    @Test
    void testRemoveFromCommandNoGroup() {
        when(user.getGroups()).thenReturn(new ArrayList<>());
        when(user.getBookmarks()).thenReturn(List.of(bookmark));

        var result = executor.execute("remove-from " + NAME + " " + URL, session);

        verify(bookmarkRepository, times(0)).remove(any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }
}