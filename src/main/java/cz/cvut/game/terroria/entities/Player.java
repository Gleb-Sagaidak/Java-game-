package cz.cvut.game.terroria.entities;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.audio.AudioHandler;
import cz.cvut.game.terroria.craft.Inventory;
import cz.cvut.game.terroria.gamestates.Playing;
import cz.cvut.game.terroria.utils.BlockType;
import cz.cvut.game.terroria.utils.LoadSave;
import cz.cvut.game.terroria.world.WorldGenerator;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import static cz.cvut.game.terroria.utils.Constants.PlayerConstants.*;
/**
 * Represents the player character in the game world, handling movement, animation,
 * input state, combat interactions, and inventory management.
 */
public class Player extends Entity{
    /** 2D array of player sprite frames organized by action and frame index. */
    private BufferedImage[][] animations;

    /** Tick counter for advancing animation frames. */
    private int aniTick;
    /** Current frame index within the active animation. */
    private int aniIndex;
    /** Number of ticks between frame updates. */
    private int aniSpeed = 20;

    /** Current action state (e.g., IDLE, RUNNING, JUMPING, ATTACK_1). */
    private int playerAction = IDLE;
    /** Flag indicating whether the player is currently moving. */
    private boolean moving = false;
    /** Flag indicating whether the player is performing an attack. */
    private boolean attacking = false;

    /** Movement input flags for each direction. */
    private boolean right, up, down, left, jump;
    /** Movement speed multiplier. */
    private float playerSpeed = 1.0f * Game.SCALE;

    /** Reference to level collision data for movement checks. */
    private int[][] lvlData;

    /** Offsets for drawing the sprite relative to the hitbox. */
    private float xDrawOffest = 34 * Game.SCALE;
    private float yDrawOffest = 22 * Game.SCALE;

    /** Audio handler for playing sound effects. */
    private AudioHandler audioHandler;

    /** Sprite flip parameters for horizontal orientation. */
    private int flipX = 0;
    private int flipW = 1;

    /** Inventory instance for item management. */
    private final Inventory inventory = new Inventory();

    /** Vertical movement and gravity parameters. */
    private float airSpeed = 0f;
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -2.25f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;
    private boolean jumpPressed = false;
    private boolean attackFinished = false;

    /** Health bar UI components and dimensions. */
    private BufferedImage healtBarImg;
    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    private int statusBarX = (int) (10 * Game.SCALE);
    private int statusBarY = (int) (10 * Game.SCALE);

    /** Health bar fill dimensions relative to UI. */
    private int healtBarXStart = (int) (34 * Game.SCALE);
    private int healtBarYStart = (int) (14 * Game.SCALE);
    private int healtBarWidth = (int) (150 * Game.SCALE);
    private int healtBarHeight = (int) (4 * Game.SCALE);

    /** Player health values. */
    private int maxHealth = 100;
    private int currentHealth = maxHealth;
    private int healthWidth = healtBarWidth;

    /** Collision box for melee attacks. */
    private Rectangle2D.Float attackBox;
    /** Flag to ensure attack collisions are checked once per swing. */
    private boolean attackChecked;

    /** Reference to the playing state for world and UI context. */
    private Playing playing;

    /** Equipment toggles for rendering additional sprites. */
    private boolean shirtTex = false;
    private boolean pantsTex = false;
    private boolean swordTex = false;
    private boolean shoesTex = false;

    /** Sprite arrays for equipped items. */
    private BufferedImage[][] shirtAnimations;
    private BufferedImage[][] pantsAnimations;
    private BufferedImage[][] shoesAnimations;
    private BufferedImage[][] swordAnimations;

