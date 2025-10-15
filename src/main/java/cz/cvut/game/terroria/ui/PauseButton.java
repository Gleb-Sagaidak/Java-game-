package cz.cvut.game.terroria.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents a rectangular pause button area that can be positioned and resized.
 * <p>
 * Stores its coordinates, dimensions, and hit-detection bounds, with getters and setters
 * for all properties.
 */
public class PauseButton {
    /** X-coordinate of the button's top-left corner. */
    protected int x;
    /** Y-coordinate of the button's top-left corner. */
    protected int y;
    /** Width of the button. */
    protected int width;
    /** Height of the button. */
    protected int height;

    /** Rectangle defining the button's clickable area. */
    protected Rectangle bounds;

    /**
     * Constructs a PauseButton with specified position and dimensions.
     *
     * @param x      the X-coordinate of the top-left corner
     * @param y      the Y-coordinate of the top-left corner
     * @param width  the width of the button
     * @param height the height of the button
     */
    public PauseButton(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        initBounds();
    }
    /**
     * Initializes the bounds Rectangle based on x, y, width, and height.
     */
    private void initBounds() {
        bounds = new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
