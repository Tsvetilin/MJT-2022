package bg.sofia.uni.fmi.mjt.newsfeed.utils;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TimedCacheTest {

    TimedCache<String, String> cache = new TimedCache<>(1);

    @Test
    @Order(1)
    void testGet() {
        cache.put("test", "test");
        var result = cache.get("test");
        assertEquals("test", result, "Record should be preserved.");
    }

    @Test
    @Order(2)
    void testGetUnexisting() {
        var result = cache.get("test");
        assertNull(result, "Record should not exist.");
    }

    @Test
    @Order(3)
    void testGetTimeout() throws InterruptedException {
        cache.put("test", "test");
        Thread.sleep(2000);
        var result = cache.get("test");
        assertNull(result, "Record should be deleted after duration exceeds.");
    }

    @Test
    @Order(4)
    void testPut() {
        cache.put("test", "test");
        cache.put("test", "test2");
        var result = cache.get("test");
        assertEquals("test2", result, "Record should be updated.");
    }
}
