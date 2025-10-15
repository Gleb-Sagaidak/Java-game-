package cz.cvut.game.terroria.gamestates;

/**
 * Enumeration of the game's high-level states with a mutable current state field.
 * <p>
 * Defines the primary modes the game can be in:
 * <ul>
 *   <li>PLAYING - active gameplay state,</li>
 *   <li>MENU    - main menu or pause menu state,</li>
 *   <li>QUIT    - game termination state,</li>
 *   <li>OPTIONS - settings or options menu state.</li>
 * </ul>
 * <p>
 * The static field {@code state} tracks the current GameState.
 */
public enum GameState {

    /** Active gameplay where player input affects the game world. */
    PLAYING,
    /** Main menu or pause menu where players can navigate options. */
    MENU,
    /** State indicating the game should exit. */
    QUIT,
    /** Options or settings menu state. */
    OPTIONS;

    /**
     * The current global game state. Initialized to MENU.
     */
    public static GameState state = MENU;
}
