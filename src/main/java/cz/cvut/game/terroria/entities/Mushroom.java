package cz.cvut.game.terroria.entities;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.utils.BlockType;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static cz.cvut.game.terroria.utils.Constants.Directions.LEFT;
import static cz.cvut.game.terroria.utils.Constants.Directions.RIGHT;
import static cz.cvut.game.terroria.utils.Constants.EnemyConstants.*;

/**
 * Represents a mushroom-type enemy with patrolling and jump attack behavior.
 * Extends the base Enemy class to implement specific AI logic for movement,
 * attack detection, and item drops on death.
 */
public class Mushroom extends Enemy{
    /** Initial horizontal spawn position for patrol reference. */
    private final float startX;
    /** Maximum horizontal distance from startX that the mushroom will patrol. */
    private final float patrolRange = 50 * Game.TILES_SIZE;
    /** Upward force applied when the mushroom jumps. */
    private final float jumpPower = 1.25f * Game.SCALE;
    /** Hitbox for the mushroom's melee attack range. */
    private Rectangle2D.Float attackBox;

    /** X-axis offset from the entity position to place the attackBox. */
    private int attackBoxXOffset;
    /** Y-axis offset from the entity position to place the attackBox. */
    private int attackBoxYOffset;

    /**
     * Constructs a mushroom enemy at the specified coordinates.
     * Initializes hitbox, attack box, and tracks starting position.
     *
     * @param x spawn X-coordinate in world units
     * @param y spawn Y-coordinate in world units
     */
    public Mushroom(float x, float y) {
        super(x, y, MUSHROOM_WIDTH, MUSHROOM_HEIGHT, MUSHROOM);
        initHitbox(x, y, (int) (14 * Game.SCALE), (int) (32 * Game.SCALE));
        this.startX = x;
        initAttackBox();
    }
    /**
     * Initializes the rectangle used for melee attack collision checks.
     */
    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int) (58 * Game.SCALE), (int) (22 * Game.SCALE));
        attackBoxXOffset = (int) (15 * Game.SCALE);
        attackBoxYOffset = (int) (10 * Game.SCALE);
    }

    /**
     * Main update loop called each tick. Handles AI behavior, animation,
     * attack box movement, and item drop logic on death.
     *
     * @param lvlData tile map data for collision and sight checks
     * @param player  reference to the player for interactions
     */
    public void update(int[][] lvlData, Player player) {
        boolean wasAlive = active;
        updateBehave(lvlData, player);
        updateAnimationTick();
        updateAttackBox();
        
        if (wasAlive && !active) {
            System.out.println("[DROP] Mushroom died — dropping MUSHROOM_MEAT");
            BufferedImage meatTex = BlockType.MUSHROOM_MEAT.getTile();
            boolean added = player.getInventory().addItem(
                    BlockType.MUSHROOM_MEAT,
                    1,
                    meatTex
            );
            System.out.println("[DROP] addItem returned " + added);
        }
    }
    /**
     * Determines AI state transitions, movement, jumping, and attack triggers.
     *
     * @param lvlData tile map data for collision and sight checks
     * @param player  reference to the player for targeting
     */
    private void updateBehave(int[][] lvlData, Player player) {
        int widthTiles = lvlData.length;
        float worldWidth = widthTiles * Game.TILES_SIZE;

        if (!inAir && !IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (!inAir) {
            if (hitbox.x <= 0) walkDir = RIGHT;
            else if (hitbox.x + hitbox.width >= worldWidth) walkDir = LEFT;

            if (hitbox.x <= startX - patrolRange) walkDir = RIGHT;
            else if (hitbox.x >= startX + patrolRange) walkDir = LEFT;
        }

        if (inAir) {
            updateInAir(lvlData);
            moveOrJump(lvlData);
        } else {
            switch (enemyState) {
                case IDLE:
                    newState(RUNNING);
                    break;
                case RUNNING:
                    if (canSeePlayer(lvlData, player)) {
                        turnTowardsPlayer(player);
                    }
                    if (isPlayerCloseForAttack(player))
                        newState(ATTACK);
                    moveOrJump(lvlData);
                    break;
                case ATTACK:
                    if (aniIndex == 0)
                        attackChecked = false;
                    if ((aniIndex == 2 || aniIndex == 5) && !attackChecked)
                        checkEnemyHit(attackBox, player);
                    if(!isPlayerInRange(player) && !canSeePlayer(lvlData, player))
                        enemyState = IDLE;
                    break;
                case HIT:
                case DEAD:
                    break;
            }
        }
    }

    /**
     * Attempts horizontal movement or jumps if blocked.
     *
     * @param lvlData tile map data for collision checks
     */
    private void moveOrJump(int[][] lvlData) {
        float xSpeed = (walkDir == LEFT) ? -walkSpeed : walkSpeed;
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        } else {
            jump();
        }
    }
    /**
     * Initiates a vertical jump by setting fallSpeed upwards. Only if not already in air.
     */
    private void jump() {
        if (!inAir) {
            inAir = true;
            fallSpeed = -jumpPower;
        }
    }
    /**
     * Updates the position of the attackBox relative to the hitbox.
     */
    public void updateAttackBox() {
        attackBox.x = hitbox.x - attackBoxXOffset;
        attackBox.y = hitbox.y - attackBoxYOffset + 10;
    }

    /**
     * X-offset for sprite flipping when drawing.
     * @return horizontal flip offset (width if flipped right, 0 otherwise)
     */
    public int flipX() { return (walkDir == RIGHT) ? width : 0; }
    /**
     * Width multiplier for sprite flipping when drawing.
     * @return -1 if flipped right, 1 otherwise
     */
    public int flipW() { return (walkDir == RIGHT) ? -1 : 1; }

    public void drawAttackBox(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setColor(Color.RED);
        g.drawRect((int) attackBox.x - xLvlOffset ,(int) attackBox.y - yLvlOffset,(int) attackBox.width,(int) attackBox.height);
    }

    public void drawHitbox(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setColor(Color.GREEN);
        g.drawRect((int)hitbox.x - xLvlOffset,(int)hitbox.y - yLvlOffset,(int) hitbox.width,(int) hitbox.height);
    }

    public int getWidth() {
        return width;
    }

}
