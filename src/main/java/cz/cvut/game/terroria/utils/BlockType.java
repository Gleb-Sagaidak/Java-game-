package cz.cvut.game.terroria.utils;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Enumeration of all block and item types in the game, with associated atlas coordinates.
 * <p>
 * Each BlockType specifies a row and column index into one of several sprite atlases:
 * - Standard floor and terrain tiles (Floor_Tiles2.png)
 * - Ore textures (ore.png)
 * - Plank variants and special items (Textures-16.png)
 * <p>
 * Provides methods to retrieve the corresponding tile image, obtain the enum's ordinal ID,
 * and convert an integer ID back into a BlockType (with out-of-range values mapping to AIR).
 */
public enum BlockType {
    /** Empty space; has no tile. */
    AIR            (-1,-1),

    GRASS          ( 0,  1),
    DIRT           ( 1,  1),
    ORE            ( 0,  0),

    SWORD(1, 0),

    PANTS_ICON(0,0),
    SHIRT_ICON(0,0),
    SHOES_ICON(0,0),

    MUSHROOM_MEAT(18,11),

    GRASS_LEFT_AIR ( 0,  0),
    GRASS_RIGHT_AIR( 0,  2),
    GRASS_LR_AIR   ( 3,  6),

    PLANK_RED      (13,  4),   // 5th tile in 14th row
    PLANK_GOLD     (13,  6),   // 7th tile
    PLANK_RAINBOW  (13,  7);

    // Common tile and atlas settings
    private static final int TILE_SIZE   = 16;
    private static final BufferedImage[][] ATLAS     =
            LoadSave.GetTiles("Floor_Tiles2.png", TILE_SIZE);
    private static final BufferedImage[][] ORE_ATLAS =
            LoadSave.GetTiles("ore.png",          TILE_SIZE);
    private static final String PLANKS_ATLAS_FILE = LoadSave.TEXTURES_ATLAS;
    private static final int    PLANK_TILE_SIZE    = 16;
    private static final BufferedImage[][] PLANK_ATLAS =
            LoadSave.GetTiles(PLANKS_ATLAS_FILE, PLANK_TILE_SIZE);

    /** Row index in its atlas. */
    /** Column index in its atlas. */
    private final int row, col;


    /**
     * Constructs a BlockType with the specified atlas coordinates.
     *
     * @param row the row index within the appropriate sprite atlas
     * @param col the column index within the appropriate sprite atlas
     */
    BlockType(int row, int col) {
        this.row = row;
        this.col = col;
    }
    /**
     * Retrieves the tile image for this block type from the correct atlas.
     * <p>
     * Returns null for AIR. Uses ORE_ATLAS for ORE, special single-image
     * atlases for equipment icons, and PLANK_ATLAS for planks and mushroom meat.
     *
     * @return BufferedImage of the tile, or null if none
     */
    public BufferedImage getTile() {
        if (row < 0) return null;          // AIR
        if (this == ORE)                   // ore.png
            return ORE_ATLAS[row][col];
        if (this == SWORD) {
            return LoadSave.GetSpriteAtlas("SwordICON.png");
        }
        if (this == PANTS_ICON){
            return LoadSave.GetSpriteAtlas("PantsICON.png");
        }
        if (this == SHIRT_ICON){
            return LoadSave.GetSpriteAtlas("ShirtICON.png");
        }
        if (this == SHOES_ICON){
            return LoadSave.GetSpriteAtlas("ShoesICON.png");
        }

        // Plank variants and mushroom meat from plank atlas
        switch(this) {
            case PLANK_RED:
            case PLANK_GOLD:
            case PLANK_RAINBOW:
            case MUSHROOM_MEAT:
                return PLANK_ATLAS[row][col];
            default:
                return ATLAS[row][col];    // your usual floor tiles
        }
    }

    public int getId() {
        return this.ordinal();
    }

    /**
     * Converts an integer ID back to its BlockType. Returns AIR if out of range.
     *
     * @param id the numeric ID to map
     * @return the corresponding BlockType, or AIR if invalid
     */
    public static BlockType fromId(int id) {
        BlockType[] vals = values();
        if (id < 0 || id >= vals.length)
            return AIR;
        return vals[id];
    }
}
