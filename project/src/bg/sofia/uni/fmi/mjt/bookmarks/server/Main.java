package bg.sofia.uni.fmi.mjt.bookmarks.server;

import bg.sofia.uni.fmi.mjt.bookmarks.server.external.BitlyUrlShortener;
import bg.sofia.uni.fmi.mjt.bookmarks.server.external.UrlShortener;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.DefaultLogger;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.Severity;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.providers.DefaultConsoleProvider;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.providers.DefaultFileProvider;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.FileDatabaseContext;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.FileRepository;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository.FileRepositoryOptions;
import bg.sofia.uni.fmi.mjt.bookmarks.server.services.BookmarksService;
import bg.sofia.uni.fmi.mjt.bookmarks.server.services.DefaultBookmarksService;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.DefaultSessionStore;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.datetime.DefaultDateTimeProvider;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.hasher.DefaultPasswordHasher;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.hasher.PasswordHasher;

import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.util.Scanner;

public class Main {

    private static final int PORT = 8080;

    public static void main(String... args) throws InterruptedException {
        var options = ServerOptions
            .create(PORT)
            .addSessionStore(new DefaultSessionStore())
            .addLogger(
                DefaultLogger
                    .configure()
                    .addConsoleLogging(Severity.INFO)
                    .configureConsoleProvider(new DefaultConsoleProvider())
                    .addFileLogging(Severity.INFO)
                    .configureFileProvider(new DefaultFileProvider())
                    .configureDateTimeProvider(new DefaultDateTimeProvider())
                    .build()
            )
            .addDatabaseContext(
                new FileDatabaseContext(
                    new FileRepository<>(
                        FileRepositoryOptions.create("./db/users.json").build(),
                        String.class,
                        User.class),
                    new FileRepository<>(
                        FileRepositoryOptions.create("./db/bookmarks.json").build(),
                        String.class,
                        Bookmark.class),
                    new FileRepository<>(
                        FileRepositoryOptions.create("./db/groups.json").build(),
                        String.class,
                        Group.class)
                )
            )
            .addService(PasswordHasher.class, new DefaultPasswordHasher(new SecureRandom()))
            .addService(UrlShortener.class, new BitlyUrlShortener(HttpClient.newHttpClient(), "API KEY HERE"))
            .addService(BookmarksService.class, new DefaultBookmarksService(HttpClient.newHttpClient()))
            .build();

        Thread serverThread = null;
        Server server = null;

        var scanner = new Scanner(System.in);
        System.out.println("<Bookmarks manager server>");
        printCommands();

        while (true) {
            System.out.print(">> ");
            String cmd = scanner.nextLine();

            switch (cmd) {
                case "start" -> {
                    if (server != null) {
                        System.out.println("Server already started. ");
                        break;
                    }

                    server = new Server(options);
                    serverThread = new Thread(server);
                    serverThread.start();

                    System.out.println("Starting server on port " + PORT + "...");
                }

                case "stop" -> {
                    if (server == null) {
                        System.out.println("Server not started yet.");
                        break;
                    }

                    System.out.println("Stopping server...");
                    server.stopServer();
                    serverThread.interrupt();
                    serverThread.join();
                    server = null;
                }

                case "exit" -> {
                    if (server != null) {
                        System.out.println("Server still running. Cannot exit.");
                        break;
                    }

                    return;
                }

                default -> {
                    System.out.println("Invalid command.");
                    printCommands();
                }
            }

        }
    }

    private static void printCommands() {
        System.out.println("Available commands: ");
        System.out.println("start : starts the server");
        System.out.println("stop  : stops the server");
        System.out.println("exit  : exits the server console");
        System.out.println();
    }


}
