package cz.cvut.game.terroria.craft;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.entities.Player;
import cz.cvut.game.terroria.utils.BlockType;
import cz.cvut.game.terroria.utils.Constants;
import cz.cvut.game.terroria.utils.ItemStack;
import java.util.List;

import java.awt.*;
import java.awt.image.BufferedImage;
/**
 * Represents a fixed-size inventory of item stacks with stacking, removal, and count utilities.
 * <p>
 * Holds up to SLOTS item stacks. New items will stack on existing slots of the same type
 * or occupy the first empty slot. Provides methods to add items, retrieve stacks, remove items,
 * count items by type, and reset the inventory.
 */
public class Inventory {
    /** Number of slots available in the inventory. */
    private static final int SLOTS = 16;
    /** Array of item stacks for each slot; a null entry indicates an empty slot. */
    private final ItemStack[] items;

    public static final int INV_COLS      = 4;
    public static final int INV_ROWS      = 4;
    public static final int INV_SLOT_SIZE = Game.TILES_SIZE * 2;
    public static final int INV_PADDING   = 8;
    private final int INV_WIDTH   = INV_COLS * INV_SLOT_SIZE + (INV_COLS - 1) * INV_PADDING;
    private final int INV_HEIGHT  = INV_ROWS * INV_SLOT_SIZE + (INV_ROWS - 1) * INV_PADDING;
    public final int INV_START_X = (Constants.WINDOW_WIDTH  - INV_WIDTH)  / 2;
    public final int INV_START_Y = (Constants.WINDOW_HEIGHT - INV_HEIGHT) / 2;
    public int selectedSlot = -1;


    public Inventory() {
        items = new ItemStack[SLOTS];
    }

    /**
     * Attempts to add the specified item type and count to the inventory.
     * <p>
     * First tries to stack onto an existing ItemStack of the same type;
     * if none exists, places a new ItemStack in the first empty slot.
     *
     * @param type the BlockType of the item to add
     * @param count the quantity of items to add
     * @param tex the texture image for the item
     * @return true if the items were added successfully, false if inventory is full
     */
    public boolean addItem(BlockType type, int count, BufferedImage tex) {
        // 1) Try stacking onto an existing slot of the same type
        for (int i = 0; i < SLOTS; i++) {
            ItemStack s = items[i];
            if (s != null && s.getType() == type) {
                s.add(count);
                return true;
            }
        }
        // 2) Place in first empty slot
        for (int i = 0; i < SLOTS; i++) {
            if (items[i] == null) {
                items[i] = new ItemStack(type, count, tex);
                return true;
            }
        }
        return false;  // inventory full
    }

    public ItemStack getItem(int index) {
        if (index < 0 || index >= SLOTS) return null;
        return items[index];
    }

    /**
     * Clears all slots in the inventory.
     */
    public void resetInv(){
        for (int i = 0; i < items.length; i++) {
            items[i] = null;
        }
    }

    /**
     * Removes a single item from the specified slot, and clears the slot if the stack becomes empty.
     *
     * @param index the slot index
     */
    public void removeOne(int index) {
        if (index < 0 || index >= SLOTS) return;
        ItemStack s = items[index];
        if (s == null) return;
        s.remove(1);
        if (s.isEmpty()) {
            items[index] = null;
        }
    }

    /**
     * Removes up to count items of the specified type, draining from stacks in slot order.
     *
     * @param type the BlockType to remove
     * @param count the total number of items to remove
     */
    public void removeItem(BlockType type, int count) {
        for (int i = 0; i < SLOTS && count > 0; i++) {
            ItemStack s = items[i];
            if (s != null && s.getType() == type) {
                int before = s.getCount();
                s.remove(count);
                int used = before - s.getCount();
                count -= used;
                if (s.isEmpty()) items[i] = null;
            }
        }
    }

    /**
     * Counts the total number of items of the specified type across all slots.
     *
     * @param type the BlockType to count
     * @return total count of that item type in the inventory
     */
    public int countOf(BlockType type) {
        int total = 0;
        for (ItemStack s : items) {
            if (s != null && s.getType() == type) {
                total += s.getCount();
            }
        }
        return total;
    }

    public boolean handleCraftPressed(int mx,int my, RecipeManager recipeManager){
        List<Recipe> recs = recipeManager.getRecipes();
        int panelX = INV_START_X - (INV_SLOT_SIZE + INV_PADDING) * (recs.get(0).getIngredients().size() + 1);
        for (int i = 0; i < recs.size(); i++) {
            int y = INV_START_Y + i * (INV_SLOT_SIZE + INV_PADDING);
            Rectangle r = new Rectangle(panelX, y, INV_SLOT_SIZE, INV_SLOT_SIZE);
            if (r.contains(mx, my)) {
                return recipeManager.craft(recs.get(i), this);
            }
        }
        return false;
    }

