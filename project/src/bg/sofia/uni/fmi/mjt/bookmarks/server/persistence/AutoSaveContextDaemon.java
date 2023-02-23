package bg.sofia.uni.fmi.mjt.bookmarks.server.persistence;

public class AutoSaveContextDaemon extends Thread {

    private final DatabaseContext context;
    private final int interval;
    private boolean isRunning;

    public AutoSaveContextDaemon(DatabaseContext context, int interval) {
        this.context = context;
        this.interval = interval;
    }

    public void stopDaemon() {
        isRunning = false;
    }

    @Override
    public void start() {
        try {
            while (isRunning) {
                context.persist();
                wait(interval);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
