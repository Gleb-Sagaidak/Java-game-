package cz.cvut.game.terroria;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * Top-level application window containing the game panel.
 * <p>
 * Wraps a {@link GamePanel} in a {@link JFrame}, sets window properties
 * (title, size, close operation), and forwards focus loss events to
 * pause game logic.
 */
public class GameWindow extends JFrame {
    private JFrame jframe;

    /**
     * Constructs the game window, embeds the provided game panel,
     * and registers focus listeners to handle pause on focus loss.
     *
     * @param gamePanel the GamePanel instance to display in this window
     */
    public GameWindow(GamePanel gamePanel) {
        jframe = new JFrame();
        jframe.setTitle("Terroria");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.add(gamePanel);
        jframe.setResizable(false);
        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
        jframe.addWindowFocusListener(new WindowFocusListener() {

            @Override
            public void windowGainedFocus(WindowEvent e) {

            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                gamePanel.getGame().windowFocusLost();
            }
        });

    }
}