    public int getSize() { return SLOTS; }

    public void drawInventory(Graphics g, RecipeManager recipeManager) {
        g.setColor(new Color(0,0,0,150));
        g.fillRect(INV_START_X - INV_PADDING,
                INV_START_Y - INV_PADDING,
                INV_WIDTH   + INV_PADDING*2,
                INV_HEIGHT  + INV_PADDING*2);


        // Draw slots and items
        for (int row=0; row<INV_ROWS; row++) {
            for (int col=0; col<INV_COLS; col++) {
                int idx = col + row*INV_COLS;
                int x = INV_START_X + col*(INV_SLOT_SIZE+INV_PADDING);
                int y = INV_START_Y + row*(INV_SLOT_SIZE+INV_PADDING);

                // Slot background
                g.setColor(Color.DARK_GRAY);
                g.fillRect(x,y,INV_SLOT_SIZE,INV_SLOT_SIZE);
                g.setColor(Color.WHITE);
                g.drawRect(x,y,INV_SLOT_SIZE,INV_SLOT_SIZE);

                // Selection highlight
                if (idx == selectedSlot) {
                    g.setColor(Color.YELLOW);
                    g.drawRect(x-2,y-2,INV_SLOT_SIZE+4,INV_SLOT_SIZE+4);
                }

                // Item icon and count
                var stack = this.getItem(idx);
                if (stack!=null && !stack.isEmpty()) {
                    BufferedImage tex = stack.getTexture();
                    g.drawImage(tex, x+4, y+4, INV_SLOT_SIZE-8, INV_SLOT_SIZE-8, null);
                    g.setColor(Color.WHITE);
                    g.drawString(""+stack.getCount(),
                            x+INV_SLOT_SIZE-10, y+INV_SLOT_SIZE-6);
                }
            }
        }

        // Draw crafting panel
        int panelX = INV_START_X - (INV_SLOT_SIZE+INV_PADDING)*(recipeManager.getRecipes().get(0).getIngredients().size()+1);
        int panelY = INV_START_Y;
        for (int i=0; i<recipeManager.getRecipes().size(); i++) {
            Recipe r = recipeManager.getRecipes().get(i);
            int y = panelY + i*(INV_SLOT_SIZE+INV_PADDING);

            // Recipe icon background color by craftability
            g.setColor(recipeManager.canCraft(r, this) ? Color.GREEN : Color.RED);
            g.drawRect(panelX, y, INV_SLOT_SIZE, INV_SLOT_SIZE);
            g.drawImage(r.getIcon(),
                    panelX+4, y+4,
                    INV_SLOT_SIZE-8, INV_SLOT_SIZE-8,
                    null
            );

            // Ingredient slots
            for (int k=0; k<r.getIngredients().size(); k++) {
                BufferedImage icon = r.getIngredients().get(k).getTile();
                int ix = panelX + (INV_SLOT_SIZE+INV_PADDING)*(k+1);
                g.setColor(Color.DARK_GRAY);
                g.fillRect(ix,y,INV_SLOT_SIZE,INV_SLOT_SIZE);
                g.setColor(Color.WHITE);
                g.drawRect(ix,y,INV_SLOT_SIZE,INV_SLOT_SIZE);
                g.drawImage(icon,
                        ix+4, y+4,
                        INV_SLOT_SIZE-8, INV_SLOT_SIZE-8,
                        null
                );
            }
        }
    }

    public void handleSlotClick(int idx, Player player) {
        ItemStack stack = player.getInventory().getItem(idx);
        if (stack != null) {
            BlockType type = stack.getType();
            // Штаны
            if (type == BlockType.SHIRT_ICON) {
                if (player.isShirtEquipped()) {
                    player.unequipShirt();
                    player.getInventory().addItem(type, 1, stack.getTexture());
                } else {
                    player.equipShirt();
                }
                return;
            }
            // Рубашка
            if (type == BlockType.PANTS_ICON) {
                if (player.isPantsEquipped()) {
                    player.unequipPants();
                    player.getInventory().addItem(type, 1, stack.getTexture());
                } else {
                    player.equipPants();
                }
                return;
            }
            // Обувь
            if (type == BlockType.SHOES_ICON) {
                if (player.isShoesEquipped()) {
                    player.unequipShoes();
                    player.getInventory().addItem(type, 1, stack.getTexture());
                } else {
                    player.equipShoes();
                }
                return;
            }
            // Меч
            if (type == BlockType.SWORD) {
                if (player.isSwordEquipped()) {
                    player.unequipSword();
                    player.getInventory().addItem(type, 1, stack.getTexture());
                } else {
                    player.equipSword();
                }
                return;
            }
        }
        selectedSlot = (selectedSlot == idx) ? -1 : idx;
    }
}
