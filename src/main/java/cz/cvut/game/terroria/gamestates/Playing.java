package cz.cvut.game.terroria.gamestates;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.audio.AudioHandler;
import cz.cvut.game.terroria.craft.Inventory;
import cz.cvut.game.terroria.craft.Recipe;
import cz.cvut.game.terroria.craft.RecipeManager;
import cz.cvut.game.terroria.entities.EnemyManager;
import cz.cvut.game.terroria.entities.Player;
import cz.cvut.game.terroria.ui.GameOverScreen;
import cz.cvut.game.terroria.ui.PauseScreen;
import cz.cvut.game.terroria.utils.BlockType;
import cz.cvut.game.terroria.utils.Constants;
import cz.cvut.game.terroria.utils.ItemStack;
import cz.cvut.game.terroria.utils.LoadSave;
import cz.cvut.game.terroria.world.WorldGenerator;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import static cz.cvut.game.terroria.craft.Inventory.*;


/**
 * The primary gameplay state where world, player, enemies, UI, and input are managed.
 * <p>
 * Extends {@link State} and implements {@link StateMethods}. Handles terrain rendering,
 * player and enemy updates, mining mechanics, inventory and recipe UI, pause and game-over
 * screens, and world interaction logic.
 */
public class Playing extends State implements StateMethods {

    // --- World & Rendering ---
    private final WorldGenerator worldGen;
    private int[][] world;
    private final BufferedImage playingBackground;
    private final BufferedImage caveBackground;
    private int     xLvlOffset = 0, yLvlOffset = 0;
    private static final int DIG_RANGE = 4;
    private List<WorldGenerator.Tree> trees;
    private final BufferedImage[] treeTextures;

    // --- Player & Enemies ---
    private final Player player;
    private final EnemyManager enemyManager;
    private boolean gameOver = false;
    private final GameOverScreen gameOverScreen;

    // --- Pause & Mining ---
    private boolean paused   = false;
    private PauseScreen pauseScreen;
    private boolean mining   = false;
    private int     mineTileX, mineTileY;
    private int     mineTimer  = 0;

    // --- Inventory & Crafting ---
    private boolean invActive = false;
    private final RecipeManager recipeManager;
    private Inventory inventory;

    // --- Camera ---
    private final int leftBorder   = Constants.WINDOW_WIDTH  / 2;
    private final int rightBorder  = Constants.WINDOW_WIDTH  / 2;
    private final int topBorder    = Constants.WINDOW_HEIGHT / 2;
    private final int bottomBorder = Constants.WINDOW_HEIGHT / 2;
    private final int maxLvlOffsetX = WorldGenerator.WIDTH_TILES  * Game.TILES_SIZE - Constants.WINDOW_WIDTH;
    private final int maxLvlOffsetY = WorldGenerator.HEIGHT_TILES * Game.TILES_SIZE - Constants.WINDOW_HEIGHT;




    /**
     * Constructs the Playing state, generating the world, loading assets,
     * initializing player, enemies, UI screens, and crafting recipes.
     *
     * @param game the main Game instance
     */
    public Playing(Game game) {
        super(game);

        worldGen    = new WorldGenerator(System.currentTimeMillis());
        world       = worldGen.getWorld();
        trees       = worldGen.getTrees();
        enemyManager= new EnemyManager(this);


        player = new Player(200, 200,
                (int)(Game.SCALE * 80),
                (int)(Game.SCALE * 64),
                this);
        player.loadLvlData(world);
        inventory = player.getInventory();

        playingBackground = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND);
        caveBackground = LoadSave.GetSpriteAtlas(LoadSave.CAVE_BACKGROUND);
        gameOverScreen    = new GameOverScreen(this);
        pauseScreen = new PauseScreen(this);


        treeTextures = LoadSave.loadImages(new String[]{
                "GoldTree.png", "rainbowTree.png", "redTree.png"
        });


