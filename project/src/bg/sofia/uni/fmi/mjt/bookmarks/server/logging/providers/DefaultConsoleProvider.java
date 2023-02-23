package bg.sofia.uni.fmi.mjt.bookmarks.server.logging.providers;

public class DefaultConsoleProvider implements ConsoleProvider {
    @Override
    public void write(String str) {
        System.out.print(str);
    }
}
