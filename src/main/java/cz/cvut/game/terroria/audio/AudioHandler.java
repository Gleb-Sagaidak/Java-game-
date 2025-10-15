package cz.cvut.game.terroria.audio;

import cz.cvut.game.terroria.gamestates.GameState;
import cz.cvut.game.terroria.gamestates.Playing;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.Random;


/**
 * AudioHandler manages background music and sound effects within the game.
 * <p>
 * It loads audio clips for menu and in-game songs as well as various sound effects,
 * handles playback control (play, stop, loop), volume adjustment, and muting.
 */
public class AudioHandler {
    Playing playing;
    /** Constant index for menu music. */
    public static int MENU = 0;
    /** Constant index for gameplay music. */
    public static int SONG = 1;

    /** Constant indices for sound effect types. */
    public static int DIE = 0;
    public static int JUMP = 1;
    public static int HUMAN_ATTACK= 2;
    public static int SWORD_ATTACK1 = 3;
    public static int SWORD_ATTACK2 = 4;

    private Clip[] songs, effects;
    private int currentSongId;
    private float volume = 1f;
    private boolean songMute, effectMute;
    private Random rand = new Random();
    /**
     * Constructs an AudioHandler, loads all songs and effects, and starts menu music.
     */
    public AudioHandler() {
        loadSongs();
        loadEffects();
        playSong(MENU);
    }
    /**
     * Updates playback based on the current GameState.
     * <p>
     * Plays menu music in MENU state, or gameplay music in PLAYING state.
     */
    public void setSong() {
        switch (GameState.state) {
            case MENU:
                playSong(MENU);
            case PLAYING:
                playSong(SONG);
        }
    }
    /**
     * Stops currently playing song (if any) and starts the specified one looping continuously.
     *
     * @param song index of the song to play (MENU or SONG)
     */
    public void playSong(int song) {
        stopSong();

        currentSongId = song;
        updateSongVolume();
        songs[currentSongId].setMicrosecondPosition(0);
        songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY);
    }
    /**
     * Loads all sound effect clips from resources and sets their initial volume.
     */
    private void loadEffects() {
        String[] effectNames = { "die", "jump", "human_attack", "sword_attack1", "sword_attack2"};
        effects = new Clip[effectNames.length];
        for (int i = 0; i < effects.length; i++)
            effects[i] = getClip(effectNames[i]);

        updateEffectsVolume();
    }
    /**
     * Adjusts volume for all loaded sound effects based on current volume level.
     */
    private void updateEffectsVolume() {
        for (Clip c : effects) {
            FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }

    /**
     * Retrieves an audio Clip for the given resource name, decoding as PCM if needed.
     *
     * @param name base filename of the .wav resource (without extension)
     * @return initialized Clip ready for playback, or null on error
     */
    private Clip getClip(String name) {
        try {
            URL url = getClass().getResource("/audio/" + name + ".wav");
            AudioInputStream sourceAis = AudioSystem.getAudioInputStream(url);
            AudioFormat sourceFormat = sourceAis.getFormat();

            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sourceFormat.getSampleRate(),
                    16,
                    sourceFormat.getChannels(),
                    sourceFormat.getChannels() * 2,
                    sourceFormat.getSampleRate(),
                    false
            );

            AudioInputStream decodedAis =
                    AudioSystem.getAudioInputStream(decodedFormat, sourceAis);

            Clip clip = AudioSystem.getClip();
            clip.open(decodedAis);
            return clip;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads all background music clips from resources.
     */
    private void loadSongs() {
        String[] names = { "menu", "song" };
        songs = new Clip[names.length];
        for (int i = 0; i < songs.length; i++)
            songs[i] = getClip(names[i]);
    }

    /**
     * Adjusts volume for the currently selected song.
     */
    private void updateSongVolume() {

        FloatControl gainControl = (FloatControl) songs[currentSongId].getControl(FloatControl.Type.MASTER_GAIN);
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        gainControl.setValue(gain);

    }

    /**
     * Toggles muting of all sound effects and plays the jump effect when unmuting.
     */
    public void toggleEffectMute() {
        this.effectMute = !effectMute;
        for (Clip c : effects) {
            BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
            booleanControl.setValue(effectMute);
        }
        if (!effectMute)
            playEffect(JUMP);
    }

    /**
     * Plays the specified sound effect from the beginning.
     *
     * @param effect index of the effect to play
     */
    public void playEffect(int effect) {
        Clip c = effects[effect];
        if (c == null) return;
        if (c.isRunning() || c.isActive()) {
            c.stop();
        }
        c.setFramePosition(0);
        c.setMicrosecondPosition(0);
        try {
            c.flush();
        } catch (IllegalStateException ignored){}
        c.start();
    }

    /**
     * Toggles muting of background music.
     */
    public void toggleSongMute() {
        this.songMute = !songMute;
        for (Clip c : songs) {
            BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
            booleanControl.setValue(songMute);
        }
    }

    /**
     * Stops the currently playing song if active.
     */
    public void stopSong() {
        if (songs[currentSongId].isActive())
            songs[currentSongId].stop();
    }

    /**
     * Sets master volume for both songs and effects.
     *
     * @param volume value between 0.0 (silent) and 1.0 (full volume)
     */
    public void setVolume(float volume) {
        this.volume = volume;
        updateSongVolume();
        updateEffectsVolume();
    }

    /**
     * Plays the humanoid attack sound effect.
     */
    public void playAttackSound() {
        playEffect(HUMAN_ATTACK);
    }

    /**
     * Plays one of two sword attack sound effects at random.
     */
    public void playSwordAttackSound() {
        int start = 3;
        start += rand.nextInt(2);
        playEffect(start);
    }
}
