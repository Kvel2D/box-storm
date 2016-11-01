package kvel.boxstorm.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import kvel.boxstorm.AnimationManager;

import java.util.HashSet;
import java.util.Map;

//@formatter:off
public class BoxAnimation extends AnimationManager {
 
    private static final int VARY = 1;
 
    private Animation[] boxAnimation;
 
    private TextureRegion[][] boxFrames;
 
    @Override
    public void create() {
 
        FRAME_COLS = 1;
        FRAME_ROWS = 1;
 
        TextureRegion[][] tmp = TextureRegion.split(box, box.getWidth() / FRAME_COLS,
                                                         box.getHeight() / FRAME_ROWS);
 
        OBJECT_HEIGHT   = box.getHeight() / FRAME_ROWS;
        OBJECT_WIDTH    = box.getWidth() / FRAME_COLS;
 
        boxFrames       = new TextureRegion[VARY][FRAME_ROWS];
        boxFrames[0]    = java.util.Arrays.copyOfRange(tmp[0], 0, 1);
        boxAnimation    = new Animation[VARY];
        boxAnimation[0] = new Animation(1f, boxFrames[0]);
 
        args = new HashSet<String>();
        stateTime = 0f;
    }
 
    @Override
    public void conditionsApply() {
        stateTime += Gdx.graphics.getDeltaTime();
 
        currentFrame = boxAnimation[0].getKeyFrame(stateTime, true);
    }
 
    @Override
    public TextureRegion getTexture() {
        return currentFrame;
    }
 
    @Override
    public int getObjectHeight() {
        return OBJECT_HEIGHT;
    }
 
    @Override
    public int getObjectWidth() {
        return OBJECT_WIDTH;
    }
 
    @Override
    public void newConditions(Map<String, Boolean> AnimationArgs) {
    }
}
//@formatter:on