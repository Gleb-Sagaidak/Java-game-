package cz.cvut.game.terroria.utils;

import cz.cvut.game.terroria.Game;

/**
 * Defines global configuration constants for game dimensions, UI element sizes,
 * directional values, and character/enemy properties.
 * <p>
 * Nested classes group related constants for clarity: button sizes, pause menu controls,
 * directions, enemy and player animation/state parameters.
 */
public class Constants {
    /** Window width in pixels. */
    public static final int WINDOW_WIDTH  = 1200;
    /** Window height in pixels. */
    public static final int WINDOW_HEIGHT = 720;
    /**
     * Button size constants for menu UI.
     */
    public static class Buttons{
        public static final int B_WIDTH_DEFAULT = 140;
        public static final int B_HEIGHT_DEFAULT = 56;
        public static final int B_WIDTH = (int) (B_WIDTH_DEFAULT * Game.SCALE);
        public static final int B_HEIGHT = (int) (B_HEIGHT_DEFAULT * Game.SCALE);
    }
    /**
     * Size constants for pause menu sound and URM buttons.
     */
    public static class PauseButtons{
        public static final int SOUND_SIZE_DEF = 42;
        public static final int SOUND_SIZE = (int) (SOUND_SIZE_DEF * Game.SCALE);
    }
    /**
     * Size constants for undo/redo/menu (URM) buttons.
     */
    public static class URMButtons {
        public static final int URM_DEFAULT_SIZE = 56;
        public static final int URM_SIZE = (int) (URM_DEFAULT_SIZE * Game.SCALE);

    }
    /**
     * Size constants for volume slider and thumb.
     */
    public static class VolumeButtons {
        public static final int VOLUME_DEFAULT_WIDTH = 28;
        public static final int VOLUME_DEFAULT_HEIGHT = 44;
        public static final int SLIDER_DEFAULT_WIDTH = 215;

        public static final int VOLUME_WIDTH = (int) (VOLUME_DEFAULT_WIDTH * Game.SCALE);
        public static final int VOLUME_HEIGHT = (int) (VOLUME_DEFAULT_HEIGHT * Game.SCALE);
        public static final int SLIDER_WIDTH = (int) (SLIDER_DEFAULT_WIDTH * Game.SCALE);
    }
    /**
     * Directional constants for entity movement.
     */
    public static class Directions {
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }
    /**
     * Constants and helper methods for enemy types, states, dimensions, offsets,
     * and stats like health and damage.
     */
    public static class EnemyConstants {
        public static final int MUSHROOM = 0;
        public static final int SLIME = 1;
        public static final int IDLE = 0;
        public static final int RUNNING  = 1;
        public static final int ATTACK = 2;
        public static final int HIT = 3;
        public static final int DEAD = 4;

        public static final int MUSHROOM_WIDTH_DEFAULT = 80;
        public static final int MUSHROOM_HEIGHT_DEFAULT = 64;

        public static final int SLIME_WIDTH_DEFAULT = 32;
        public static final int SLIME_HEIGHT_DEFAULT = 32;

        public static final int SLIME_WIDTH = (int) (SLIME_WIDTH_DEFAULT * Game.SCALE);
        public static final int SLIME_HEIGHT = (int) (SLIME_HEIGHT_DEFAULT * Game.SCALE);

        public static final int MUSHROOM_WIDTH = (int) (MUSHROOM_WIDTH_DEFAULT * Game.SCALE);
        public static final int MUSHROOM_HEIGHT = (int) (MUSHROOM_HEIGHT_DEFAULT * Game.SCALE);

        public static final int MUSHROOM_XDRAW_OFFSET = 33;
        public static final int MUSHROOM_YDRAW_OFFSET = 30;

        public static final int SLIME_XDRAW_OFFSET = 3;
        public static final int SLIME_YDRAW_OFFSET = 19;

        /**
         * Returns the number of frames for a given enemy type and state.
         *
         * @param enemy_type  the type constant (SLIME or MUSHROOM)
         * @param enemy_state the state constant (IDLE, RUNNING, etc.)
         * @return frame count or 0 if unspecified
         */
        public static int GetSpriteAmount(int enemy_type, int enemy_state) {

            switch (enemy_type) {
                case SLIME:
                    switch (enemy_state) {
                        case IDLE:
                            return 5;
                        case RUNNING:
                            return 8;
                        case DEAD:
                            return 6;
                    }
                case MUSHROOM:
                    switch (enemy_state) {
                        case IDLE:
                            return 7;
                        case RUNNING:
                            return 8;
                        case ATTACK:
                            return 10;
                        case HIT:
                            return 5;
                        case DEAD:
                            return 15;
                    }
            }

            return 0;
        }
        public static int GetMaxHealth(int enemy_type) {
            switch (enemy_type) {
                case SLIME:
                    return 15;
                case MUSHROOM:
                    return 10;
                default:
                    return 1;
            }
        }
        public static int GetEnemyDmg(int enemy_type) {
            switch (enemy_type) {
                case SLIME:
                    return 10;
                case MUSHROOM:
                    return 1;
                default:
                    return 0;
            }
        }
    }
    /**
     * Constants and helper for player action states and sprite counts.
     */
    public static class PlayerConstants {
        public static final int IDLE = 0;
        public static final int WALKING  = 1;
        public static final int RUNNING = 2;
        public static final int JUMP = 3;
        public static final int FALLING = 4;
        public static final int ATTACK_1 = 5;
        public static final int GROUND = 6;

        /**
         * Returns the number of frames for the specified player action.
         *
         * @param player_action the action constant (IDLE, WALKING, etc.)
         * @return number of frames or 1 if unspecified
         */
        public static int GetSpriteAmounts(int player_action) {
            switch (player_action) {
                case IDLE:
                    return 5;
                case WALKING:
                case RUNNING:
                    return 8;
                case JUMP:
                case FALLING:
                    return 4;
                case ATTACK_1:
                    return 6;
                case GROUND:
                    return 10;
                default:
                    return 1;
            }
        }
    }

}
