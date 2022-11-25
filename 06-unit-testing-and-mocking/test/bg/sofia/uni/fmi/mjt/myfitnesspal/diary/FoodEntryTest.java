package bg.sofia.uni.fmi.mjt.myfitnesspal.diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FoodEntryTest {

    @Test
    void testCreateEntryShouldThrowWhenInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new FoodEntry(null, 20, null));
        assertThrows(IllegalArgumentException.class, () -> new FoodEntry("Test", 20, null));
        assertThrows(IllegalArgumentException.class, () -> new FoodEntry("Test", -5, new NutritionInfo(10, 20, 70)));
    }
}
