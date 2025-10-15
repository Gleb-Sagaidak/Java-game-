package cz.cvut.game.terroria.ui;

import cz.cvut.game.terroria.utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static cz.cvut.game.terroria.utils.Constants.URMButtons.*;

/**
 * Represents an undo/redo/menu (URM) button extending the PauseButton area.
 * <p>
 * Manages visual states (normal, hover, pressed) by selecting the appropriate
 * sprite from the URM button atlas based on row and column indices.
 */
public class UrmButton extends PauseButton {
    /** Array of button icons for normal, hover, and pressed states. */
    private BufferedImage[] imgs;
    /** The row in the URM atlas determining button type. */
    private int rowIndex;
    /** Current image index based on mouse interaction (0=normal,1=hover,2=pressed). */
    private int index;
    /** Whether the cursor is currently over the button. */
    private boolean mouseOver;
    /** Whether the mouse button is currently pressed on this button. */
    private boolean mousePressed;

    /**
     * Constructs a URM button at the specified position and size with a given atlas row.
     *
     * @param x        the X-coordinate of the button's top-left corner
     * @param y        the Y-coordinate of the button's top-left corner
     * @param width    the width of the button
     * @param height   the height of the button
     * @param rowIndex the row in the URM sprite atlas for this button type
     */
    public UrmButton(int x, int y, int width, int height, int rowIndex) {
        super(x, y, width, height);
        this.rowIndex = rowIndex;
        loadImgs();
    }

    /**
     * Loads the three URM button sprites (normal, hover, pressed) from the atlas.
     */
    private void loadImgs() {
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.URM_BUTTONS);
        imgs = new BufferedImage[3];
        for (int i = 0; i < imgs.length; i++)
            imgs[i] = temp.getSubimage(i * URM_DEFAULT_SIZE, rowIndex * URM_DEFAULT_SIZE, URM_DEFAULT_SIZE, URM_DEFAULT_SIZE);

    }

    /**
     * Updates the image index based on mouseOver and mousePressed flags.
     */
    public void update() {
        index = 0;
        if (mouseOver)
            index = 1;
        if (mousePressed)
            index = 2;

    }

    /**
     * Draws the current URM button icon.
     *
     * @param g the Graphics context used for rendering
     */
    public void draw(Graphics g) {
        g.drawImage(imgs[index], x, y, URM_SIZE, URM_SIZE, null);
    }

    public void resetBools() {
        mouseOver = false;
        mousePressed = false;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }
}
