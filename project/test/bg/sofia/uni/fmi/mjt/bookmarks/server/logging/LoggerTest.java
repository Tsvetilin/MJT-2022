package bg.sofia.uni.fmi.mjt.bookmarks.server.logging;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.LoggerOperationException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.providers.ConsoleProvider;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.providers.FileProvider;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.datetime.DateTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoggerTest {

    ConsoleProvider consoleProvider = mock(ConsoleProvider.class);
    FileProvider fileProvider = mock(FileProvider.class);
    DateTimeProvider dateTimeProvider = mock(DateTimeProvider.class);

    Logger logger = DefaultLogger.configure().addConsoleLogging(Severity.INFO).configureConsoleProvider(consoleProvider)
        .addFileLogging(Severity.ERROR).configureFileProvider(fileProvider).configureDateTimeProvider(dateTimeProvider)
        .build();

    private final String message = "msg";
    private final LocalDateTime time = LocalDateTime.of(1, 1, 1, 1, 1);

    @BeforeEach
    void setup() {
        when(dateTimeProvider.getCurrentTime()).thenReturn(time);
    }

    @Test
    void testLogInfo() throws LoggerOperationException {
        logger.logInfo(message);
        verify(consoleProvider, times(1)).write(anyString());
        verify(fileProvider, times(0)).write(anyString());
        verify(fileProvider, times(0)).writeError(any(Exception.class), anyString());
        verify(dateTimeProvider, times(1)).getCurrentTime();
    }

    @Test
    void testLogWarning() throws LoggerOperationException {
        logger.logWarning(message);
        verify(consoleProvider, times(1)).write(anyString());
        verify(fileProvider, times(0)).write(anyString());
        verify(fileProvider, times(0)).writeError(any(Exception.class), anyString());
        verify(dateTimeProvider, times(1)).getCurrentTime();
    }

    @Test
    void testLogError() throws LoggerOperationException {
        logger.logError(message);
        verify(consoleProvider, times(1)).write(anyString());
        verify(fileProvider, times(1)).write(anyString());
        verify(fileProvider, times(0)).writeError(any(Exception.class), anyString());
        verify(dateTimeProvider, times(1)).getCurrentTime();
    }

    @Test
    void testLogException() throws LoggerOperationException {
        logger.logException(new Exception(), "id");
        verify(consoleProvider, times(0)).write(anyString());
        verify(fileProvider, times(0)).write(anyString());
        verify(fileProvider, times(1)).writeError(any(Exception.class), anyString());
    }

    @Test
    void testLogThrow() throws LoggerOperationException {
        doThrow(LoggerOperationException.class).when(fileProvider).write(anyString());

        assertThrows(RuntimeException.class, () -> logger.logError(message));
    }

    @Test
    void testLogThrowConsole() throws LoggerOperationException {
        doThrow(LoggerOperationException.class).when(consoleProvider).write(anyString());

        assertThrows(RuntimeException.class, () -> logger.logError(message));
    }
}


/*
* private final Severity consoleSeverity;
    private final Severity fileSeverity;

    private final DateTimeProvider dateTimeProvider;
    private final ConsoleProvider consoleProvider;
    private final FileProvider fileProvider;

    private DefaultLogger(BookmarksLoggerBuilder builder) {
        this.consoleSeverity = builder.consoleSeverity;
        this.fileSeverity = builder.fileSeverity;

        this.dateTimeProvider = Nullable.orDefault(builder.dateTimeProvider, new DefaultDateTimeProvider());
        this.consoleProvider = Nullable.orDefault(builder.consoleProvider, new DefaultConsoleProvider());
        this.fileProvider = Nullable.orDefault(builder.fileProvider, new DefaultFileProvider());
    }

    public static BookmarksLoggerBuilder configure() {
        return new BookmarksLoggerBuilder();
    }

    public static DefaultLogger getDefaultLogger() {
        return new BookmarksLoggerBuilder().addConsoleLogging(Severity.INFO)
            .addFileLogging(Severity.INFO).build();
    }


    @Override
    public void logInfo(String message) {
        log(Severity.INFO, message);
    }

    @Override
    public void logWarning(String message) {
        log(Severity.WARN, message);
    }


    @Override
    public void logError(String message) {
        log(Severity.ERROR, message);
    }

    @Override
    public void logException(Exception e, String id) {
        try {
            fileProvider.writeError(e, id);
        } catch (LoggerOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void log(Severity severity, String message) {

        var dateTime = dateTimeProvider.getCurrentTime();

        if (fileSeverity.getValue() <= severity.getValue()) {
            logFile(severity, dateTime, message);
        }

        if (consoleSeverity.getValue() <= severity.getValue()) {
            logConsole(severity, dateTime, message);
        }
    }

    private String getMessage(Severity severity, LocalDateTime dateTime, String message) {
        return ("[" + severity.name() + "] [" + dateTime + "] " + message).trim() + System.lineSeparator();
    }

    private void logFile(Severity severity, LocalDateTime dateTime, String message) {
        try {
            fileProvider.write(getMessage(severity, dateTime, message));
        } catch (LoggerOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void logConsole(Severity severity, LocalDateTime dateTime, String message) {
        try {
            consoleProvider.write(getMessage(severity, dateTime, message));
        } catch (LoggerOperationException e) {
            throw new RuntimeException(e);
        }
    }
*
* */