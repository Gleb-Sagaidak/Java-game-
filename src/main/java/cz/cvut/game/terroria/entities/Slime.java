package cz.cvut.game.terroria.entities;

import cz.cvut.game.terroria.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static cz.cvut.game.terroria.utils.Constants.Directions.*;
import static cz.cvut.game.terroria.utils.Constants.EnemyConstants.*;


public class Slime extends Enemy{
    private Rectangle2D.Float attackBox;
    private int attackBoxXOffset;
    private int attackBoxYOffset;
    public Slime(float x, float y) {
        super(x, y, SLIME_WIDTH, SLIME_HEIGHT, SLIME);
        initHitbox(x,y, (int) (24 * Game.SCALE), (int) (13 * Game.SCALE));
        this.enemyType = enemyType;
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x,y,(int) (40 * Game.SCALE), (int) (24 * Game.SCALE));
        attackBoxXOffset = (int) (10 * Game.SCALE);
        attackBoxYOffset = (int) (10 * Game.SCALE);
    }

    public void loadLvlData(int[][] lvlData) {
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
    }

    private void updateBehave(int[][] lvlData, Player player) {
        if (!inAir && !IsEntityOnFloor(hitbox, lvlData)) {
            inAir = true;
        }

        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir){
            updateInAir(lvlData);
            move(lvlData);
        }
        else {
            switch (enemyState) {
                case IDLE:
                    newState(RUNNING);
                    break;
                case RUNNING:
                    if (canSeePlayer(lvlData, player))
                        turnTowardsPlayer(player);
                    if (isPlayerCloseForAttack(player))
                        newState(RUNNING);
                    move(lvlData);
                    break;
                case HIT:
                    break;
            }
        }

    }
    public void update(int[][] lvlData , Player player) {
        updateBehave(lvlData , player);
        updateAnimationTick();
        updateAttackBox();
    }

    private void updateAttackBox() {
        attackBox.x = hitbox.x - attackBoxXOffset;
        attackBox.y = hitbox.y - attackBoxYOffset;
    }

    public int flipX(){
        if (walkDir == RIGHT)
            return width;
        else
            return 0;
    }
    public int flipW(){
        if (walkDir == RIGHT)
            return -1;
        else
            return 1;
    }

    public void drawAttackBox(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setColor(Color.RED);
        g.drawRect((int) attackBox.x - xLvlOffset ,(int) attackBox.y - yLvlOffset,(int) attackBox.width,(int) attackBox.height);
    }


}
