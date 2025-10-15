package cz.cvut.game.terroria.world;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.utils.BlockType;
import cz.cvut.game.terroria.utils.Constants;
import fastnoise.FastNoiseLite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

import static cz.cvut.game.terroria.Game.TILES_SIZE;

/**
 * Generates and manages the game world terrain, caves, ores, and trees using noise and randomness.
 * <p>
 * The world is represented as a 2D grid of tile IDs, with dimensions WIDTH_TILES x HEIGHT_TILES.
 * Terrain height is determined by Perlin noise, caves are carved randomly, ores are scattered,
 * and trees are placed at intervals. Provides methods to draw the visible portion of the world,
 * destroy or set blocks, and reset the world with a new seed.
 */
public class WorldGenerator {
    public static final int WIDTH_TILES = 1000;
    public static final int HEIGHT_TILES = 75;
    public static final int GROUND_LEVEL = 25;
    private static final float NOISE_SCALE = 1.5f;
    private static final float CAVE_DENSITY = 0.25f;
    private int[][] world = new int[WIDTH_TILES][HEIGHT_TILES];
    private FastNoiseLite noise;
    private Random random;

    /**
     * Constructs the world generator with the given seed, initializes noise and randomness,
     * and runs generation routines for terrain, caves, ores, and trees.
     *
     * @param seed seed value for reproducible world generation
     */
    public WorldGenerator(long seed) {
        noise = new FastNoiseLite((int) seed);
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        random = new Random(seed);
        generateTerrain();
        generateCaves();
        generateOres();
        generateTrees();
    }

    /** Generates ground, dirt, and air tiles across the world width based on Perlin noise. */
    private void generateTerrain() {
        for (int x = 0; x < WIDTH_TILES; x++) {
            double noiseValue = noise.GetNoise(x * NOISE_SCALE, 0);
            int groundHeight = GROUND_LEVEL + (int) (noiseValue * 15);
            for (int y = 0; y < HEIGHT_TILES; y++) {
                if (y < groundHeight) {
                    world[x][y] = 0;
                } else if (y == groundHeight) {
                    world[x][y] = 1;
                } else {
                    world[x][y] = 2; 
                }
            }
        }
    }

    /** Randomly carves out caves in the lower half of the world based on CAVE_DENSITY. */
    private void generateCaves() {
        for (int x = 0; x < WIDTH_TILES; x++) {
            for (int y = HEIGHT_TILES / 2; y < HEIGHT_TILES; y++) {
                if (random.nextFloat() < CAVE_DENSITY) {
                    world[x][y] = 0;
                }
            }
        }
    }

    /** Randomly replaces dirt tiles with ore blocks at a 5% chance in cave regions. */
    private void generateOres() {
        for (int x = 0; x < WIDTH_TILES; x++) {
            for (int y = HEIGHT_TILES / 2; y < HEIGHT_TILES; y++) {
                if (world[x][y] == 2 && random.nextFloat() < 0.05) {
                    world[x][y] = 3;
                }
            }
        }
    }
    /** Draws the visible tiles on screen, offset by level scroll values. */
    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        int tileSize = Game.TILES_SIZE;
        int screenW = Constants.WINDOW_WIDTH;
        int screenH = Constants.WINDOW_HEIGHT;


        int xStart = Math.max(0, xLvlOffset / tileSize);
        int xEnd   = Math.min(world.length,
                (xLvlOffset + screenW) / tileSize + 1);

        int yStart = Math.max(0, yLvlOffset / tileSize);
        int yEnd   = Math.min(world[0].length,
                (yLvlOffset + screenH) / tileSize + 1);

        for (int x = xStart; x < xEnd; x++) {
            for (int y = yStart; y < yEnd; y++) {
                BlockType type = BlockType.values()[ world[x][y] ];
                BufferedImage tile = type.getTile();
                if (tile != null) {
                    g.drawImage(
                            tile,
                            x * tileSize - xLvlOffset,
                            y * tileSize - yLvlOffset,
                            tileSize, tileSize,
                            null
                    );
                }
            }
        }
    }
    /**
     * Sets the tile at the given coordinates to AIR, simulating block destruction.
     *
     * @param x tile column index
     * @param y tile row index
     */
    public void destroyBlock(int x, int y) {
        if (x >= 0 && x < WIDTH_TILES
                && y >= 0 && y < HEIGHT_TILES) {
            world[x][y] = 0;
        }
    }

    private List<WorldGenerator.Tree> trees;

    /** Resets the world generation with a new time-based seed and regenerates all features. */
    public void resetWrld() {
        long newSeed = System.currentTimeMillis();
        noise = new FastNoiseLite((int) newSeed);
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        random = new Random(newSeed);

        generateTerrain();
        generateCaves();
        generateOres();
        generateTrees();
    }

    /**
     * Encapsulates a tree's position and variant type in the world.
     */
    public static class Tree {
        public final int tileX, tileY, type;
        public Tree(int tileX, int tileY, int type) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.type  = type;
        }
    }

    /**
     * Returns the list of generated trees for placement in rendering or logic.
     *
     * @return List of Tree objects
     */
    public List<Tree> getTrees() {
        return trees;
    }

    /**
     * Randomly places trees across the world at intervals of 10-30 tiles atop ground.
     */
    private void generateTrees() {
        trees = new ArrayList<>();
        int x = 0;
        while (x < WIDTH_TILES) {
            x += 10 + random.nextInt(21);
            if (x >= WIDTH_TILES) break;

            int groundY = 0;
            for (int y = 0; y < HEIGHT_TILES; y++) {
                if (world[x][y] != BlockType.AIR.ordinal()) {
                    groundY = y;
                    break;
                }
            }

            int type = random.nextInt(3);  // 0,1,2
            trees.add(new Tree(x, groundY - 1, type));
        }
    }

    /**
     * Sets a block at the specified coordinates to the given type.
     *
     * @param x    tile column index
     * @param y    tile row index
     * @param type BlockType to place
     */
    public void setBlock(int x, int y, BlockType type) {
        if (x >= 0 && x < WIDTH_TILES
                && y >= 0 && y < HEIGHT_TILES) {
            world[x][y] = type.ordinal();
        }
    }

    public void update() {

    }

    public int[][] getWorld() {
        return world;
    }
}
