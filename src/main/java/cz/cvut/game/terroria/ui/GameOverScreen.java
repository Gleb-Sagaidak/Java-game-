package cz.cvut.game.terroria.ui;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.gamestates.GameState;
import cz.cvut.game.terroria.gamestates.Playing;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
/**
 * Displays and handles input for the game over screen.
 * <p>
 * Renders a semi-transparent overlay with "Game Over" text and a prompt to return to the menu,
 * and listens for the ESC key to reset and return to the main menu state.
 */
public class GameOverScreen {
    private Playing playing;
    public GameOverScreen(Playing playing) {
        this.playing = playing;
    }

    /**
     * Draws the game over overlay.
     * <p>
     * Fills the screen with a translucent black background, then renders "Game Over"
     * and instructions to press ESC to return to the menu.
     *
     * @param g the Graphics context used for drawing
     */
    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0,0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("Game Over", Game.GAME_WIDTH / 2, 100);
        g.drawString("Press ESC to enter MENU", Game.GAME_WIDTH / 2, 200);
    }
    /**
     * Handles key press events, returning to the menu when ESC is pressed.
     * <p>
     * Resets the playing state and switches the global GameState to MENU.
     *
     * @param e the KeyEvent representing the key press
     */
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            playing.resetAll();
            GameState.state = GameState.MENU;
        }
    }
}
