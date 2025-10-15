package cz.cvut.game.terroria.entities;

import cz.cvut.game.terroria.Game;

import java.awt.geom.Rectangle2D;

import static cz.cvut.game.terroria.utils.Constants.Directions.*;
import static cz.cvut.game.terroria.utils.Constants.EnemyConstants.*;


/**
 * Abstract base class for all enemy entities in the game.
 * Handles movement, animation, physics, and interactions with the player.
 */
public abstract class Enemy extends Entity {
    /** Current animation frame index, current state of the enemy,type identifier for different enemy behavior and stats.*/
    protected int aniIndex, enemyState, enemyType;
    /** Tick counter for advancing animation frames, number of ticks between animation frame updates */
    protected int aniTick, aniSpeed = 25;
    /** Flag to perform initialization on the first update. */
    protected boolean firstUpdate = true;
    /** Flag indicating whether the enemy is in the air (falling). */
    protected boolean inAir;
    /** Current vertical speed due to gravity. */
    protected float fallSpeed;
    /** Gravity acceleration applied each tick. */
    protected float gravity = 0.04f * Game.SCALE;
    /** Horizontal walking speed. */
    protected float walkSpeed = 0.35f * Game.SCALE;
    /** Direction of walking: LEFT or RIGHT. */
    protected int walkDir = LEFT;
    /** Y-coordinate of the current tile (for positioning). */
    protected int tileY;
    /** Distance at which the enemy can perform an attack. */
    protected float attackDistance = Game.TILES_SIZE;
    /** Maximum health points for this enemy. */
    protected int maxHealth;
    /** Current health points. */
    protected int currentHealth;
    /** Indicates if the enemy is active (not removed). */
    protected boolean active = true;
    /** Flag to ensure an attack is only checked once per action. */
    protected boolean attackChecked;


