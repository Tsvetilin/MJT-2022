package bg.sofia.uni.fmi.mjt.cocktail.server.storage;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.Ingredient;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StorageTest {

    private static final Set<Ingredient> INGREDIENTS_SET = new HashSet<Ingredient>(
        Arrays.asList(
            new Ingredient("igr1", "1"),
            new Ingredient("igr2", "3")
        )
    );
    private static final Cocktail COCKTAIL_FIRST = new Cocktail("test", INGREDIENTS_SET);
    CocktailStorage storage = new DefaultCocktailStorage();

    @Test
    void testGet() throws CocktailAlreadyExistsException, CocktailNotFoundException {
        storage.createCocktail(COCKTAIL_FIRST);
        assertEquals(COCKTAIL_FIRST.name(), storage.getCocktail(COCKTAIL_FIRST.name()).name());
    }

    @Test
    void testGetShouldFail() {
        assertThrows(CocktailNotFoundException.class, () -> storage.getCocktail("non-existing"));
    }

    @Test
    void testAdd() throws CocktailAlreadyExistsException, CocktailNotFoundException {
        storage.createCocktail(COCKTAIL_FIRST);
        assertEquals(COCKTAIL_FIRST.name(), storage.getCocktail(COCKTAIL_FIRST.name()).name());
    }

    @Test
    void testAddShouldFail() throws CocktailAlreadyExistsException, CocktailNotFoundException {
        storage.createCocktail(COCKTAIL_FIRST);
        assertEquals(COCKTAIL_FIRST.name(), storage.getCocktail(COCKTAIL_FIRST.name()).name());
        assertThrows(CocktailAlreadyExistsException.class, () -> storage.createCocktail(COCKTAIL_FIRST));
    }

    @Test
    void testGetAll() throws CocktailAlreadyExistsException {
        storage.createCocktail(COCKTAIL_FIRST);
        assertEquals(1, storage.getCocktails().size());
    }

    @Test
    void testGetAllEmpty() {
        assertEquals(0, storage.getCocktails().size());
    }

    @Test
    void testGetByIngredient() throws CocktailAlreadyExistsException {
        storage.createCocktail(COCKTAIL_FIRST);
        assertEquals(COCKTAIL_FIRST,storage.getCocktailsWithIngredient(INGREDIENTS_SET.stream().findFirst().get().name()).toArray()[0]);
    }
}