        recipeManager = new RecipeManager();
        BufferedImage swordIcon  = LoadSave.GetSpriteAtlas("SwordICON.png");
        recipeManager.addRecipe(new Recipe(
                BlockType.SWORD,
                List.of(BlockType.ORE, BlockType.ORE, BlockType.PLANK_RAINBOW),
                swordIcon
        ));
        recipeManager.addRecipe(new Recipe(BlockType.SHIRT_ICON,List.of(BlockType.MUSHROOM_MEAT,
                BlockType.MUSHROOM_MEAT,
                BlockType.PLANK_GOLD
        ), BlockType.SHIRT_ICON.getTile()));
        recipeManager.addRecipe(new Recipe(BlockType.SHOES_ICON, List.of(BlockType.MUSHROOM_MEAT,
                BlockType.PLANK_RED,
                BlockType.PLANK_RED
        ), BlockType.SHOES_ICON.getTile()));
        recipeManager.addRecipe(new Recipe(BlockType.PANTS_ICON,List.of(BlockType.MUSHROOM_MEAT,
                BlockType.MUSHROOM_MEAT,
                BlockType.MUSHROOM_MEAT
        ), BlockType.PANTS_ICON.getTile()));

    }

    /**
     * Main update loop: handles pause, game over, player, mining, enemies, and camera.
     */
    @Override
    public void update() {
        if (paused)
            pauseScreen.update();
        else {
            if(gameOver) return;
            player.update();
            if (mining) handleMining();
            enemyManager.update(world, player);
            checkCloseToBorder();
        }
    }

    /**
     * Handles mining/digging interactions based on timer and range.
     */
    private void handleMining() {
        mineTimer--;
        if (mineTimer <= 0) {
            int px = ((int)(player.getHitbox().x + player.getHitbox().width/2)) / Game.TILES_SIZE;
            int py = ((int)(player.getHitbox().y + player.getHitbox().height)) / Game.TILES_SIZE;
            int dx = mineTileX - px, dy = mineTileY - py;
            if (dx*dx + dy*dy <= DIG_RANGE*DIG_RANGE) {
                var hitTree = trees.stream()
                        .filter(t -> t.tileX==mineTileX && t.tileY==mineTileY)
                        .findFirst();
                if (hitTree.isPresent()) {
                    trees.remove(hitTree.get());
                    player.addPlank(hitTree.get().type, 1);
                } else {
                    player.mineAndCollect(worldGen, mineTileX, mineTileY);
                }
                player.clearAttackJustFinished();
                player.loadLvlData(world);
            }
            if (mining) {
                if (dx*dx + dy*dy <= DIG_RANGE*DIG_RANGE) {
                    player.setAttacking(true);
                    mineTimer = player.getAttackAnimDuration();
                } else {
                    player.setAttacking(false);
                    mining = false;
                }
            }
        }
    }

    /**
     * Renders world layers, trees, player, enemies, and overlays (pause, inventory, game over).
     *
     * @param g the Graphics context for drawing
     */
    @Override
    public void draw(Graphics g) {
        int sw = Constants.WINDOW_WIDTH;
        int sh = Constants.WINDOW_HEIGHT;

        // compute where caves start (in screen‐pixels)
        int caveRow  = WorldGenerator.HEIGHT_TILES / 2;
        int caveYpx  = caveRow * Game.TILES_SIZE;
        int splitY   = caveYpx - yLvlOffset;
        splitY       = Math.max(0, Math.min(sh, splitY));

        g.drawImage(
                caveBackground,
                0, 0, sw, sh,
                0, 0,
                caveBackground.getWidth(),
                caveBackground.getHeight(),
                null
        );


        g.drawImage(
                playingBackground,
                0, 0, sw, splitY,
                0, 0,
                playingBackground.getWidth(),
                (int)(playingBackground.getHeight() * (splitY / (float)sh)),
                null
        );

        for (var t : trees) {
            BufferedImage tex = treeTextures[t.type];
            int px = t.tileX * Game.TILES_SIZE - xLvlOffset;
            int py = t.tileY * Game.TILES_SIZE - yLvlOffset;
            int w = tex.getWidth(), h = tex.getHeight();
            g.drawImage(tex,
                    px - (w - Game.TILES_SIZE)/2,
                    py - (h - Game.TILES_SIZE),
                    w, h, null
            );
        }

        worldGen.draw(g, xLvlOffset, yLvlOffset);
        player.render(g, xLvlOffset, yLvlOffset);
        enemyManager.draw(g, xLvlOffset, yLvlOffset);

        if      (paused)   drawPauseOverlay(g);
        else if (invActive) inventory.drawInventory(g, recipeManager);
        else if (gameOver)  gameOverScreen.draw(g);
    }

    /**
     * Draws a semi-transparent overlay and the pause menu when the game is paused.
     *
     * @param g the Graphics context used for rendering the overlay and menu
     */
    private void drawPauseOverlay(Graphics g) {
        g.setColor(new Color(0,0,0,100));
        g.fillRect(0,0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        pauseScreen.draw(g);
    }

    /**
     * Renders the inventory UI, including slots, items, selection highlight, and crafting panel.
     *
     * @param g the Graphics context used for rendering the inventory
     */


    /**
     * Handles mouse press events for pause menu, inventory actions, block placement,
     * mining, and attack initiation.
     *
     * @param e the MouseEvent triggered by pressing a mouse button
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (paused)
            pauseScreen.mousePressed(e);
        else {
            int my = e.getY();
            int btn = e.getButton();
            int mx = e.getX();

            if (invActive && SwingUtilities.isLeftMouseButton(e)){
                if(inventory.handleCraftPressed(mx,my,recipeManager))
                    return;
            }

            // Block placement
            if (!invActive && btn == MouseEvent.BUTTON3 && inventory.selectedSlot != -1) {
                ItemStack stack = player.getInventory().getItem(inventory.selectedSlot);
                if (stack != null) {
                    BlockType type = stack.getType();
                    int tx = (mx + xLvlOffset) / Game.TILES_SIZE;
                    int ty = (my + yLvlOffset) / Game.TILES_SIZE;
                    if (tx >= 0 && tx < world.length
                            && ty >= 0 && ty < world[0].length
                            && world[tx][ty] == BlockType.AIR.ordinal()) {
                        worldGen.setBlock(tx, ty, type);
                        player.loadLvlData(world);
                        player.getInventory().removeOne(inventory.selectedSlot);
                        if (player.getInventory().getItem(inventory.selectedSlot) == null) {
                            inventory.selectedSlot = -1;
                        }
                    }
                }
                return;
            }

            // Mining/attack with left click
            if (!gameOver && !invActive && SwingUtilities.isLeftMouseButton(e)) {

                player.setAttacking(true);
                if(player.isSwordEquipped()) {
                    getGame().getAudioHandler().playSwordAttackSound();
                }else
                    getGame().getAudioHandler().playAttackSound();
                mineTileX = (mx + xLvlOffset) / Game.TILES_SIZE;
                mineTileY = (my + yLvlOffset) / Game.TILES_SIZE;
                mining = true;
                mineTimer = player.getAttackAnimDuration();
            }

            // Inventory slot selection and equipment toggles
            if (invActive && SwingUtilities.isLeftMouseButton(e)) {
                int col = (mx - inventory.INV_START_X) / (INV_SLOT_SIZE + INV_PADDING);
                int row = (my - inventory.INV_START_Y) / (INV_SLOT_SIZE + INV_PADDING);
                if (col >= 0 && col < INV_COLS && row >= 0 && row < INV_ROWS) {
                    int idx = row * INV_COLS + col;
                    int slotX = inventory.INV_START_X + col * (INV_SLOT_SIZE + INV_PADDING);
                    int slotY = inventory.INV_START_Y + row * (INV_SLOT_SIZE + INV_PADDING);
                    if (mx >= slotX && mx < slotX + INV_SLOT_SIZE &&
                            my >= slotY && my < slotY + INV_SLOT_SIZE) {
                        inventory.handleSlotClick(idx, player);
                    }
                }
            }
        }

    }

    /**
     * Handles mouse release events, resetting attack and mining states or
     * forwarding to pause screen.
     *
     * @param e the MouseEvent triggered by releasing a mouse button
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if(paused)
            pauseScreen.mouseReleased(e);
        else {
            if (SwingUtilities.isLeftMouseButton(e)) {
                player.setAttacking(false);
                mining = false;
            }
        }
    }

    @Override public void mouseClicked(MouseEvent e) {
    }
    /**
     * Delegates mouse movement to the pause screen when paused.
     */
    @Override public void mouseMoved(MouseEvent e)   {
        if (paused)
            pauseScreen.mouseMoved(e);
    }

    /**
     * Processes key presses for movement, jump, pause, inventory toggles,
     * and game-over screen.
     *
     * @param e the KeyEvent triggered by a key press
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            gameOverScreen.keyPressed(e);
        } else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A :
                    player.setLeft(true);
                    break;
                case KeyEvent.VK_D :
                    player.setRight(true);
                    break;
                case KeyEvent.VK_SPACE :
                    player.setJump(true);
                    getGame().getAudioHandler().playEffect(AudioHandler.JUMP);
                    break;
                case KeyEvent.VK_ESCAPE :
                    paused = !paused;
                    break;
                case KeyEvent.VK_E :
                    invActive = !invActive;
                    break;
            }
        }
    }
    public void mouseDragged(MouseEvent e) {
        if (paused)
            pauseScreen.mouseDragged(e);
    }

    /**
     * Processes key releases for stopping movement and jump.
     *
     * @param e the KeyEvent triggered by a key release
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A -> player.setLeft(false);
                case KeyEvent.VK_D -> player.setRight(false);
                case KeyEvent.VK_SPACE -> player.setJump(false);
            }
        }
    }

    /**
     * Checks and updates camera offset when player nears screen borders.
     */
    private void checkCloseToBorder() {
        int px = (int)player.getHitbox().x;
        int py = (int)player.getHitbox().y;
        int dx = px - xLvlOffset, dy = py - yLvlOffset;
        if (dx > rightBorder)  xLvlOffset += dx - rightBorder;
        else if (dx < leftBorder) xLvlOffset += dx - leftBorder;
        xLvlOffset = Math.max(0, Math.min(xLvlOffset, maxLvlOffsetX));

        if (dy > bottomBorder)  yLvlOffset += dy - bottomBorder;
        else if (dy < topBorder) yLvlOffset += dy - topBorder;
        yLvlOffset = Math.max(0, Math.min(yLvlOffset, maxLvlOffsetY));
    }

    public void resetAll() {
        gameOver = false;
        paused   = false;
        player.resetAll();
        worldGen.resetWrld();
        world = worldGen.getWorld();
        trees = worldGen.getTrees();
        enemyManager.resetAllEnemies();
        player.loadLvlData(world);
        player.getInventory().resetInv();
        xLvlOffset = 0;
        yLvlOffset = 0;
    }
    /**
     * Checks if an enemy was hit by the player's attack box.
     *
     * @param attackBox rectangle of attack hitbox
     */
    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }

    public int[][] getWorld()  { return world;  }
    public Player getPlayer() { return player; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public void unpauseGame() {
        paused = false;
    }
}
