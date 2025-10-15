package cz.cvut.game.terroria.ui;

import cz.cvut.game.terroria.utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static cz.cvut.game.terroria.utils.Constants.PauseButtons.*;

/**
 * Represents a toggleable sound button extending the PauseButton area.
 * <p>
 * Manages visual states (normal, hover, pressed) for both muted and unmuted modes,
 * loads button sprites from the sound atlas, and exposes interaction flags.
 */
public class SoundButton extends PauseButton{
    /** 2D array of button icons: [row][col] where row=muted/unmuted, col=normal/hover/pressed. */
    private BufferedImage[][] soundImgs;
    /** Whether the cursor is currently over the button. */
    private boolean mouseOver;
    /** Whether the mouse button is currently pressed on this button. */
    private boolean mousePressed;
    /** Whether the sound is currently muted. */
    private boolean muted;
    /** Current row index (0 = unmuted, 1 = muted). */
    private int rowIndex;
    /** Current column index (0 = normal, 1 = hover, 2 = pressed). */
    private int colIndex;

    /**
     * Constructs a SoundButton at the specified position and size, loading its sprites.
     *
     * @param x      the X-coordinate of the button's top-left corner
     * @param y      the Y-coordinate of the button's top-left corner
     * @param width  the width of the button
     * @param height the height of the button
     */
    public SoundButton(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadSoundimgs();
    }


    /**
     * Loads the sound button sprites from the sound atlas into the soundImgs array.
     */
    private void loadSoundimgs() {
        soundImgs = new BufferedImage[2][3];
        BufferedImage tmp = LoadSave.GetSpriteAtlas(LoadSave.SOUND_BUTTONS);
        for(int x = 0; x < soundImgs.length; x++) {
            for(int y = 0; y < soundImgs[0].length; y++) {
                soundImgs[x][y] = tmp.getSubimage(y * SOUND_SIZE_DEF, x * SOUND_SIZE_DEF, SOUND_SIZE_DEF, SOUND_SIZE_DEF);
            }
        }
    }

    /**
     * Draws the current sound button icon based on muted and interaction state.
     *
     * @param g the Graphics context used for rendering
     */
    public void draw(Graphics g) {
        g.drawImage(soundImgs[rowIndex][colIndex], x, y,width,height,null);
    }
    /**
     * Updates the row and column indices according to muted, mouseOver, and mousePressed flags.
     */
    public void update() {
        if(muted) {
            rowIndex = 1;
        }else {
            rowIndex = 0;
        }
        colIndex = 0;
        if(mouseOver)
            colIndex = 1;
        if(mousePressed)
            colIndex = 2;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void resetBools(){
        mouseOver = false;
        mousePressed = false;
    }
}
