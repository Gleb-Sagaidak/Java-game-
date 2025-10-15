

import static org.junit.jupiter.api.Assertions.*;

import cz.cvut.game.terroria.Game;
import cz.cvut.game.terroria.entities.Enemy;
import cz.cvut.game.terroria.entities.Mushroom;
import cz.cvut.game.terroria.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;

class MushroomTest {

    private Mushroom m;

    @BeforeEach
    void setUp() {
        // spawn at (100, 200)
        m = new Mushroom(100f, 200f);
    }

    @Test
    void flipDefaultsToLeft() {
        // walkDir is LEFT on construction
        assertEquals(0, m.flipX(),  "flipX() should be 0 when facing LEFT");
        assertEquals(1, m.flipW(),  "flipW() should be +1 when facing LEFT");
    }

    @Test
    void flipWhenFacingRight() throws Exception {
        // force protected field walkDir = RIGHT
        Field walkDirField = Enemy.class.getDeclaredField("walkDir");
        walkDirField.setAccessible(true);
        walkDirField.set(m, Constants.Directions.RIGHT);

        assertEquals(m.getWidth(), m.flipX(),
                "flipX() should be width when facing RIGHT");
        assertEquals(-1, m.flipW(),
                "flipW() should be -1 when facing RIGHT");
    }

    @Test
    void initAttackBoxAtConstructorPosition() throws Exception {
        // access private attackBox field
        Field boxField = Mushroom.class.getDeclaredField("attackBox");
        boxField.setAccessible(true);
        Rectangle2D.Float box = (Rectangle2D.Float) boxField.get(m);

        // on init, box.x == spawn x, box.y == spawn y
        assertEquals(100f, box.x, 1e-6f);
        assertEquals(200f, box.y, 1e-6f);
    }

    @Test
    void updateAttackBoxAppliesOffsets() throws Exception {
        // move the mushroom's hitbox by +20,+30
        Rectangle2D.Float hb = m.getHitbox();
        hb.x += 20;
        hb.y += 30;

        // call updateAttackBox via reflection
        m.updateAttackBox();

        Field boxField = Mushroom.class.getDeclaredField("attackBox");
        boxField.setAccessible(true);
        Rectangle2D.Float box = (Rectangle2D.Float) boxField.get(m);

        // read the private offsets
        Field xOffFld = Mushroom.class.getDeclaredField("attackBoxXOffset");
        Field yOffFld = Mushroom.class.getDeclaredField("attackBoxYOffset");
        xOffFld.setAccessible(true);
        yOffFld.setAccessible(true);
        int xOff = xOffFld.getInt(m);
        int yOff = yOffFld.getInt(m);

        // expected: box.x == hb.x - xOff
        assertEquals(hb.x - xOff, box.x, 1e-6f);
        // expected: box.y == hb.y - yOff + 10
        assertEquals(hb.y - yOff + 10, box.y, 1e-6f);
    }
}