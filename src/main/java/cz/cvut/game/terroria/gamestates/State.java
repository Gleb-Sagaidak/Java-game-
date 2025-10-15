package cz.cvut.game.terroria.gamestates;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.audio.AudioHandler;
import cz.cvut.game.terroria.ui.MenuScreen;

import java.awt.event.MouseEvent;

/**
 * Abstract base class for game state handlers, providing common utilities
 * such as accessing the main Game instance, input region checks, and
 * switching global game state with associated audio transitions.
 */
public class State {
    /** Reference to the main game, used for accessing subsystems like AudioHandler. */
    protected Game game;
    /**
     * Constructs a State tied to the given Game instance.
     *
     * @param game the Game instance this state will manipulate
     */
    public State(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    /**
     * Determines whether the given mouse event occurred within the bounds
     * of the specified MenuScreen button.
     *
     * @param e the MouseEvent to test
     * @param b the MenuScreen button whose bounds to check
     * @return true if the event's coordinates lie inside the button's bounds
     */
    public boolean isIn(MouseEvent e, MenuScreen b) {
        return b.getBounds().contains(e.getX(), e.getY());
    }

    public void setgameState(GameState state){
        switch(state){
            case MENU -> game.getAudioHandler().setSong();
            case PLAYING -> game.getAudioHandler().setSong();
        }
        GameState.state = state;
    }
}
