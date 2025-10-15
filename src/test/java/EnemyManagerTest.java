import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import cz.cvut.game.terroria.entities.EnemyManager;
import cz.cvut.game.terroria.entities.Mushroom;
import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.utils.BlockType;
import static cz.cvut.game.terroria.utils.Constants.EnemyConstants.MUSHROOM_HEIGHT_DEFAULT;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class EnemyManagerTest {

    @Test
    void testGetMushroomsSpawnCount_UnderThreshold() {
        int width = 10, height = 5;  // width/20 = 0 -> spawnCount = max(1,0) = 1
        int[][] lvlData = new int[width][height];
        // Place a "ground" at row 2
        for (int x = 0; x < width; x++) {
            lvlData[x][2] = BlockType.DIRT.ordinal();
        }
        List<Mushroom> list = EnemyManager.GetMushrooms(lvlData);
        assertEquals(1, list.size(), "Expected 1 spawn when width < 20");
    }

    @Test
    void testGetMushroomsSpawnCount_AboveThreshold() {
        int width = 100, height = 10;  // width/20 = 5 -> spawnCount = 5
        int[][] lvlData = new int[width][height];
        // Place ground at row 3
        for (int x = 0; x < width; x++) {
            lvlData[x][3] = BlockType.DIRT.ordinal();
        }
        List<Mushroom> list = EnemyManager.GetMushrooms(lvlData);
        assertEquals(5, list.size(), "Expected width/20 spawns when width >= 20");
    }

    @Test
    void testGetMushrooms_PositionAboveGround() {
        int width = 20, height = 8;
        int[][] lvlData = new int[width][height];
        // Ground at row 4
        for (int x = 0; x < width; x++) {
            lvlData[x][4] = BlockType.DIRT.ordinal();
        }
        List<Mushroom> list = EnemyManager.GetMushrooms(lvlData);
        for (Mushroom m : list) {
            float expectedY = 4 * Game.TILES_SIZE - MUSHROOM_HEIGHT_DEFAULT;
            assertEquals(expectedY, m.getHitbox().y,
                    "Mushroom should spawn just above the first solid block");
        }
    }
}