    /**
     * Constructs a player at the given location and initializes assets.
     *
     * @param x      initial X-coordinate in world units
     * @param y      initial Y-coordinate in world units
     * @param width  width of the collision hitbox
     * @param height height of the collision hitbox
     * @param playing reference to the playing state for context
     */
    public Player(float x, float y,int width,int height, Playing playing) {
        super(x, y,width,height);
        this.playing = playing;
        loadAnimations();
        loadPantsAnimations();
        loadShirtAnimations();
        loadShoesAnimations();
        loadSwordAnimations();
        initHitbox(x,y, (int) (13 * Game.SCALE), (int) (42 * Game.SCALE));
        initAttackBox();
        audioHandler = playing.getGame().getAudioHandler();
    }
    /** Initializes the melee attack collision box relative to the hitbox. */
    private void initAttackBox(){
        attackBox = new Rectangle2D.Float(x,y,(int) (26 * Game.SCALE),(int) (26 * Game.SCALE));
    }
    /**
     * Main update loop called each tick to process input, movement,
     * attacks, animations, and health state.
     */
    public void update() {
        updateHealthBar();
        if(currentHealth <= 0) {
            playing.setGameOver(true);
            return;
        }
        updateAttackBox();
        updatePos();
        if (attacking) {
            checkAttack();
        }
        updateAnimationTick();
        setAnimation();

    }
    /**
     * Checks for collision between attackBox and enemies at the correct frame.
     */
    private void checkAttack() {
        if(attackChecked || aniIndex != 1)
            return;
        attackChecked = true;
        playing.checkEnemyHit(attackBox);
    }
    /**
     * Positions the attackBox based on facing direction.
     */
    private void updateAttackBox() {
        if (right) {
            attackBox.x = hitbox.x + hitbox.width + (int) (7 * Game.SCALE);
        }else if(left) {
            attackBox.x = hitbox.x - hitbox.width - (int) (7 * Game.SCALE);
        }
        attackBox.y = hitbox.y + (int)(10 * Game.SCALE);
    }
    /**
     * Updates the width of the health bar fill based on current health.
     */
    private void updateHealthBar() {
        healthWidth = (int)((currentHealth / (float) maxHealth) * healtBarWidth);
    }
    /**
     * Renders the player sprite, equipment, and UI elements.
     *
     * @param g           graphics context
     * @param lvlOffset   horizontal camera offset
     * @param yLvlOffset  vertical camera offset
     */
    public void render(Graphics g, int lvlOffset, int yLvlOffset) {
        int drawX = (int) (hitbox.x - xDrawOffest) - lvlOffset;
        int drawY = (int) (hitbox.y - yDrawOffest) - yLvlOffset;
        g.drawImage(animations[playerAction][aniIndex],
                drawX + flipX,
                drawY,
                width * flipW, height,
                null);
        if (shirtTex){
            g.drawImage(shirtAnimations[playerAction][aniIndex], drawX + flipX,
                    drawY,
                    width * flipW, height, null);
        }
        if (pantsTex)
            g.drawImage(pantsAnimations[playerAction][aniIndex], drawX + flipX,
                    drawY,
                    width * flipW,height,null);
        if (shoesTex)
            g.drawImage(shoesAnimations[playerAction][aniIndex], drawX + flipX,
                    drawY,
                    width * flipW,height,null);
        if (swordTex)
            g.drawImage(swordAnimations[playerAction][aniIndex], drawX + flipX,
                    drawY,
                    width * flipW,height,null);
        drawUI(g);
//        drawAttackBox(g, lvlOffset, yLvlOffset);
    }

