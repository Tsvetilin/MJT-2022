package bg.sofia.uni.fmi.mjt.bookmarks.client.chrome;

import bg.sofia.uni.fmi.mjt.bookmarks.client.exceptions.ChromeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ChromeServiceTest {

    @Test
    void testGetBookmarks() {
        try {
            var result = ChromeService.getBookmarks();
            assertEquals(result.size(), result.stream().filter(x -> x.startsWith("http")).toList().size(),
                "All must be valid urls");
        } catch (ChromeException e) {
            // ignored;
        }
    }
}
