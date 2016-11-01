package kvel.boxstorm.GameObjects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import javafx.util.Pair;
import kvel.boxstorm.AnimationManager;
import kvel.boxstorm.Constants;
import kvel.boxstorm.animations.LaserAnimation;
import kvel.boxstorm.animations.LaserboxAnimation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// Lasers have position which is four numbers
// First to numbers correspond to bounds to which
// the laser is restricted by. The third and
// fourth are coordinates of the emitter, which
// is not drawn, but placed in the TileMap
//@formatter:off
public class Laser {
    public int rangeLeft;
    public int rangeRight;
    public int x;
    public int y;

    private int defaultRangeLeft;
    private int defaultRangeRight;

    public boolean horizontal;
    public int id;
    public float L_WIDTH;
    public float L_HEIGHT;
    public float LB_WIDTH;
    public float LB_HEIGHT;

    private LaserAnimation laserAnimation;
    private LaserboxAnimation laserboxAnimation;
    private TextureRegion laserTexture;
    private TextureRegion laserboxTexture;
    private Map<String, Boolean> AnimationArgs = new HashMap<String, Boolean>();

    public boolean laserOn = true;

    public Laser(int range1, int range2, int x, int y, String direction, int id) {
        laserAnimation = new LaserAnimation();
        laserboxAnimation = new LaserboxAnimation();
        laserAnimation.create();
        laserboxAnimation.create();

        // Set id for button-laser interaction
        this.id = id;
        this.x = x;
        this.y = y;

        // Check direction from name in tiled
        horizontal = !(direction == null);

        L_WIDTH     = 1 / (float) Constants.TILE_SIZE * laserAnimation.getObjectWidth();
        L_HEIGHT    = 1 / (float) Constants.TILE_SIZE * laserAnimation.getObjectHeight();
        LB_WIDTH    = 1 / (float) Constants.TILE_SIZE * laserboxAnimation.getObjectWidth();
        LB_HEIGHT   = 1 / (float) Constants.TILE_SIZE * laserboxAnimation.getObjectHeight();

        // Set position variables in the position array
        rangeLeft   = defaultRangeLeft  = range1;
        rangeRight  = defaultRangeRight = range2;
    }

    // Update range of the laser
    public void updateRange(int r1, int r2) {
        rangeLeft   = r1;
        rangeRight  = r2;
    }

    public void reset() {
        rangeLeft   = defaultRangeLeft;
        rangeRight  = defaultRangeRight;
    }

    public void renderLaser(Batch batch) {
        AnimationArgs.clear();
        AnimationArgs.put("horizontal", horizontal);
        laserAnimation.newConditions(AnimationArgs);
        laserAnimation.conditionsApply();
        laserTexture    = laserAnimation.getTexture();

        batch.begin();
        for (int i = rangeLeft; i <= rangeRight; i++) {
            laserAnimation.conditionsApply();
            laserTexture = laserAnimation.getTexture();
            if (x != i && horizontal) {
                batch.draw(laserTexture, i, y, L_WIDTH, L_HEIGHT);
            }
            if (y != i && !horizontal) {
                batch.draw(laserTexture, x, i, L_WIDTH, L_HEIGHT);
            }
        }
        batch.end();
    }

    public void renderLaserbox(Batch batch) {
        AnimationArgs.put("laserboxOn", laserOn);
        AnimationArgs.put("horizontal", horizontal);
        laserboxAnimation.newConditions(AnimationArgs);
        laserboxAnimation.conditionsApply();
        laserboxTexture = laserboxAnimation.getTexture();

        batch.begin();
        batch.draw(laserboxTexture, x, y, LB_WIDTH, LB_HEIGHT);
        batch.end();
    }

    public void update(float delta) {
        if (delta == 0) return;
        // Resetting this variable is important, because it won't reset itself
        laserOn = true;
    }
}
//@formatter:on