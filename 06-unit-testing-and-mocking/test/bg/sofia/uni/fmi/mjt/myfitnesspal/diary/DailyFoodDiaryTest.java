package bg.sofia.uni.fmi.mjt.myfitnesspal.diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.exception.UnknownFoodException;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfoAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyFoodDiaryTest {

    @Mock
    private NutritionInfoAPI nutritionInfoAPI;

    @InjectMocks
    private DailyFoodDiary diary;

    private static final NutritionInfo NUTRITION_INFO = new NutritionInfo(10, 20, 70);
    private static final int SERVING_SIZE = 20;
    private static final String FOOD_NAME = "food name";

    private static final FoodEntry FOOD_ENTRY = new FoodEntry(FOOD_NAME, SERVING_SIZE, NUTRITION_INFO);


    @Test
    void testAddFoodWithInvalidMealShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> diary.addFood(null, FOOD_NAME, SERVING_SIZE), "addFood should throw exception when null meal is supplied.");
    }

    @Test
    void testAddFoodWithInvalidFoodNameShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> diary.addFood(Meal.LUNCH, null, SERVING_SIZE), "addFood should throw exception when null food name is supplied.");
        assertThrows(IllegalArgumentException.class, () -> diary.addFood(Meal.LUNCH, "", SERVING_SIZE), "addFood should throw exception when empty food name is supplied.");
        assertThrows(IllegalArgumentException.class, () -> diary.addFood(Meal.LUNCH, "  ", SERVING_SIZE), "addFood should throw exception when blank food name is supplied.");
    }

    @Test
    void testAddFoodWithInvalidServingSizeShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> diary.addFood(Meal.LUNCH, FOOD_NAME, -5), "addFood should throw exception when negative serving size is supplied.");
    }

    @Test
    void testAddFoodWithNoNutritionIndofAvailableShouldThrowUnknownFoodException() throws UnknownFoodException {
        when(nutritionInfoAPI.getNutritionInfo(FOOD_NAME)).thenThrow(UnknownFoodException.class);
        assertThrows(UnknownFoodException.class, () -> diary.addFood(Meal.LUNCH, FOOD_NAME, SERVING_SIZE), "addFood should throw exception when unknown food is supplied is supplied.");
    }

    @Test
    void testAddFoodShouldAddFood() throws UnknownFoodException {
        when(nutritionInfoAPI.getNutritionInfo(FOOD_NAME)).thenReturn(NUTRITION_INFO);

        var result = diary.addFood(Meal.LUNCH, FOOD_NAME, SERVING_SIZE);

        assertEquals(result.food(), FOOD_NAME, "Food name should be correct");
        assertEquals(result.nutritionInfo(), NUTRITION_INFO, "Info should be correct");
        assertEquals(result.servingSize(), SERVING_SIZE, "Serving size should be correct");

        verify(nutritionInfoAPI, times(1)).getNutritionInfo(FOOD_NAME);
    }

    @Test
    void testGetAllFoodEntriesShouldReturnUnmodifiableCollection() {
        var result = diary.getAllFoodEntries();

        assertThrows(UnsupportedOperationException.class, () -> result.add(FOOD_ENTRY), "Should return unmodifiable collection");
        assertEquals(result.size(), 0, "Should return empty collection.");
    }

    @Test
    void testGetAllFoodEntriesShouldReturnAll() throws UnknownFoodException {
        when(nutritionInfoAPI.getNutritionInfo(FOOD_NAME)).thenReturn(NUTRITION_INFO);
        var added = diary.addFood(Meal.LUNCH, FOOD_NAME, SERVING_SIZE);
        var result = diary.getAllFoodEntries();
        assertTrue(result.contains(added), "Result should contain added element");
    }

    @Test
    void testGetAllFoodEntriesByProteinContentShouldSortCorrectly() throws UnknownFoodException {
        when(nutritionInfoAPI.getNutritionInfo(FOOD_NAME)).thenReturn(NUTRITION_INFO);
        var added1 = diary.addFood(Meal.LUNCH, FOOD_NAME, SERVING_SIZE + 2);
        var added2 = diary.addFood(Meal.LUNCH, FOOD_NAME, SERVING_SIZE);

        var result = diary.getAllFoodEntriesByProteinContent();
        assertEquals(added2, result.get(0), "Incorrect ordering");
        assertEquals(added1, result.get(1), "Incorrect ordering");
    }

    @Test
    void testGetDailyCaloriesIntakePerMeal() {
        assertThrows(IllegalArgumentException.class, () -> diary.getDailyCaloriesIntakePerMeal(null), "Should throw exception when meal is null");
    }

    @Test
    void testGetDailyCaloriesIntakePerMealShouldCountCorrectly() throws UnknownFoodException {
        when(nutritionInfoAPI.getNutritionInfo(FOOD_NAME)).thenReturn(NUTRITION_INFO);
        var added = diary.addFood(Meal.LUNCH, FOOD_NAME, SERVING_SIZE);

        var result = diary.getDailyCaloriesIntakePerMeal(Meal.LUNCH);
        assertEquals(result, NUTRITION_INFO.calories() * SERVING_SIZE, "Should calculate correctly");
    }

    @Test
    void testGetDailyCaloriesIntakePerMealMultipleShouldCountCorrectly() throws UnknownFoodException {
        when(nutritionInfoAPI.getNutritionInfo(FOOD_NAME)).thenReturn(NUTRITION_INFO);
        var added = diary.addFood(Meal.LUNCH, FOOD_NAME, SERVING_SIZE);
        var added2 = diary.addFood(Meal.LUNCH, FOOD_NAME, SERVING_SIZE);

        var result = diary.getDailyCaloriesIntakePerMeal(Meal.LUNCH);
        assertEquals(NUTRITION_INFO.calories() * SERVING_SIZE * 2, result, "Should calculate correctly");
    }

    @Test
    void testGetDailyCaloriesIntakeShouldCalculateCorectly() throws UnknownFoodException {
        var result = diary.getDailyCaloriesIntake();

        assertEquals(result, 0, "Should calculate correctly");
    }

}