package cz.cvut.game.terroria.ui;

import cz.cvut.game.terroria.gamestates.GameState;
import cz.cvut.game.terroria.utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static cz.cvut.game.terroria.utils.Constants.Buttons.*;

/**
 * Represents an interactive menu button in the game's user interface.
 * <p>
 * Each MenuScreen instance manages its position, visual state (normal/hovered/pressed),
 * and associated GameState. It handles loading button sprites, tracking mouse interactions,
 * and updating the global game state when activated.
 */
public class MenuScreen {
    /** X-coordinate of the button's center position. */
    private final int xPos;
    /** Y-coordinate of the button's top position. */
    private final int yPos;
    /** Row index in the sprite atlas for this button's images. */
    private final int rowIndex;
    /** GameState to apply when this button is activated. */
    private final GameState state;

    /** Horizontal offset used to center the button (half of B_WIDTH). */
    private final int xOffsetCenter = B_WIDTH / 2;

    /** Array of BufferedImage frames: [0]=normal, [1]=hover, [2]=pressed. */
    private BufferedImage[] imgs;
    /** Current image index based on mouse interaction state. */
    private int index;

    /** Whether the mouse cursor is currently over the button. */
    private boolean mouseOver;
    /** Whether the mouse button is currently pressed on this button. */
    private boolean mousePressed;

    /** Clickable area for hit-detection. */
    private Rectangle bounds;

    /**
     * Constructs a MenuScreen button with the given position, sprite row, and action state.
     *
     * @param xPos      the X-coordinate of the button's center
     * @param yPos      the Y-coordinate of the button's top edge
     * @param rowIndex  the row in the sprite atlas for this button's images
     * @param state     the GameState to switch to when the button is clicked
     */
    public MenuScreen(int xPos, int yPos,  int rowIndex,GameState state) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.state = state;
        this.rowIndex = rowIndex;
        loadImgs();
        initBounds();
    }
    /**
     * Initializes the clickable bounding rectangle based on position and button dimensions.
     */
    private void initBounds() {
        bounds = new Rectangle(xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT);
    }

    /**
     * Loads the three button sprites (normal, hover, pressed) from the sprite atlas.
     */
    private void loadImgs() {
        imgs = new BufferedImage[3];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);
        for (int i = 0; i < imgs.length; i++) {
            imgs[i] =temp.getSubimage(i * B_WIDTH_DEFAULT, rowIndex * B_HEIGHT_DEFAULT, B_WIDTH_DEFAULT, B_HEIGHT_DEFAULT);
        }
    }

    /**
     * Draws the button with the current interaction frame.
     *
     * @param g the Graphics context used for rendering
     */
    public void draw(Graphics g) {
        g.drawImage(imgs[index], xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT, null);
    }
    /**
     * Updates the current frame index based on mouseOver and mousePressed flags.
     */
    public void update(){
        index = 0;
        if (mouseOver) {
            index = 1;
        }
        if (mousePressed) {
            index = 2;
        }
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public void applyGameState() {
        GameState.state = state;
    }

    public void resetBools(){
        mouseOver = false;
        mousePressed = false;
    }

    public GameState getState() {
        return state;
    }
}
