package cz.cvut.game.terroria;

import cz.cvut.game.terroria.inputs.KeyboardInputs;
import cz.cvut.game.terroria.inputs.MouseInputs;
import cz.cvut.game.terroria.utils.Constants;
import cz.cvut.game.terroria.world.WorldGenerator;

import javax.swing.*;
import java.awt.*;

/**
 * Swing panel that displays game graphics and routes input events to the game states.
 * <p>
 * Initializes mouse and keyboard listeners, sets the preferred size based on
 * window constants, and delegates painting to the {@link Game#render(Graphics)} method.
 */
public class GamePanel extends JPanel {
    /** Listener for mouse clicks, movement, and drag events. */
    private MouseInputs mouseInputs;
    private Game game;

    /**
     * Constructs the GamePanel, configures size and input listeners,
     * and stores the game reference for rendering.
     *
     * @param game the main Game object this panel will display
     */
    public GamePanel(Game game) {
        setPanelSize();
        addMouseListener(mouseInputs = new MouseInputs(this));
        addMouseMotionListener(mouseInputs);
        addKeyListener(new KeyboardInputs(this));
        this.game = game;

    }


    private void setPanelSize() {
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
    }

    /**
     * Overrides the paintComponent to clear the background and delegate
     * all rendering to the {@link Game#render(Graphics)} method.
     *
     * @param g the Graphics context used for drawing
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        game.render(g);

    }


    public void updateGame() {

    }

    public Game getGame() {
        return game;
    }
}
