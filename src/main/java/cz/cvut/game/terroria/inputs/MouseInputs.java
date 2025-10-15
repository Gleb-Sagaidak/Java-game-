package cz.cvut.game.terroria.inputs;

import cz.cvut.game.terroria.GamePanel;
import cz.cvut.game.terroria.gamestates.GameState;
import cz.cvut.game.terroria.gamestates.Menu;
import cz.cvut.game.terroria.ui.MenuScreen;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static cz.cvut.game.terroria.gamestates.GameState.PLAYING;

/**
 * Handles mouse input events (clicks, presses, releases, movement, dragging) and delegates
 * them to the appropriate game state handlers.
 * <p>
 * Implements both MouseListener and MouseMotionListener interfaces to forward events
 * to either the menu or gameplay logic based on the current GameState.
 */
public class MouseInputs implements MouseListener, MouseMotionListener {
    /** Reference to the main GamePanel for retrieving state-specific handlers. */
    private GamePanel gamePanel;

    /**
     * Constructs a MouseInputs listener tied to the given GamePanel.
     *
     * @param gamePanel the GamePanel whose game logic will receive mouse events
     */
    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    @Override
    public void mouseClicked(MouseEvent e) {

    }
    /**
     * Invoked when a mouse button has been pressed. Delegates to the current state's handler.
     *
     * @param e the MouseEvent representing the press
     */
    @Override
    public void mousePressed(MouseEvent e) {
        switch (GameState.state){
            case MENU:
                gamePanel.getGame().getMenu().mousePressed(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().mousePressed(e);
                break;

        }

    }

    /**
     * Invoked when a mouse button has been released. Delegates to the current state's handler.
     *
     * @param e the MouseEvent representing the release
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        switch (GameState.state){
            case MENU:
                gamePanel.getGame().getMenu().mouseReleased(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().mouseReleased(e);
                break;

        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Invoked when the mouse is moved (without buttons pressed). Delegates to the current state's handler.
     *
     * @param e the MouseEvent representing the movement
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        switch (GameState.state){
            case MENU:
                gamePanel.getGame().getMenu().mouseMoved(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().mouseMoved(e);
                break;

        }
    }
    /**
     * Invoked when the mouse is dragged (moved with button pressed). Delegates to the PLAYING state's handler.
     *
     * @param e the MouseEvent representing the drag
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        switch (GameState.state) {
            case PLAYING:
                gamePanel.getGame().getPlaying().mouseDragged(e);
                break;
            default:
                break;

        }
    }

}
