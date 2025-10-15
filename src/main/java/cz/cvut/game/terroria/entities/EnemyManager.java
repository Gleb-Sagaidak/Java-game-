package cz.cvut.game.terroria.entities;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.gamestates.Playing;
import cz.cvut.game.terroria.utils.BlockType;
import cz.cvut.game.terroria.utils.Constants;
import cz.cvut.game.terroria.utils.LoadSave;

import static cz.cvut.game.terroria.utils.Constants.EnemyConstants.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
/**
 * Manages all enemy instances within the playing state, including loading sprites,
 * spawning enemies, updating, drawing, and handling combat interactions.
 */
public class EnemyManager {
    private Playing playing;
    private BufferedImage[][] slimesArr;
    /** Sprite arrays for slime and mushroom enemy types. */
    private BufferedImage[][] mushroomArr;
    /** Active list of mushroom enemies in the world. */
    private ArrayList<Mushroom> mushrooms = new ArrayList<>();

    /**
     * Constructs the manager, loads enemy images, and spawns initial enemies.
     * @param playing the playing state providing world data and rendering context
     */
    public EnemyManager(Playing playing) {
        this.playing = playing;
        loadEnemyImgs();
        addEnemies();
    }
    /**
     * Populates the enemy list based on world level data.
     */
    private void addEnemies() {
        mushrooms = GetMushrooms(playing.getWorld());
    }

    /**
     * Updates each active enemy's behavior and physics.
     * @param lvlData tile map data for collision checks
     * @param player reference to the player for AI interactions
     */
    public void update(int[][] lvlData, Player player) {
        for(Mushroom m : mushrooms){
            m.update(lvlData, player);
        }
    }
    /**
     * Draws active enemies to the screen.
     * @param g graphics context
     * @param xLvlOffset horizontal camera offset
     * @param yLvlOffset vertical camera offset
     */
    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        drawMushrooms(g, xLvlOffset, yLvlOffset);
    }

    /**
     * Renders each active mushroom with appropriate animation frame and positioning.
     * @param g graphics context
     * @param xLvlOffset horizontal camera offset
     * @param yLvlOffset vertical camera offset
     */
    private void drawMushrooms(Graphics g, int xLvlOffset, int yLvlOffset) {
        for(Mushroom m : mushrooms){
            if (m.isActive()) {
                g.drawImage(mushroomArr[m.getEnemyState()][m.getAniIndex()],
                        (int) (m.getHitbox().x - MUSHROOM_XDRAW_OFFSET) - xLvlOffset + m.flipX(),
                        (int) (m.getHitbox().y - MUSHROOM_YDRAW_OFFSET) - yLvlOffset ,
                        MUSHROOM_WIDTH * m.flipW(), MUSHROOM_HEIGHT, null);
//                m.drawAttackBox(g, xLvlOffset, yLvlOffset);
//                m.drawHitbox(g, xLvlOffset, yLvlOffset);
            }
        }
    }
    /**
     * Loads sprite atlases for each enemy type into frame arrays.
     */
    private void loadEnemyImgs() {
        mushroomArr = new BufferedImage[5][15];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.MUSHROOM_SPRITE);
        for(int j = 0; j < mushroomArr.length; j++) {
            for(int i = 0; i < mushroomArr[j].length; i++) {
                mushroomArr[j][i] = temp.getSubimage(i * MUSHROOM_WIDTH_DEFAULT, j * MUSHROOM_HEIGHT_DEFAULT,
                        MUSHROOM_WIDTH_DEFAULT, MUSHROOM_HEIGHT_DEFAULT);
            }
        }
    }
    /**
     * Randomly generates mushroom spawn positions based on world data.
     * Ensures enemies spawn above the first solid block in each chosen column.
     * @param lvlData tile map data for level geometry
     * @return list of newly created Mushroom instances
     */
    public static ArrayList<Mushroom> GetMushrooms(int[][] lvlData){
        ArrayList<Mushroom> mushrooms1 = new ArrayList<>();
        Random random = new Random();
        int widthTiles  = lvlData.length;
        int heightTiles = lvlData[0].length;
        int spawnCount = Math.max(1, widthTiles / 20);
        for (int i = 0; i < spawnCount; i++) {
            int xTile = random.nextInt(widthTiles);
            int yTile = 0;
            for (int y = 0; y < heightTiles; y++) {
                if (lvlData[xTile][y] != BlockType.AIR.ordinal()) {
                    yTile = y;
                    break;
                }
            }
            float xPos = xTile * Game.TILES_SIZE;
            float yPos = yTile * Game.TILES_SIZE - MUSHROOM_HEIGHT_DEFAULT;
            Mushroom mushroom = new Mushroom(xPos, yPos);
            mushrooms1.add(mushroom);
        }
        return mushrooms1;
    }
    /**
     * Checks if an attack box intersects any active enemy and applies damage.
     * @param attackBox the player's attack hitbox
     */
    public void checkEnemyHit(Rectangle2D.Float attackBox){
        for(Mushroom m : mushrooms){
            if(m.isActive()){
                if(attackBox.intersects(m.getHitbox())){
                    m.hurt(10);
                    return;
                }
            }
        }
    }
    /**
     * Clears and respawns all enemies to reset the level.
     */
    public void resetAllEnemies() {
        mushrooms.clear();
        addEnemies();

    }
}
