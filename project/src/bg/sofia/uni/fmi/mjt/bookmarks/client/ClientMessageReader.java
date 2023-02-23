package bg.sofia.uni.fmi.mjt.bookmarks.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;

public class ClientMessageReader implements Runnable {

    private final BufferedReader reader;
    private boolean isRunning;

    public ClientMessageReader(BufferedReader reader) {
        this.reader = reader;
        isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        String line;
        try {
            while (!Thread.interrupted()) {
                if ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (ClosedByInterruptException ignored) {
            // ignored
        } catch (IOException e) {
            System.err.println("[ Disconnected ] Connection is closed, communication terminated.");
        }

        isRunning = false;
    }

}