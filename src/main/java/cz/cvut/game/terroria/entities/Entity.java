package cz.cvut.game.terroria.entities;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.utils.BlockType;

import java.awt.geom.Rectangle2D;
/**
 * Abstract representation of any object in the game world with a position, size, and collision boundary.
 */
public abstract class Entity {
    /** X-coordinate of the entity's logical position. */
    protected float x;

    /** Y-coordinate of the entity's logical position. */
    protected float y;

    /** Width of the entity's collision hitbox. */
    protected int width;

    /** Height of the entity's collision hitbox. */
    protected int height;

    /** Rectangle representing the entity's collision boundary. */
    protected Rectangle2D.Float hitbox;

    /** Pixel offset applied when checking collision corners to avoid clipping. */
    protected static final int COLLISION_OFFSET = 2;

    /**
     * Constructs an entity at the given position with specified dimensions.
     *
     * @param x      Initial horizontal position
     * @param y      Initial vertical position
     * @param width  Width of the entity's hitbox
     * @param height Height of the entity's hitbox
     */
    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    /**
     * Initializes the entity's hitbox at the given position and size.
     *
     * @param x      X-coordinate where hitbox should be placed
     * @param y      Y-coordinate where hitbox should be placed
     * @param width  Width of the hitbox
     * @param height Height of the hitbox
     */
    protected void initHitbox(float x, float y, int width, int height) {
        hitbox = new Rectangle2D.Float( x, y,width, height );
    }
    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    /**
     * Checks if a single point in world coordinates lies within a solid (non-air) tile.
     *
     * @param x       the X-coordinate of the point in pixels
     * @param y       the Y-coordinate of the point in pixels
     * @param lvlData 2D level data array mapping tile indices to block IDs
     * @return true if the point is outside bounds or on a non-air tile
     */
    protected static boolean IsSolid(
            float x, float y,
            int[][] lvlData) {
        int mapWidthPx  = lvlData.length * Game.TILES_SIZE;
        int mapHeightPx = lvlData[0].length * Game.TILES_SIZE;
        if (x < 0 || x >= mapWidthPx || y < 0 || y >= mapHeightPx) {
            return true;
        }
        int tileX = (int) (x / Game.TILES_SIZE);
        int tileY = (int) (y / Game.TILES_SIZE);
        int value = lvlData[tileX][tileY];
        return value != BlockType.AIR.ordinal();
    }


    /**
     * Determines if an axis-aligned rectangle at the specified position and size
     * is free of solid tiles in all four corners.
     *
     * @param x        the X-coordinate of the rectangle's top-left corner in pixels
     * @param y        the Y-coordinate of the rectangle's top-left corner in pixels
     * @param width    the width of the rectangle in pixels
     * @param height   the height of the rectangle in pixels
     * @param lvlData  2D level data array mapping tile indices to block IDs
     * @return true if none of the rectangle's corners overlap solid tiles
     */
    protected static boolean CanMoveHere(
            float x, float y, float width, float height,
            int[][] lvlData) {
        return !IsSolid(x + COLLISION_OFFSET, y + COLLISION_OFFSET, lvlData)
                && !IsSolid(x + width - COLLISION_OFFSET, y + COLLISION_OFFSET, lvlData)
                && !IsSolid(x + COLLISION_OFFSET, y + height - COLLISION_OFFSET, lvlData)
                && !IsSolid(x + width - COLLISION_OFFSET, y + height - COLLISION_OFFSET, lvlData);
    }

    /**
     * Calculates the X-position an entity should occupy next to a wall after horizontal movement.
     *
     * @param hitbox the entity's collision rectangle
     * @param xSpeed horizontal speed (positive moving right, negative moving left)
     * @return adjusted X-coordinate that places the entity flush against the wall
     */
    protected static float GetEntityXPosNextToWall(
            Rectangle2D.Float hitbox,
            float xSpeed) {
        if (xSpeed > 0) {
            int tileX = (int) ((hitbox.x + hitbox.width + xSpeed) / Game.TILES_SIZE);
            return tileX * Game.TILES_SIZE - hitbox.width - COLLISION_OFFSET;
        } else {
            int tileX = (int) ((hitbox.x + xSpeed) / Game.TILES_SIZE);
            return tileX * Game.TILES_SIZE + Game.TILES_SIZE + COLLISION_OFFSET;
        }
    }

    /**
     * Calculates the Y-position an entity should occupy under a roof or above a floor after vertical movement.
     *
     * @param hitbox  the entity's collision rectangle
     * @param airSpeed vertical speed (positive moving down, negative moving up)
     * @return adjusted Y-coordinate that places the entity flush against the ceiling or floor
     */
    protected static float GetEntityYPosUnderRoofOrAboveFloor(
            Rectangle2D.Float hitbox,
            float airSpeed) {
        if (airSpeed > 0) {
            int tileY = (int) ((hitbox.y + hitbox.height + airSpeed) / Game.TILES_SIZE);
            return tileY * Game.TILES_SIZE - hitbox.height;
        } else {
            int tileY = (int) ((hitbox.y + airSpeed) / Game.TILES_SIZE);
            return tileY * Game.TILES_SIZE + Game.TILES_SIZE;
        }
    }

    /**
     * Checks if the entity's bottom corners are resting on solid floor tiles.
     *
     * @param hitbox the entity's collision rectangle
     * @param lvlData 2D level data array mapping tile indices to block IDs
     * @return true if both bottom corners overlap solid tiles
     */
    protected static boolean IsEntityOnFloor(
            Rectangle2D.Float hitbox,
            int[][] lvlData) {
        return IsSolid(hitbox.x + COLLISION_OFFSET,
                hitbox.y + hitbox.height + COLLISION_OFFSET,
                lvlData)
                && IsSolid(hitbox.x + hitbox.width - COLLISION_OFFSET,
                hitbox.y + hitbox.height + COLLISION_OFFSET,
                lvlData);
    }

}