    private void drawAttackBox(Graphics g, int lvlOffsetX, int yLvlOffset) {
        g.setColor(Color.RED);
        g.drawRect((int) attackBox.x - lvlOffsetX, (int) attackBox.y - yLvlOffset, (int) attackBox.width, (int) attackBox.height);
    }
    /** Draws health bar UI frame and fill. */
    private void drawUI(Graphics g) {
        g.drawImage(healtBarImg, statusBarX,statusBarY,statusBarWidth,statusBarHeight,null);
        g.setColor(Color.RED);
        g.fillRect(healtBarXStart + statusBarX,healtBarYStart + statusBarY,healthWidth,healtBarHeight);
    }
    /**
     * Loads the base character sprites and health bar image.
     */
    private void loadAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.CHARACTER_ATLAS);
        animations = new BufferedImage[7][10];
        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                animations[j][i] = img.getSubimage(i * 80, j * 64, 80, 64);
            }
        }
        healtBarImg = LoadSave.GetSpriteAtlas(LoadSave.HEALTH_BAR);
    }
    private void loadShirtAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.SHIRT_SPRITE);
        shirtAnimations = new BufferedImage[7][10];
        for (int j = 0; j < shirtAnimations.length; j++) {
            for (int i = 0; i < shirtAnimations[j].length; i++) {
                shirtAnimations[j][i] = img.getSubimage(i * 80, j * 64, 80, 64);
            }
        }
    }
    private void loadPantsAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PANTS_SPRITE);
        pantsAnimations = new BufferedImage[7][10];
        for (int j = 0; j < pantsAnimations.length; j++) {
            for (int i = 0; i < pantsAnimations[j].length; i++) {
                pantsAnimations[j][i] = img.getSubimage(i * 80, j * 64, 80, 64);
            }
        }
    }
    private void loadShoesAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.SHOES_SPRITE);
        shoesAnimations = new BufferedImage[7][10];
        for (int j = 0; j < shoesAnimations.length; j++) {
            for (int i = 0; i < shoesAnimations[j].length; i++) {
                shoesAnimations[j][i] = img.getSubimage(i * 80, j * 64, 80, 64);
            }
        }
    }
    private void loadSwordAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.SWORD_SPRITE);
        swordAnimations = new BufferedImage[7][10];
        for (int j = 0; j < swordAnimations.length; j++) {
            for (int i = 0; i < swordAnimations[j].length; i++) {
                swordAnimations[j][i] = img.getSubimage(i * 80, j * 64, 80, 64);
            }
        }
    }

    /**
     * Sets level data for collision checks and initializes inAir state if needed.
     * @param lvlData tile map collision data
     */
    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;

    }

    /**
     * Advances animation frames based on aniSpeed and handles attack end logic.
     */
    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (attacking && playerAction == ATTACK_1
                    && aniIndex >= GetSpriteAmounts(playerAction)) {
                aniIndex = 0;
                attacking = false;
                attackFinished = true;
            }
            else if (aniIndex >= GetSpriteAmounts(playerAction)) {
                aniIndex = 0;
                attacking = false;
                attackChecked = false;
            }
        }
    }

    /**
     * Determines the appropriate animation state based on movement and actions.
     */
    private void setAnimation() {
        int startAni = playerAction;

        if(moving){
            playerAction = RUNNING;
        }else {
            playerAction = IDLE;
        }
        if(inAir){
            if (airSpeed < 0 )
                playerAction = JUMP;
            else
                playerAction = FALLING;
        }
        if(attacking){
            playerAction = ATTACK_1;
            if(startAni  != ATTACK_1) {
                aniIndex = 1;
                aniTick = 0;
                return;
            }
        }
        if (startAni != playerAction) {
            resetAniTick();
        }
    }

    /**
     * Resets animation tick and frame index for a new action.
     */
    private void resetAniTick() {
        aniIndex = 0;
        aniTick = 0;
    }
    /**
     * Updates player position, handles input, gravity, collisions, and movement.
     */
    private void updatePos() {
        moving = false;
        if (jump)
            jump();

        if (!inAir) {
            if ((!left && !right) || (right && left))
                return;
        }

        float xOffset = 0;
        if (left) {
            flipX = 0;
            flipW = 1;
            xOffset -= playerSpeed;

        }
        if (right) {
            flipX = width;
            flipW = -1;
            xOffset += playerSpeed;

        }

        if (!inAir) {
            if (!IsEntityOnFloor(hitbox, lvlData)) {
                inAir = true;
            }
        }
        if (inAir) {
            if (CanMoveHere(
                    hitbox.x,
                    hitbox.y + airSpeed,
                    hitbox.width,
                    hitbox.height,
                    lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += gravity;
            } else {
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                if (airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
            }
        }
        if (xOffset != 0) {
            float newX = hitbox.x + xOffset;
            float topCheck    = hitbox.y + COLLISION_OFFSET;
            float bottomCheck = hitbox.y + hitbox.height - COLLISION_OFFSET;
            float sideX = xOffset > 0
                    ? newX + hitbox.width - COLLISION_OFFSET
                    : newX + COLLISION_OFFSET;
            if (!IsSolid(sideX, topCheck, lvlData)
                    && !IsSolid(sideX, bottomCheck, lvlData)) {
                hitbox.x = newX;
                moving = true;
            }
        }
    }
    /**
     * Changes player health by a given value, clamped between 0 and maxHealth.
     * @param value positive or negative change to health
     */
    public void changeHealth(int value) {
        currentHealth += value;
        if(currentHealth <= 0) {
            currentHealth = 0;
            //gameOver();
        }else if (currentHealth >= maxHealth){
            currentHealth = maxHealth;
        }
    }
    /**
     * Initiates a jump if on the ground by applying upward airSpeed.
     */
    private void jump() {
        if (inAir)
            return;
        inAir = true;
        airSpeed = jumpSpeed;

    }
    /** Resets inAir state and vertical speed after landing. */
    private void resetInAir() {
        inAir = false;
        airSpeed = 0f;
    }

    private void updateXpos(float xSpeed) {
        if(CanMoveHere(xSpeed + hitbox.x, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        }else {
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
        }
    }

    /**
     * Resets movement input flags.
     */
    public void resetDirBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public boolean isAttackJustFinished() {
        return attackFinished;
    }

    public void clearAttackJustFinished() {
        attackFinished = false;
    }


    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Adds a specified number of planks of the given tree type to the inventory.
     * <p>
     * Determines the correct plank BlockType based on the treeType code:
     * <ul>
     *   <li>0: PLANK_GOLD</li>
     *   <li>1: PLANK_RAINBOW</li>
     *   <li>other: PLANK_RED</li>
     * </ul>
     * Retrieves the plank's texture tile and, if available, adds the items to the inventory.
     *
     * @param treeType an integer code representing the type of tree (0 = gold, 1 = rainbow, others = red)
     * @param count the number of planks to add
     */
    public void addPlank(int treeType, int count) {
        BlockType plankType = switch(treeType) {
            case 0 -> BlockType.PLANK_GOLD;
            case 1 -> BlockType.PLANK_RAINBOW;
            default -> BlockType.PLANK_RED;
        };
        BufferedImage tex = plankType.getTile();
        if (tex != null)
            inventory.addItem(plankType, count, tex);
    }
    /**
     * Mines the block at the specified world coordinates and collects it into the inventory.
     * <p>
     * Checks if the target block is not air, converts its ID to a BlockType, retrieves its texture,
     * destroys the block in the world generator, and adds one of the resulting item to the inventory.
     *
     * @param worldGen the WorldGenerator instance representing the game world
     * @param tx the x-coordinate of the block to mine
     * @param ty the y-coordinate of the block to mine
     */
    public void mineAndCollect(WorldGenerator worldGen, int tx, int ty) {
        int id = worldGen.getWorld()[tx][ty];
        if (id == BlockType.AIR.ordinal()) return;

        BlockType bt = BlockType.fromId(id);
        BufferedImage tex = bt.getTile();
        worldGen.destroyBlock(tx, ty);
        // ← use the (type, count, texture) overload
        inventory.addItem(bt, 1, tex);
    }

    public int getAttackAnimDuration() {
        return aniSpeed * GetSpriteAmounts(ATTACK_1);
    }

    public boolean isRight() {
        return right;
    }

    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isLeft() {
        return left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }
    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void resetAll() {
        resetDirBooleans();
        inAir = false;
        attacking = false;
        moving = false;
        playerAction = IDLE;
        currentHealth = maxHealth;

        hitbox.x = x;
        hitbox.y = y;

//        if (!IsEntityOnFloor(hitbox, lvlData))
//            inAir = true;
    }

    public void equipShirt() {
        this.shirtTex = true;

    }

    public void unequipShirt() {
        this.shirtTex = false;

    }
    public void equipSword() {
        this.swordTex = true;
    }
    public void unequipSword() {
        this.swordTex = false;
    }
    public void equipShoes(){
        this.shoesTex = true;
    }
    public void unequipShoes() {
        this.shoesTex = false;
    }


    public void equipPants() {
       this.pantsTex = true;

    }
    public void unequipPants() {
        this.pantsTex = false;
    }

    public boolean isShirtEquipped() {
        return this.shirtTex;
    }

    public boolean isPantsEquipped() {
        return this.pantsTex;
    }
    public boolean isShoesEquipped() {
        return this.shoesTex;
    }
    public boolean isSwordEquipped() {
        return this.swordTex;
    }
}
