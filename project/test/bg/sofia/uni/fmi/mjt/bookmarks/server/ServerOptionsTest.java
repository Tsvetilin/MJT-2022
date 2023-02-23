package bg.sofia.uni.fmi.mjt.bookmarks.server;

import bg.sofia.uni.fmi.mjt.bookmarks.server.external.UrlShortener;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.Logger;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.FileDatabaseContext;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.SessionStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ServerOptionsTest {

    FileDatabaseContext context = mock(FileDatabaseContext.class);
    Logger logger = mock(Logger.class);
    SessionStore sessionStore = mock(SessionStore.class);
    UrlShortener service = mock(UrlShortener.class);

    @Test
    void test() {
        var options = ServerOptions
            .create(8080)
            .addDatabaseContext(context)
            .addLogger(logger)
            .addSessionStore(sessionStore)
            .setHost("host")
            .setBufferSize(1024)
            .addService(UrlShortener.class, service)
            .build();

        assertEquals(context,options.context(),"invalid option saved.");
        assertEquals(logger,options.logger(),"invalid option saved.");
        assertEquals(sessionStore,options.sessionStore(),"invalid option saved.");
        assertEquals("host",options.host(),"invalid option saved.");
        assertEquals(8080,options.port(),"invalid option saved.");
        assertEquals(1024,options.bufferSize(),"invalid option saved.");
    }
}
