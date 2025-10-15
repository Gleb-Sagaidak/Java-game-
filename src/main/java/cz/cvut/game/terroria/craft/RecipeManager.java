package cz.cvut.game.terroria.craft;

import cz.cvut.game.terroria.utils.BlockType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages a collection of crafting recipes and facilitates crafting operations.
 * <p>
 * Allows adding recipes, querying available recipes, checking if a recipe can be crafted
 * against a given inventory, and performing the craft by consuming ingredients and
 * producing the result.
 */
public class RecipeManager {
    /** List of all registered recipes. */
    private final List<Recipe> recipes = new ArrayList<>();

    /**
     * Adds a new recipe to the manager.
     *
     * @param r the Recipe to register
     */
    public void addRecipe(Recipe r) {
        recipes.add(r);
    }

    /**
     * Retrieves all registered recipes.
     *
     * @return a List of Recipe objects
     */
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * Determines whether the specified recipe can be crafted with the items
     * currently present in the inventory.
     * <p>
     * Counts required ingredients and compares against inventory counts.
     *
     * @param r the Recipe to check
     * @param inv the Inventory to use for ingredient lookup
     * @return true if the inventory contains at least the required quantity for each ingredient
     */
    public boolean canCraft(Recipe r, Inventory inv) {
        Map<BlockType,Integer> needed = new HashMap<>();
        for (BlockType t : r.getIngredients()) {
            needed.merge(t, 1, Integer::sum);
        }
        for (Map.Entry<BlockType,Integer> e : needed.entrySet()) {
            if (inv.countOf(e.getKey()) < e.getValue())
                return false;
        }
        return true;
    }

    /**
     * Attempts to craft the given recipe using the inventory.
     * <p>
     * If all required ingredients are present, removes them from the inventory
     * and adds the resulting item.
     *
     * @param r the Recipe to craft
     * @param inv the Inventory from which to consume ingredients and to which the result is added
     * @return true if crafting was successful; false if insufficient ingredients
     */
    public boolean craft(Recipe r, Inventory inv) {
        if (!canCraft(r, inv)) return false;
        Map<BlockType,Integer> needed = new HashMap<>();
        for (BlockType t : r.getIngredients()) {
            needed.merge(t, 1, Integer::sum);
        }
        for (Map.Entry<BlockType,Integer> e : needed.entrySet()) {
            inv.removeItem(e.getKey(), e.getValue());
        }
        return inv.addItem(r.getResult(), 1, r.getIcon());
    }
}
