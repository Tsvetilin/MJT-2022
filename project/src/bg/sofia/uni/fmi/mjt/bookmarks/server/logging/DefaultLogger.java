package bg.sofia.uni.fmi.mjt.bookmarks.server.logging;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.LoggerOperationException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.providers.ConsoleProvider;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.providers.DefaultConsoleProvider;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.providers.DefaultFileProvider;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.providers.FileProvider;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.datetime.DateTimeProvider;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.datetime.DefaultDateTimeProvider;

import java.time.LocalDateTime;

public class DefaultLogger implements Logger {

    private final Severity consoleSeverity;
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


    public static class BookmarksLoggerBuilder {

        private Severity consoleSeverity = Severity.NONE;
        private Severity fileSeverity = Severity.NONE;
        private DateTimeProvider dateTimeProvider;
        private ConsoleProvider consoleProvider;
        private FileProvider fileProvider;

        private BookmarksLoggerBuilder() {
        }

        public BookmarksLoggerBuilder addConsoleLogging(Severity severity) {
            consoleSeverity = severity;
            return this;
        }

        public BookmarksLoggerBuilder addFileLogging(Severity severity) {
            fileSeverity = severity;
            return this;
        }

        public BookmarksLoggerBuilder addConsoleLogging(Severity severity, ConsoleProvider provider) {
            consoleProvider = provider;
            return addConsoleLogging(severity);
        }

        public BookmarksLoggerBuilder addFileLogging(Severity severity, FileProvider provider) {
            fileProvider = provider;
            return addFileLogging(severity);
        }

        public BookmarksLoggerBuilder configureDateTimeProvider(DateTimeProvider provider) {
            dateTimeProvider = provider;
            return this;
        }

        public BookmarksLoggerBuilder configureConsoleProvider(ConsoleProvider provider) {
            consoleProvider = provider;
            return this;
        }

        public BookmarksLoggerBuilder configureFileProvider(FileProvider provider) {
            fileProvider = provider;
            return this;
        }

        public DefaultLogger build() {
            return new DefaultLogger(this);
        }

    }
}
