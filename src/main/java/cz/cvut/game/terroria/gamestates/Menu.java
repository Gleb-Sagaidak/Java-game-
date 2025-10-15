package cz.cvut.game.terroria.gamestates;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.audio.AudioHandler;
import cz.cvut.game.terroria.ui.MenuScreen;
import cz.cvut.game.terroria.utils.Constants;
import cz.cvut.game.terroria.utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static cz.cvut.game.terroria.utils.Constants.Buttons.*;

/**
 * The main menu state of the game, responsible for rendering the menu background,
 * loading and displaying menu buttons, and handling mouse interactions.
 * <p>
 * Extends {@link State} and implements {@link StateMethods} to integrate with
 * the game's state management and input handling system.
 */
public class Menu extends State implements StateMethods {
    /** Array of menu buttons (e.g., Play, Options, Quit). */
    private MenuScreen[] buttns = new MenuScreen[3];
    /** Background image displayed behind the menu buttons. */
    private BufferedImage backgroundImg;

    private int menuX, menuY,menuWidth,menuHeight;

    /**
     * Constructs the Menu state, loading the background and initializing buttons.
     *
     * @param game reference to the main Game instance
     */
    public Menu(Game game) {
        super(game);
        loadbackground();
        loadButtons();
    }

    /**
     * Loads and scales the menu background image, and centers it on screen.
     */
    private void loadbackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
        menuWidth = (int) (backgroundImg.getWidth() * Game.SCALE);
        menuHeight = (int) (backgroundImg.getHeight() * Game.SCALE);
        menuX = (Constants.WINDOW_WIDTH   - menuWidth)  / 2;
        menuY = (Constants.WINDOW_HEIGHT - menuHeight) / 2;

    }

    /**
     * Creates and positions the MenuScreen buttons vertically centered within the background.
     */
    private void loadButtons() {
        int count   = 3;
        buttns     = new MenuScreen[count];
        int cx      = Constants.WINDOW_WIDTH / 2;


        int btnH    = (int)(B_HEIGHT_DEFAULT * Game.SCALE);

        int padding = btnH / 4;
        int totalH  = btnH * count + padding * (count - 1);

        int startY = menuY + (menuHeight - totalH) / 2 + btnH / 2;

        for (int i = 0; i < count; i++) {
            int y = startY + i * (btnH + padding);
            buttns[i] = new MenuScreen(cx, y, i, GameState.values()[i]);
        }
    }

    /**
     * Updates each button's visual state (normal, hover, pressed).
     */
    @Override
    public void update() {
        for (MenuScreen buttn : buttns) {
            buttn.update();
        }
    }

    /**
     * Draws the menu background and all buttons to the screen.
     *
     * @param g the Graphics context for rendering
     */
    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, menuX, menuY  , menuWidth, menuHeight, null);
        for (MenuScreen buttn : buttns) {
            buttn.draw(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Marks a button as pressed when the mouse is pressed within its bounds.
     *
     * @param e the MouseEvent representing the press
     */
    @Override
    public void mousePressed(MouseEvent e) {
        for (MenuScreen b : buttns) {
            if(isIn(e, b)) {
                b.setMousePressed(true);
                break;
            }
        }

    }

    /**
     * When the mouse is released, if released over the same pressed button,
     * applies the button's GameState action and resets all button flags.
     *
     * @param e the MouseEvent representing the release
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        for (MenuScreen b : buttns) {
            if(isIn(e, b)) {
                if(b.isMousePressed()) {
                    b.applyGameState();
                }
                if(b.getState() == GameState.PLAYING) {
                    game.getAudioHandler().setSong();
                }
                break;
            }
        }
        resetButtons();
    }

    private void resetButtons() {
        for (MenuScreen b : buttns) {
            b.resetBools();
        }
    }

    /**
     * Updates mouseOver flags based on cursor movement over buttons.
     *
     * @param e the MouseEvent representing the movement
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        for (MenuScreen b : buttns) {
            b.setMouseOver(false);
        }
        for (MenuScreen b : buttns) {
            if(isIn(e, b)) {
                b.setMouseOver(true);
                break;
            }
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
