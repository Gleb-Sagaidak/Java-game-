package cz.cvut.game.terroria.inputs;

import cz.cvut.game.terroria.GamePanel;
import cz.cvut.game.terroria.gamestates.GameState;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static cz.cvut.game.terroria.gamestates.GameState.*;

/**
 * Handles keyboard input events and delegates them to the appropriate game state handlers.
 * <p>
 * Implements the KeyListener interface to react to key presses and releases,
 * forwarding events to either the menu or gameplay logic based on the current GameState.
 */
public class KeyboardInputs implements KeyListener {

    /** Reference to the main game panel for retrieving state-specific handlers. */
    private GamePanel gamePanel;
    /**
     * Constructs a KeyboardInputs listener tied to the given GamePanel.
     *
     * @param gamePanel the GamePanel whose game logic will receive input events
     */
    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
    /**
     * Invoked when a key has been pressed. Delegates to the current state's keyPressed handler.
     *
     * @param e the KeyEvent representing the pressed key
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (GameState.state){
            case MENU:
                gamePanel.getGame().getMenu().keyPressed(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().keyPressed(e);
                break;
            default:
                break;
        }

    }
    /**
     * Invoked when a key has been released. Delegates to the current state's keyReleased handler.
     *
     * @param e the KeyEvent representing the released key
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (GameState.state){
            case MENU:
                gamePanel.getGame().getMenu().keyReleased(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().keyReleased(e);
                break;
            default:
                break;
        }
    }
}
