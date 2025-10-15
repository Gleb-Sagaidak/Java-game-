package cz.cvut.game.terroria.utils;

import cz.cvut.game.terroria.entities.Slime;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
/**
 * Utility class for loading image resources and extracting sprite atlases and tiles.
 * <p>
 * Defines constants for various resource filenames and provides methods to load
 * single images, arrays of images, and tiled subimages from sprite sheets.
 * Also caches plank textures from the main textures atlas.
 */
public class LoadSave {
    // --- Resource filename constants ---
    public static final String MENU_BUTTONS = "button_atlas.png";
    public static final String MENU_BACKGROUND = "menu_background.png";
    public static final String CHARACTER_ATLAS = "male_skin1.png";
    public static final String SLIME_SPRITE = "slime_enemblue.png";
    public static final String MUSHROOM_SPRITE = "mushroom_sprite.png";
    public static final String PLAYING_BACKGROUND = "Mountain-Dusk.png";
    public static final String HEALTH_BAR = "health_bar.png";
    public static final String TERRAIN_TEXTURES = "Floor_Tiles2.png";
    public static final String CAVE_TEXTURES = "mainlev_build.png";
    public static final String ORE_TEXTURES = "ore.png";
    public static final String TEXTURES_ATLAS = "Textures-16.png";
    private static final int ATLAS_TILE_SIZE = 16;
    public static final String PANTS_SPRITE = "Pants.png";
    public static final String SHIRT_SPRITE = "Shirt.png";
    public static final String SHOES_SPRITE = "Shoes.png";
    public static final String SWORD_SPRITE = "Sword.png";
    public static final String PAUSE_BACK = "pause_menu.png";
    public static final String SOUND_BUTTONS = "sound_button.png";
    public static final String URM_BUTTONS = "urm_buttons.png";
    public static final String VOLUME_BUTTONS = "volume_buttons.png";
    public static final String CAVE_BACKGROUND = "backgroundCaves.png";

    /**
     * Preloaded array of plank textures extracted from the textures atlas row 13.
     */
    private static BufferedImage[] PLANK_TEXTURES;
    static {
        BufferedImage[][] atlas = GetTiles(TEXTURES_ATLAS, ATLAS_TILE_SIZE);
        int plankRow = 13;
        if (plankRow < atlas.length) {
            PLANK_TEXTURES = atlas[plankRow];
        } else {
            PLANK_TEXTURES = new BufferedImage[0];
        }
    }
    /**
     * Loads a sprite atlas image resource from the classpath.
     *
     * @param fileName the filename of the resource (relative to classpath root)
     * @return BufferedImage of the loaded atlas, or null if loading failed
     */
    public static BufferedImage GetSpriteAtlas(String fileName) {
        BufferedImage image = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
        try {
            image = ImageIO.read(is);
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return image;
    }
    /**
     * Splits a sprite sheet into a 2D array of tiles of the given size.
     *
     * @param fileName the filename of the sprite sheet
     * @param tileSize the width and height of each square tile in pixels
     * @return two-dimensional BufferedImage array [row][col] of tiles
     */
    public static BufferedImage[][] GetTiles(String fileName, int tileSize) {
        BufferedImage sheet = GetSpriteAtlas(fileName);
        int cols = sheet.getWidth()  / tileSize;
        int rows = sheet.getHeight() / tileSize;
        BufferedImage[][] tiles = new BufferedImage[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c] = sheet.getSubimage(
                        c * tileSize,
                        r * tileSize,
                        tileSize, tileSize
                );
            }
        }
        return tiles;
    }
    /**
     * Loads a single image resource from the classpath.
     *
     * @param fileName the filename of the image resource
     * @return BufferedImage of the loaded image, or null if loading failed
     */
    public static BufferedImage loadImage(String fileName) {
        BufferedImage img = null;
        try (InputStream is = LoadSave.class.getResourceAsStream("/" + fileName)) {
            if (is == null) throw new IOException("Resource not found: " + fileName);
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }
    /**
     * Loads multiple image resources given their filenames.
     *
     * @param fileNames array of filenames to load
     * @return array of BufferedImages corresponding to the loaded files
     */
    public static BufferedImage[] loadImages(String... fileNames) {
        BufferedImage[] out = new BufferedImage[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            out[i] = loadImage(fileNames[i]);
        }
        return out;
    }
    /**
     * Retrieves a plank texture by its column index from the preloaded atlas row.
     *
     * @param col the column index of the desired plank texture
     * @return BufferedImage of the plank texture, or null if index out of range
     */
    public static BufferedImage getPlankTexture(int col) {
        if (col >= 0 && col < PLANK_TEXTURES.length) {
            return PLANK_TEXTURES[col];
        }
        return null;
    }

}
