package kvel.boxstorm.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import kvel.boxstorm.AnimationManager;

import java.util.HashSet;
import java.util.Map;

//@formatter:off
public class LaserboxAnimation extends AnimationManager {
 
    private static final int ON_VARY = 2;
    private static final int OFF_VARY = 2;
 
    private Animation[] onAnimation;
    private Animation[] offAnimation;
 
    private TextureRegion[][] onFrames;
    private TextureRegion[][] offFrames;
 
    @Override
    public void create() {
 
        FRAME_COLS = 4;
        FRAME_ROWS = 4;
 
        TextureRegion[][] tmp = TextureRegion.split(laserbox, laserbox.getWidth() / FRAME_COLS,
                                                              laserbox.getHeight() / FRAME_ROWS);
 
        OBJECT_HEIGHT   = laserbox.getHeight() / FRAME_ROWS;
        OBJECT_WIDTH    = laserbox.getWidth() / FRAME_COLS;
 
        onFrames        = new TextureRegion[ON_VARY][FRAME_ROWS];
        offFrames       = new TextureRegion[OFF_VARY][FRAME_ROWS];
        onFrames[0]     = java.util.Arrays.copyOfRange(tmp[0], 0, 4);
        offFrames[0]    = java.util.Arrays.copyOfRange(tmp[1], 0, 4);
        onFrames[1]     = java.util.Arrays.copyOfRange(tmp[2], 0, 4);
        offFrames[1]    = java.util.Arrays.copyOfRange(tmp[3], 0, 4);

        onAnimation     = new Animation[ON_VARY];
        offAnimation    = new Animation[OFF_VARY];
        for (int i = 0; i < ON_VARY; i++) {
            onAnimation[i]  = new Animation(0.05f, onFrames[i]);
        }
        for (int i = 0; i < OFF_VARY; i++) {
            offAnimation[i]  = new Animation(0.05f, offFrames[i]);
        }

        args = new HashSet<String>();
        stateTime = 0f;
    }
 
    @Override
    public void conditionsApply() {
        stateTime += Gdx.graphics.getDeltaTime();

        int position = 0;
        if (args.contains("horizontal")) position = 1;
        if (args.contains("laserboxOn"))
            currentFrame = onAnimation[position].getKeyFrame(stateTime, true);
        else
            currentFrame = offAnimation[position].getKeyFrame(stateTime, true);
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
        if (AnimationArgs.get("horizontal")) args.add("horizontal");
        else                                 args.remove("horizontal");
        if (AnimationArgs.get("laserboxOn")) args.add("laserboxOn");
        else                                 args.remove("laserboxOn");
    }
}
//@formatter:on