    /**
     * Constructs an enemy at the given position with specified dimensions and type.
     * @param x horizontal start position
     * @param y vertical start position
     * @param width width of the hitbox
     * @param height height of the hitbox
     * @param enemyType identifier for enemy characteristics
     */
    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        initHitbox(x, y, width, height);
        maxHealth = GetMaxHealth(enemyType);
        currentHealth = maxHealth;

    }
    /**
     * Checks and initializes parameters on the first game update.
     * @param lvlData tile map data for collision checks
     */
    protected void firstUpdateCheck(int[][] lvlData) {
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
        firstUpdate = false;
    }

    /**
     * Updates vertical movement due to gravity and collisions.
     * @param lvlData tile map data for collision checks
     */
    protected void updateInAir(int[][] lvlData) {
        if (CanMoveHere(hitbox.x, hitbox.y + fallSpeed, hitbox.width, hitbox.height, lvlData)) {
            hitbox.y += fallSpeed;
            fallSpeed += gravity;
        } else {
            inAir = false;
            hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, fallSpeed);
            tileY = (int) (hitbox.y / Game.TILES_SIZE);
        }
    }
    /**
     * Moves the enemy horizontally; reverses direction on collision.
     * @param lvlData tile map data for collision checks
     */
    protected void move(int[][] lvlData) {
        float xSpeed = (walkDir == LEFT) ? -walkSpeed : walkSpeed;
        if (CanMoveHere(
                hitbox.x + xSpeed,
                hitbox.y,
                hitbox.width,
                hitbox.height,
                lvlData)) {
            hitbox.x += xSpeed;
            return;
        }
        changeWalkDir();
    }
    /**
     * Orients the enemy to face the player.
     * @param player the player entity
     */
    protected void turnTowardsPlayer(Player player) {
        if (player.hitbox.x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }
    /**
     * Determines if the enemy has a clear line of sight to the player on the same vertical level.
     * @param lvlData tile map data for collision and sight checks
     * @param player the player entity
     * @return true if the player is visible and in pursuit range
     */
    protected boolean canSeePlayer(int[][] lvlData, Player player) {
        int playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE);
        if (playerTileY == (int) hitbox.y || playerTileY  + 1 == (int) hitbox.y || playerTileY - 1 == (int) hitbox.y)
            if (isPlayerInRange(player)) {
                if (IsSightClear(lvlData, hitbox, player.hitbox, (int) hitbox.y))
                    return true;
            }

        return false;
    }
    /**
     * Checks if the player is within detection range (multiple of attack distance).
     * @param player the player entity
     * @return true if within detection range
     */
    protected boolean isPlayerInRange(Player player) {
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance * 5;
    }
    /**
     * Checks if the player is close enough to be attacked.
     * @param player the player entity
     * @return true if within attack distance
     */
    protected boolean isPlayerCloseForAttack(Player player) {
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance;
    }
    /**
     * Sets a new animation state, resetting ticks and frame index.
     * @param enemyState new state constant
     */
    protected void newState(int enemyState) {
        this.enemyState = enemyState;
        aniTick = 0;
        aniIndex = 0;
    }
    /**
     * Applies damage to the enemy, updating health and state accordingly.
     * @param amount amount of health to subtract
     */
    public void hurt(int amount) {
        currentHealth -= amount;
        if (currentHealth <= 0)
            newState(DEAD);
        else
            newState(HIT);
    }
    /**
     * Checks collision between the enemy's attack box and the player, applying damage once.
     * @param attackBox attack hitbox
     * @param player the player entity
     */
    protected void checkEnemyHit(Rectangle2D.Float attackBox,Player player) {
        if (attackBox.intersects(player.getHitbox())) {
            player.changeHealth(-GetEnemyDmg(enemyType));
        }
        attackChecked = true;
    }


    protected void checkPlayerHit(Rectangle2D.Float attackBox, Player player) {
        if (attackBox.intersects(player.hitbox))
//            player.changeHealth(-GetEnemyDmg(enemyType));
        attackChecked = true;

    }
    /**
     * Updates the animation frame based on aniSpeed and handles state transitions on sequence end.
     */
    protected void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(enemyType, enemyState)) {
                aniIndex = 0;

                switch (enemyState) {
                    case RUNNING -> enemyState = IDLE;
                    case HIT -> enemyState = IDLE;
                    case DEAD -> active = false;
                }
            }
        }
    }
    /**
     * Reverses the walking direction.
     */
    protected void changeWalkDir() {
        if (walkDir == LEFT)
            walkDir = RIGHT;
        else
            walkDir = LEFT;

    }

    public void resetEnemy() {
        hitbox.x = x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(IDLE);
        active = true;
        fallSpeed = 0;
    }

    public int getAniIndex() {
        return aniIndex;
    }

    public int getEnemyState() {
        return enemyState;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Determines if there is a continuous walkable path on the given row between two tile columns.
     * Useful for line-of-sight checks.
     *
     * @param xStart  starting tile column (inclusive)
     * @param xEnd    ending tile column (exclusive)
     * @param y       tile row to check
     * @param lvlData 2D level data array mapping tile indices to block IDs
     * @return true if all tiles between xStart and xEnd on row y are walkable and have floor support
     */
    protected static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
        for (int i = 0; i < xEnd - xStart; i++) {
            if (IsSolid(xStart + i, y, lvlData))
                return false;
            if (!IsSolid(xStart + i, y + 1, lvlData))
                return false;
        }
        return true;
    }

    /**
     * Checks if two entities (or points) have line-of-sight on the same row without obstacles.
     *
     * @param lvlData 2D level data array mapping tile indices to block IDs
     * @param firstHitbox collision rectangle of the first entity
     * @param secondHitbox collision rectangle of the second entity
     * @param yTile    the tile row at which to check visibility
     * @return true if no solid tiles block the horizontal path between the two hitboxes
     */
    protected static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) {
        int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
        int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

        if (firstXTile > secondXTile)
            return IsAllTilesWalkable(secondXTile, firstXTile, yTile, lvlData);
        else
            return IsAllTilesWalkable(firstXTile, secondXTile, yTile, lvlData);

    }
}
