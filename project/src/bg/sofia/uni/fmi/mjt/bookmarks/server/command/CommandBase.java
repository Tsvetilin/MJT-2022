package bg.sofia.uni.fmi.mjt.bookmarks.server.command;

import bg.sofia.uni.fmi.mjt.bookmarks.server.logging.Logger;
import bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.DatabaseContext;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.Session;
import bg.sofia.uni.fmi.mjt.bookmarks.server.sessions.SessionStore;

public abstract class CommandBase implements Command {

    protected SessionStore sessionStore;
    protected DatabaseContext context;
    protected Session session;
    protected Logger logger;

    protected CommandBase() {
        sessionStore = null;
        context = null;
    }

    @Override
    public final Command addDependencies(SessionStore sessionStore, DatabaseContext context) {
        this.context = context;
        this.sessionStore = sessionStore;
        return this;
    }

    @Override
    public final Command addSessionContext(Session session) {
        this.session = session;
        return this;
    }

    @Override
    public final Command addLogger(Logger logger) {
        this.logger = logger;
        return this;
    }
}
