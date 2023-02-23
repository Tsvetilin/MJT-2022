package bg.sofia.uni.fmi.mjt.bookmarks.server.command;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.DIContainer;
import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.PasswordHasherException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.Logger;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.DatabaseContext;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.Repository;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.Session;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.SessionStore;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.hasher.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnauthenticatedCommandTest {

    private final SessionStore sessionStore = mock(SessionStore.class);
    private final DatabaseContext context = mock(DatabaseContext.class);
    private final Logger logger = mock(Logger.class);
    private final SocketChannel socketChannel = mock(SocketChannel.class);

    private final CommandExecutor executor = CommandExecutor.configure(sessionStore, context, logger);

    private final User user = mock(User.class);
    private final Session session = new Session(socketChannel, null);
    private final Repository<String, User> userRepository = mock(Repository.class);

    private final PasswordHasher passwordHasher = mock(PasswordHasher.class);


    @BeforeEach
    void setup() {
        when(context.users()).thenReturn(userRepository);
        DIContainer.clear();
        DIContainer.register(PasswordHasher.class, passwordHasher);
    }

    @Test
    void testUnknownCommand() {
        var result = executor.execute("some rnd cmd", session);
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }

    @Test
    void testHelpCommand() {
        var result = executor.execute("help", session);
        assertEquals(ResponseStatus.OK, result.status(), "Invalid status");
    }

    @Test
    void testLoginCommandSuccessful() throws PasswordHasherException {
        when(sessionStore.hasSession(session)).thenReturn(false);
        when(userRepository.find(any())).thenReturn(Optional.of(user));
        when(passwordHasher.verify(any(), any())).thenReturn(true);

        var result = executor.execute("login username password", session);

        verify(sessionStore, times(1)).register(any());
        verify(passwordHasher, times(1)).verify(any(), any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.OK, result.status(), "Invalid status");
    }

    @Test
    void testLoginCommandNoUser() throws PasswordHasherException {
        when(sessionStore.hasSession(session)).thenReturn(false);
        when(userRepository.find(any())).thenReturn(Optional.empty());

        var result = executor.execute("login username password", session);

        verify(sessionStore, times(0)).register(any());
        verify(passwordHasher, times(0)).verify(any(), any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }

    @Test
    void testLoginCommandHasSession() throws PasswordHasherException {
        when(sessionStore.hasSession(session)).thenReturn(true);

        var result = executor.execute("login username password", session);

        verify(sessionStore, times(0)).register(any());
        verify(passwordHasher, times(0)).verify(any(), any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }


    @Test
    void testLoginCommandInvalidPassword() throws PasswordHasherException {
        when(sessionStore.hasSession(session)).thenReturn(false);
        when(userRepository.find(any())).thenReturn(Optional.of(user));
        when(passwordHasher.verify(any(), any())).thenReturn(false);

        var result = executor.execute("login username password", session);

        verify(sessionStore, times(0)).register(any());
        verify(passwordHasher, times(1)).verify(any(), any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }

    @Test
    void testLoginCommandHasherException() throws PasswordHasherException {
        when(sessionStore.hasSession(session)).thenReturn(false);
        when(userRepository.find(any())).thenReturn(Optional.of(user));
        when(passwordHasher.verify(any(), any())).thenThrow(PasswordHasherException.class);

        var result = executor.execute("login username password", session);

        verify(sessionStore, times(0)).register(any());
        verify(passwordHasher, times(1)).verify(any(), any());
        verify(logger, times(1)).logError(any());
        verify(logger, times(1)).logException(any(), any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }


    @Test
    void testLogoutSuccessful() {
        when(sessionStore.hasSession(session)).thenReturn(true);
        when(sessionStore.getUser(session)).thenReturn(user);

        var result = executor.execute("logout", session);

        verify(sessionStore, times(1)).remove(any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.OK, result.status(), "Invalid status");
    }

    @Test
    void testLogoutFail() {
        when(sessionStore.hasSession(session)).thenReturn(false);

        var result = executor.execute("logout", session);

        verify(sessionStore, times(0)).remove(any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }

    @Test
    void testRegisterCommandSuccessful() throws PasswordHasherException {
        when(sessionStore.hasSession(session)).thenReturn(false);
        when(userRepository.any(any())).thenReturn(false);
        when(passwordHasher.hash(any())).thenReturn("hashedpass");

        var result = executor.execute("register username Abcdef1.", session);

        verify(sessionStore, times(1)).register(any());
        verify(userRepository, times(1)).add(any());
        verify(passwordHasher, times(1)).hash(any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.OK, result.status(), "Invalid status");
    }

    @Test
    void testRegisterCommandUserLogged() throws PasswordHasherException {
        when(sessionStore.hasSession(session)).thenReturn(true);

        var result = executor.execute("register username Abcdef1.", session);

        verify(sessionStore, times(0)).register(any());
        verify(userRepository, times(0)).add(any());
        verify(passwordHasher, times(0)).hash(any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }

    @Test
    void testRegisterCommandUserExists() throws PasswordHasherException {
        when(sessionStore.hasSession(session)).thenReturn(false);
        when(userRepository.any(any())).thenReturn(true);
        when(passwordHasher.hash(any())).thenReturn("hashedpass");

        var result = executor.execute("register username Abcdef1.", session);

        verify(sessionStore, times(0)).register(any());
        verify(userRepository, times(0)).add(any());
        verify(passwordHasher, times(0)).hash(any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }

    @Test
    void testRegisterCommandInvalidPassword() throws PasswordHasherException {
        when(sessionStore.hasSession(session)).thenReturn(false);
        when(userRepository.any(any())).thenReturn(true);
        when(passwordHasher.hash(any())).thenReturn("hashedpass");

        var result = executor.execute("register username invalidPassword", session);

        verify(sessionStore, times(0)).register(any());
        verify(userRepository, times(0)).add(any());
        verify(passwordHasher, times(0)).hash(any());
        verify(logger, times(1)).logInfo(any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }

    @Test
    void testRegisterCommandHashError() throws PasswordHasherException {
        when(sessionStore.hasSession(session)).thenReturn(false);
        when(userRepository.any(any())).thenReturn(false);
        when(passwordHasher.hash(any())).thenThrow(PasswordHasherException.class);

        var result = executor.execute("register username Abcdef1.", session);

        verify(sessionStore, times(0)).register(any());
        verify(userRepository, times(0)).add(any());
        verify(passwordHasher, times(1)).hash(any());
        verify(logger, times(1)).logError(any());
        verify(logger, times(1)).logException(any(), any());
        assertEquals(ResponseStatus.ERROR, result.status(), "Invalid status");
    }
}
