package bg.sofia.uni.fmi.mjt.bookmarks.server.persistence.repository;

import bg.sofia.uni.fmi.mjt.bookmarks.server.models.User;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.serialize.Serializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileRepositoryTest {

    FileRepositoryOptions options = mock(FileRepositoryOptions.class);
    FileRepository<String, User> repo = new FileRepository<>(options, String.class, User.class);
    Reader reader = new StringReader("not empty");
    Writer writer = mock(Writer.class);
    Serializer serializer = mock(Serializer.class);

    @BeforeEach
    void setup() {
        repo.add(new User("key", "dsa", " asd"));
        when(options.serializer()).thenReturn(serializer);
        when(options.reader()).thenReturn(reader);
        when(options.writer()).thenReturn(writer);
    }

    @Test
    void testLoadSuccess() {
        when(serializer.deserialize(any(), any())).thenReturn(new HashMap<String, User>());
        assertDoesNotThrow(() -> repo.load());
    }

    @Test
    void testLoadFail() {
        when(serializer.deserialize(any(), any())).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> repo.load());
    }

    @Test
    void testAdd() {
        assertDoesNotThrow(() -> repo.add(new User("key2", "dsa", " asd")));
        assertTrue(repo.contains("key2"), "Invalid data returned.");
    }

    @Test
    void testRemove() {
        assertDoesNotThrow(() -> repo.remove("key"));
        assertFalse(repo.contains("key"), "Invalid data returned.");
    }

    @Test
    void testGetAll() {
        var res = repo.getAll();
        assertEquals(1, res.size(), "Invalid data returned.");
    }

    @Test
    void testFind() {
        assertTrue(repo.find(x -> x.getUsername().equals("nonexisting")).isEmpty(), "Invalid data returned.");
    }

    @Test
    void testAny() {
        assertTrue(repo.any(x -> x.getKey().equals("key")), "Invalid data returned.");
    }

    @Test
    void testPersist() {
        assertDoesNotThrow(() -> repo.persist(), "Persistence should succeed.");
    }


    @Test
    void testPersistFail() throws IOException {
        doThrow(IOException.class).when(writer).write(anyString());
        when(serializer.serialize(any())).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> repo.persist(), "Persistence should fail due to IO failure");
    }
}
