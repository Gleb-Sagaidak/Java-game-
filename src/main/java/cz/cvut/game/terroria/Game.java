package cz.cvut.game.terroria;

import cz.cvut.game.terroria.audio.AudioHandler;
import cz.cvut.game.terroria.gamestates.*;

import cz.cvut.game.terroria.gamestates.Menu;
import cz.cvut.game.terroria.utils.Constants;
import cz.cvut.game.terroria.world.WorldGenerator;

import java.awt.*;

/**
 * Core game class implementing the main loop, window initialization, and state delegation.
 * <p>
 * Manages the game window and panel, audio handler, and delegates update/render calls
 * to the current {@link GameState} (PLAYING or MENU). Maintains target FPS and UPS,
 * and tracks timing for smooth rendering and updates.
 */
public class Game implements Runnable {
    /** Window wrapper for the game panel. */
    private GameWindow gameWindow;
    /** Panel where rendering occurs and input is captured. */
    private GamePanel gamePanel;
    /** Thread running the game loop. */
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;
    /** Handles background music and sound effects. */
    private AudioHandler audioHandler;


    private Playing playing;
    private Menu menu;



    public final static int TILES_DEFAULT_SIZE = 16;
    public final static float SCALE = 1f;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * WorldGenerator.WIDTH_TILES;
    public final static int GAME_HEIGHT = TILES_SIZE * WorldGenerator.HEIGHT_TILES;
    public final static int GAME_WIDTH_IN_TILES = Constants.WINDOW_WIDTH / TILES_SIZE;
    public final static int GAME_HEIGHT_IN_TILES = Constants.WINDOW_HEIGHT / TILES_SIZE;

    /**
     * Constructs the Game, initializes subsystems, and starts the game loop.
     */
    public Game() {
        initClasses();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();



        startGameLoop();

    }

    /**
     * Initializes core components: audio handler, playing and menu states.
     */
    private void initClasses() {
        audioHandler = new AudioHandler();
        playing = new Playing(this);
        menu = new Menu(this);
    }

    /**
     * Creates and starts the thread running the main game loop.
     */
    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Updates the active state logic based on the current {@link GameState}.
     */
    public void update(){
        switch (GameState.state) {
            case PLAYING:
                playing.update();
                break;
            case MENU:
                menu.update();
                break;
            case OPTIONS:
            case QUIT:
            default:
                System.exit(0);
                break;
        }
    }
    /**
     * Renders the active state to the provided graphics context.
     *
     * @param g the Graphics context for drawing
     */
    public void render(Graphics g){
        switch (GameState.state) {
            case PLAYING:
                playing.draw(g);
                break;
            case MENU:
                menu.draw(g);
                break;
            default:
                break;
        }
    }

    public Playing getPlaying(){
        return playing;
    }


    /**
     * Main game loop: regulates update and render calls to match FPS_SET and UPS_SET.
     */
    @Override
    public void run() {
        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;
        double deltaU = 0;
        double deltaF = 0;

        long previousTime = System.nanoTime();

        while (true) {

            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;
            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }
            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            if(System.currentTimeMillis() - lastCheck >= 1000){
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + "; UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }

    /**
     * Called when the game window loses focus; resets player movement flags.
     */
    public void windowFocusLost() {
        if (GameState.state == GameState.PLAYING) {
            playing.getPlayer().resetDirBooleans();
        }
    }

    public Menu getMenu() {
        return menu;
    }

    public AudioHandler getAudioHandler() {
        return audioHandler;
    }

}
