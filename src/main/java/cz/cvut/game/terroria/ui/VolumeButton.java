package cz.cvut.game.terroria.ui;

import cz.cvut.game.terroria.utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static cz.cvut.game.terroria.utils.Constants.VolumeButtons.*;

/**
 * Represents a draggable volume control slider button extending the PauseButton area.
 * <p>
 * Displays a horizontal slider track and a movable thumb button that reflects
 * the current volume level (0.0 to 1.0). Handles mouse interactions to update
 * position and compute normalized volume value.
 */
public class VolumeButton extends PauseButton{
    /** Array of thumb button icons: [0]=normal, [1]=hover, [2]=pressed. */
    private BufferedImage[] imgs;
    /** Slider track image. */
    private BufferedImage slider;
    /** Current icon index based on mouse interaction. */
    private int index = 0;
    /** Whether the cursor is currently over the thumb button. */
    private boolean mouseOver;
    /** Whether the thumb button is currently pressed. */
    private boolean mousePressed;
    /** X-coordinate of the thumb button center. */
    private int buttonX;
    /** Minimum allowable X-coordinate for the thumb (left bound). */
    private int minX;
    /** Maximum allowable X-coordinate for the thumb (right bound). */
    private int maxX;
    /** Current volume value as a normalized float from 0.0 (min) to 1.0 (max). */
    private float floatValue = 0f;


    /**
     * Constructs a VolumeButton slider at the specified position and width.
     * <p>
     * The thumb is centered initially and the bounds for dragging are computed.
     *
     * @param x      the X-coordinate of the slider's left edge
     * @param y      the Y-coordinate of the slider's top edge
     * @param width  the total width of the slider track
     * @param height the height of the slider and thumb button
     */
    public VolumeButton(int x, int y, int width, int height) {
        super(x + width / 2, y, VOLUME_WIDTH, height);
        bounds.x -= VOLUME_WIDTH / 2;
        buttonX = x + width / 2;
        this.x = x;
        this.width = width;
        minX = x + VOLUME_WIDTH / 2;
        maxX = x + width - VOLUME_WIDTH / 2;
        loadImgs();
    }

    /**
     * Loads slider track and thumb button sprites from the volume atlas.
     */
    private void loadImgs() {
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.VOLUME_BUTTONS);
        imgs = new BufferedImage[3];
        for (int i = 0; i < imgs.length; i++)
            imgs[i] = temp.getSubimage(i * VOLUME_DEFAULT_WIDTH, 0, VOLUME_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT);

        slider = temp.getSubimage(3 * VOLUME_DEFAULT_WIDTH, 0, SLIDER_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT);

    }
    /**
     * Updates the thumb icon index based on mouseOver and mousePressed flags.
     */
    public void update() {
        index = 0;
        if (mouseOver)
            index = 1;
        if (mousePressed)
            index = 2;

    }
    /**
     * Draws the slider track and thumb button at the current position.
     *
     * @param g the Graphics context used for rendering
     */
    public void draw(Graphics g) {

        g.drawImage(slider, x, y, width, height, null);
        g.drawImage(imgs[index], buttonX - VOLUME_WIDTH / 2, y, VOLUME_WIDTH, height, null);

    }

    /**
     * Moves the thumb button to the given X-coordinate within bounds and updates volume.
     *
     * @param x the new X-coordinate of the cursor while dragging
     */
    public void changeX(int x) {
        if (x < minX)
            buttonX = minX;
        else if (x > maxX)
            buttonX = maxX;
        else
            buttonX = x;
        updateFloatValue();
        bounds.x = buttonX - VOLUME_WIDTH / 2;
    }
    /**
     * Computes the normalized volume value based on the thumb position.
     */
    private void updateFloatValue(){
        float range = maxX - minX;
        float value = buttonX - minX;
        floatValue = value / range;
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

    public float getFloatValue() {
        return floatValue;
    }
}
