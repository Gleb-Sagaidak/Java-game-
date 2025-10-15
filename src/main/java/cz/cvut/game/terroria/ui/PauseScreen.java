package cz.cvut.game.terroria.ui;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.audio.AudioHandler;
import cz.cvut.game.terroria.gamestates.GameState;
import cz.cvut.game.terroria.gamestates.Playing;
import cz.cvut.game.terroria.utils.Constants;
import cz.cvut.game.terroria.utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import static cz.cvut.game.terroria.utils.Constants.PauseButtons.*;
import static cz.cvut.game.terroria.utils.Constants.URMButtons.*;
import static cz.cvut.game.terroria.utils.Constants.VolumeButtons.*;

public class PauseScreen {
    private Playing playing;
    private BufferedImage pasueBackgrd;
    private int bgX,bgY,bgWidth,bgHeight;
    private SoundButton musicButton, sfxButton;
    private UrmButton menuB, unpauseB;
    private VolumeButton volumeButton;
    private AudioHandler audioHandler;
    public PauseScreen(Playing playing) {
        this.playing = playing;
        loadBackground();
        createSoundBtns();
        createUrmButtons();
        createVolumeButton();
        audioHandler = playing.getGame().getAudioHandler();
    }

    private void createVolumeButton() {
        int relX = (int)(22  * Game.SCALE);
        int relY = (int)(250 * Game.SCALE);

        int vX = bgX + relX;
        int vY = bgY + relY;
        volumeButton = new VolumeButton(vX, vY, SLIDER_WIDTH, VOLUME_HEIGHT);
    }

    private void createUrmButtons() {
        int rowY  = bgY + bgHeight -  (int)(85 * Game.SCALE);
        int gap   = (int)(12 * Game.SCALE);

        int totalWidth = URM_SIZE * 2 + gap;
        int startX = bgX + (bgWidth - totalWidth) / 2;

        menuB    = new UrmButton(startX, rowY, URM_SIZE, URM_SIZE, 2);
        unpauseB = new UrmButton(startX + URM_SIZE + gap, rowY, URM_SIZE, URM_SIZE, 0);
    }

    private void createSoundBtns() {
        int relX      = (bgWidth - SOUND_SIZE) / 2 + 50;
        int relMusicY = (int)( 115 * Game.SCALE) ;
        int relSfxY   = (int)(160 * Game.SCALE) ;
        musicButton = new SoundButton(bgX + relX,bgY + relMusicY,SOUND_SIZE, SOUND_SIZE);
        sfxButton = new SoundButton(bgX + relX,bgY + relSfxY,SOUND_SIZE, SOUND_SIZE);
    }

    private void loadBackground() {
        pasueBackgrd = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACK);
        bgWidth = (int)(pasueBackgrd.getWidth() * Game.SCALE);
        bgHeight = (int)(pasueBackgrd.getHeight() * Game.SCALE);
        bgX = (Constants.WINDOW_WIDTH - bgWidth) / 2;
        bgY = (Constants.WINDOW_HEIGHT - bgHeight) / 2;
    }

    public void update() {
        musicButton.update();
        sfxButton.update();

        menuB.update();
        unpauseB.update();

        volumeButton.update();
    }
    public void draw(Graphics g) {
        g.drawImage(pasueBackgrd,bgX,bgY,bgWidth,bgHeight,null);
        musicButton.draw(g);
        sfxButton.draw(g);

        menuB.draw(g);
        unpauseB.draw(g);

        volumeButton.draw(g);
    }


    public void mouseMoved(MouseEvent e){
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);
        menuB.setMouseOver(false);
        unpauseB.setMouseOver(false);
        volumeButton.setMouseOver(false);

        if (isIn(e, musicButton))
            musicButton.setMouseOver(true);
        else if (isIn(e, sfxButton))
            sfxButton.setMouseOver(true);
        else if (isIn(e, menuB))
            menuB.setMouseOver(true);
        else if (isIn(e, unpauseB))
            unpauseB.setMouseOver(true);
        else if (isIn(e, volumeButton))
            volumeButton.setMouseOver(true);
    }
    public void mousePressed(MouseEvent e){
        if (isIn(e, musicButton))
            musicButton.setMousePressed(true);
        else if (isIn(e, sfxButton))
            sfxButton.setMousePressed(true);
        else if (isIn(e, menuB))
            menuB.setMousePressed(true);
        else if (isIn(e, unpauseB))
            unpauseB.setMousePressed(true);
        else if (isIn(e, volumeButton))
            volumeButton.setMousePressed(true);
    }
    public void mouseReleased(MouseEvent e){
        if (isIn(e, musicButton)) {
            if (musicButton.isMousePressed()) {
                musicButton.setMuted(!musicButton.isMuted());
                audioHandler.toggleSongMute();
            }
        } else if (isIn(e, sfxButton)) {
            if (sfxButton.isMousePressed()) {
                sfxButton.setMuted(!sfxButton.isMuted());
                audioHandler.toggleEffectMute();
            }

        } else if (isIn(e, menuB)) {
            if (menuB.isMousePressed()) {
                GameState.state = GameState.MENU;
                playing.unpauseGame();
            }
        } else if (isIn(e, unpauseB)) {
            if (unpauseB.isMousePressed())
                playing.unpauseGame();
        }

        musicButton.resetBools();
        sfxButton.resetBools();
        menuB.resetBools();
        unpauseB.resetBools();
        volumeButton.resetBools();
    }
    public void mouseDragged(MouseEvent e){
        if (volumeButton.isMousePressed()) {
            float valueBefore = volumeButton.getFloatValue();
            volumeButton.changeX(e.getX());
            float valueAfter = volumeButton.getFloatValue();
            if(valueBefore != valueAfter)
                audioHandler.setVolume(valueAfter);
        }
    }

    private boolean isIn(MouseEvent e, PauseButton b){
        return b.getBounds().contains(e.getX(), e.getY());
    }
}
