package cz.cvut.game.terroria.utils;

import java.awt.image.BufferedImage;
/**
 * Represents a stack of items of a specific BlockType, including its quantity and texture.
 * <p>
 * Encapsulates the block type, the count of items in the stack, and the graphical texture
 * used for rendering in the inventory UI. Provides methods to add, remove, and query items.
 */
public class ItemStack {
    /** The type of block this stack represents. */
    private final BlockType type;

    /** The current number of items in this stack. */
    private int count;

    /** The texture image used to display this item in the UI. */
    private final BufferedImage texture;
    /**
     * Constructs an ItemStack with the given type, initial count, and texture.
     *
     * @param type    the BlockType of items in this stack
     * @param count   the starting quantity of items
     * @param texture the BufferedImage used to render this item
     */
    public ItemStack(BlockType type, int count, BufferedImage texture) {
        this.type  = type;
        this.count = count;
        this.texture = texture;
    }

    public BufferedImage getTexture() {
        return texture;
    }
    public BlockType getType()      { return type; }
    public int       getCount()     { return count; }

    /**
     * Increases the item count by the specified amount.
     *
     * @param amt the number of items to add
     */
    public void      add(int amt)   { count += amt; }

    /**
     * Decreases the item count by the specified amount, not going below zero.
     *
     * @param amt the number of items to remove
     */
    public void      remove(int amt){ count = Math.max(0, count - amt); }
    public boolean   isEmpty()      { return count <= 0; }
}
