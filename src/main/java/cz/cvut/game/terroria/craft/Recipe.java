package cz.cvut.game.terroria.craft;

import cz.cvut.game.terroria.utils.BlockType;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a crafting recipe that combines specified ingredients into a resulting BlockType.
 * <p>
 * Each Recipe defines:
 * <ul>
 *   <li>a {@link BlockType} that will be produced when crafted,</li>
 *   <li>a read-only list of ingredient {@link BlockType}s required for crafting, and</li>
 *   <li>a {@link BufferedImage} icon used for UI representation of the recipe.</li>
 * </ul>
 */
public class Recipe {
    /** The resulting block type produced by this recipe. */
    private final BlockType result;
    /** The list of block types required as ingredients for this recipe. */
    private final List<BlockType> ingredients;
    /** The icon image representing this recipe in the UI. */
    private final BufferedImage icon;

    /**
     * Constructs a Recipe with the specified result, ingredients, and icon.
     *
     * @param result the BlockType that will be crafted
     * @param ingredients the list of BlockType ingredients required (order-insensitive)
     * @param icon the BufferedImage used to display this recipe in the interface
     */
    public Recipe(BlockType result, List<BlockType> ingredients, BufferedImage icon) {
        this.result = result;
        this.ingredients = List.copyOf(ingredients);
        this.icon = icon;
    }


    /**
     * Returns the BlockType produced by this recipe.
     *
     * @return the resulting BlockType
     */
    public BlockType getResult() {
        return result;
    }

    /**
     * Returns an unmodifiable list of ingredients required for crafting.
     *
     * @return read-only List of ingredient BlockTypes
     */
    public List<BlockType> getIngredients() {
        return ingredients;
    }
    /**
     * Returns the icon image representing this recipe.
     *
     * @return BufferedImage used for recipe display
     */
    public BufferedImage getIcon() {
        return icon;
    }
}
