package bg.sofia.uni.fmi.mjt.smartfridge;

import bg.sofia.uni.fmi.mjt.smartfridge.comparators.OrderByExpirationDateStorableComparator;
import bg.sofia.uni.fmi.mjt.smartfridge.exception.FridgeCapacityExceededException;
import bg.sofia.uni.fmi.mjt.smartfridge.exception.InsufficientQuantityException;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.DefaultIngredient;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.Ingredient;
import bg.sofia.uni.fmi.mjt.smartfridge.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;

import java.util.*;

public class SmartFridge implements SmartFridgeAPI {

    private int totalCapacity;
    private int currentCapacity;
    private Map<String, Queue<Storable>> stored;

    SmartFridge(int totalCapacity) {
        this.totalCapacity = totalCapacity;
        stored = new HashMap<>();
    }


    @Override
    public <E extends Storable> void store(E item, int quantity) throws FridgeCapacityExceededException {

        if (item == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid items or count.");
        }

        if (currentCapacity + quantity > totalCapacity) {
            throw new FridgeCapacityExceededException("Capacity exceeded.");
        }

        stored.putIfAbsent(item.getName(), new PriorityQueue<>(new OrderByExpirationDateStorableComparator()));

        for (int i = 0; i < quantity; i++) {
            stored.get(item.getName()).add(item);
        }

        currentCapacity += quantity;
    }

    @Override
    public List<? extends Storable> retrieve(String itemName) {

        if (itemName == null || itemName.isBlank() || itemName.isEmpty()) {
            throw new IllegalArgumentException("Invalid item name.");
        }

        if (!stored.containsKey(itemName)) {
            return new ArrayList<>();
        }

        List<Storable> result = new ArrayList<>();
        result.addAll(stored.get(itemName));

        stored.remove(itemName);
        currentCapacity -= result.size();

        return result;
    }

    @Override
    public List<? extends Storable> retrieve(String itemName, int quantity) throws InsufficientQuantityException {
        if (itemName == null || itemName.isEmpty() || itemName.isBlank() || quantity <= 0) {
            throw new IllegalArgumentException("Invalid item or quantity.");
        }

        if (!stored.containsKey(itemName)) {
            throw new InsufficientQuantityException("No item stored");
        }

        if (stored.get(itemName).size() < quantity) {
            throw new InsufficientQuantityException("Not enough stored items");
        }

        List<Storable> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            result.add(stored.get(itemName).poll());
        }

        currentCapacity -= result.size();

        return result;
    }

    @Override
    public int getQuantityOfItem(String itemName) {
        if (itemName == null || itemName.isBlank() || itemName.isEmpty()) {
            throw new IllegalArgumentException("Invalid item name.");
        }

        if (!stored.containsKey(itemName)) {
            return 0;
        }

        return stored.get(itemName).size();
    }

    private <E extends Storable> int sufficientStocked(E item, int maxNeeded) {

        if (!stored.containsKey(item.getName())) {
            return 0;
        }

        int result = 0;
        Queue<Storable> temp = new LinkedList<>();
        Queue<Storable> storedList = stored.get(item.getName());

        while (!storedList.isEmpty()) {
            Storable current = storedList.poll();
            temp.add(current);
            if (!current.isExpired()) {
                ++result;
            }
        }

        while (!temp.isEmpty()) {
            storedList.add(temp.poll());
        }

        return Math.min(result, maxNeeded);
    }


    @Override
    public Iterator<Ingredient<? extends Storable>> getMissingIngredientsFromRecipe(Recipe recipe) {

        if (recipe == null) {
            throw new IllegalArgumentException("Invalid recipe.");
        }

        List<Ingredient<? extends Storable>> insufficient = new ArrayList<>();

        for (var ingr : recipe.getIngredients()) {
            int need = ingr.quantity() - sufficientStocked(ingr.item(), ingr.quantity());
            if (need > 0) {
                insufficient.add(new DefaultIngredient<>(ingr.item(), need));
            }
        }

        return insufficient.iterator();
    }

    @Override
    public List<? extends Storable> removeExpired() {
        List<Storable> expired = new ArrayList<>();

        var iter = stored.entrySet().iterator();

        while (iter.hasNext()) {
            var entry = iter.next();
            while (entry.getValue().peek().isExpired()) {
                expired.add(entry.getValue().poll());

                if (entry.getValue().isEmpty()) {
                    iter.remove();
                    break;
                }
            }
        }

        currentCapacity -= expired.size();
        return expired;
    }
}
