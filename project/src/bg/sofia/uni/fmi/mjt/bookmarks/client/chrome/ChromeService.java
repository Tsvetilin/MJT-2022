package bg.sofia.uni.fmi.mjt.bookmarks.client.chrome;

import bg.sofia.uni.fmi.mjt.bookmarks.client.exceptions.ChromeException;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ChromeService {

    public static List<String> getBookmarks() throws ChromeException {
        String osName = System.getProperty("os.name");

        String path;
        if (osName.toLowerCase().contains("linux")) {
            path = "~/.config/google-chrome/Default/";
        } else if (osName.toLowerCase().contains("windows")) {
            path = System.getProperty("user.home") + "\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Bookmarks";
        } else if (osName.toLowerCase().contains("mac")) {
            path =
                "/Users/" + System.getProperty("user.name") + "/Library/Application\\ Support/Google/Chrome/Bookmarks";
        } else {
            throw new ChromeException("Cannot extract bookmarks. Unknown OS.");
        }

        Gson gson = new Gson();

        try (var reader = Files.newBufferedReader(Path.of(path))) {
            return gson
                .fromJson(reader, ChromeBookmarks.class)
                .roots()
                .getAll()
                .stream()
                .map(ChromeBookmark::url)
                .distinct()
                .toList();
        } catch (IOException e) {
            throw new ChromeException("Error extracting bookmarks from system files.");
        }
    }
}
