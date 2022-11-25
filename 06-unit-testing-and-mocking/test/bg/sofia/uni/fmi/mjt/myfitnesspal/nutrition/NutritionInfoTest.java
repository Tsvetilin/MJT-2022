package bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NutritionInfoTest {
    @Test
    void testCreateNutritionInfoShouldThrowWhenNegativeAmount(){
        assertThrows(IllegalArgumentException.class,()->new NutritionInfo(-1,20,20),"Should throw when negative value supplied for quantity");
        assertThrows(IllegalArgumentException.class,()->new NutritionInfo(20,-1,20),"Should throw when negative value supplied for quantity");
        assertThrows(IllegalArgumentException.class,()->new NutritionInfo(20,20,-1),"Should throw when negative value supplied for quantity");
    }

    @Test
    void testCreateNutritionInfoShouldThrowWhenNotMaxPercent(){
        assertThrows(IllegalArgumentException.class,()->new NutritionInfo(20,20,20),"Should throw when not sum equals 100");
    }

    @Test
    void testCaloriesShouldCalculateCorrectly(){
        var obj = new NutritionInfo(20,20,60);
        var result = obj.calories();
        assertEquals(result,
                obj.proteins() * MacroNutrient.PROTEIN.calories +
                obj.fats() * MacroNutrient.FAT.calories +
                obj.carbohydrates() * MacroNutrient.CARBOHYDRATE.calories,
                "Should calculate correctly");
    }
}
