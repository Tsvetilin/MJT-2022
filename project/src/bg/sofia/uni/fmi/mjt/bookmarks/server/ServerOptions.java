package bg.sofia.uni.fmi.mjt.bookmarks.server;

import bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions.StopWordsException;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.DefaultLogger;
import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.Logger;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.DatabaseContext;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.DefaultSessionStore;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.SessionStore;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Service;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Stopwords;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ServerOptions {

    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = 8080;

    private final int port;
    private final String host;
    private final int bufferSize;
    private final DatabaseContext context;
    private final Logger logger;
    private final SessionStore sessionStore;

    private static final int ALLOWED_PORT_LB = 1000;

    private ServerOptions(ServerOptionsBuilder builder) {
        DIContainer.clear();

        Nullable.throwIfNull(builder.context);

        this.port = builder.port > ALLOWED_PORT_LB ? builder.port : DEFAULT_SERVER_PORT;
        this.bufferSize = builder.bufferSize > 0 ? builder.bufferSize : DEFAULT_BUFFER_SIZE;
        this.host = Nullable.orDefault(builder.host, DEFAULT_SERVER_HOST);
        this.context = builder.context;
        this.logger = Nullable.orDefault(builder.logger, DefaultLogger.getDefaultLogger());
        this.sessionStore = Nullable.orDefault(builder.sessionStore, new DefaultSessionStore());
        DIContainer.register(DatabaseContext.class, context);
        DIContainer.register(Logger.class, logger);
        DIContainer.register(SessionStore.class, sessionStore);
        for (var service : builder.serviceList.entrySet()) {
            DIContainer.register(service.getKey(), service.getValue());
        }

        try {
            Stopwords.load();
        } catch (StopWordsException e) {
            throw new RuntimeException(e);
        }
    }

    public static ServerOptionsBuilder create(int port) {
        return new ServerOptionsBuilder(port);
    }

    public int port() {
        return port;
    }

    public String host() {
        return host;
    }

    public int bufferSize() {
        return bufferSize;
    }

    public DatabaseContext context() {
        return context;
    }

    public Logger logger() {
        return logger;
    }

    public SessionStore sessionStore() {
        return sessionStore;
    }

    public static class ServerOptionsBuilder {

        private final int port;
        private int bufferSize = 0;
        private String host;
        private DatabaseContext context;
        private Logger logger;
        private SessionStore sessionStore;
        private final Map<Type, Service> serviceList;

        private ServerOptionsBuilder(int port) {
            this.port = port;

            this.serviceList = new HashMap<>();
        }

        public ServerOptionsBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public ServerOptionsBuilder setBufferSize(int size) {
            this.bufferSize = size;
            return this;
        }

        public ServerOptionsBuilder addDatabaseContext(DatabaseContext context) {
            this.context = context;
            return this;
        }

        public ServerOptionsBuilder addLogger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public ServerOptionsBuilder addSessionStore(SessionStore store) {
            this.sessionStore = store;
            return this;
        }

        public ServerOptionsBuilder addService(Type type, Service service) {
            this.serviceList.put(type, service);
            return this;
        }

        public ServerOptions build() {
            return new ServerOptions(this);
        }
    }
}
