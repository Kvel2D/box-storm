package kvel.boxstorm.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import kvel.boxstorm.AnimationManager;

import java.util.HashSet;
import java.util.Map;

//@formatter:off
public class ButtonAnimation extends AnimationManager {
 
    private static final int ON_VARY = 4;
    private static final int OFF_VARY = 4;
 
    private Animation[] onAnimation;
    private Animation[] offAnimation;
 
    private TextureRegion[][] onFrames;
    private TextureRegion[][] offFrames;
 
    @Override
    public void create() {
 
        FRAME_COLS = 4;
        FRAME_ROWS = 2;
 
        TextureRegion[][] tmp = TextureRegion.split(button, button.getWidth() / FRAME_COLS,
                                                            button.getHeight() / FRAME_ROWS);
 
        OBJECT_HEIGHT   = button.getHeight() / FRAME_ROWS;
        OBJECT_WIDTH    = button.getWidth() / FRAME_COLS;
 
        onFrames        = new TextureRegion[ON_VARY][FRAME_ROWS];
        offFrames       = new TextureRegion[OFF_VARY][FRAME_ROWS];
        onFrames[0]     = java.util.Arrays.copyOfRange(tmp[1], 0, 4);
        offFrames[0]    = java.util.Arrays.copyOfRange(tmp[0], 0, 4);

        onAnimation     = new Animation[ON_VARY];
        offAnimation    = new Animation[OFF_VARY];
        for (int i = 0; i < ON_VARY; i++) {
            onAnimation[i]  = new Animation(1f, onFrames[i]);
        }
        for (int i = 0; i < OFF_VARY; i++) {
            offAnimation[i]  = new Animation(1f, offFrames[i]);
        }
 
        args = new HashSet<String>();
        stateTime = 0f;
    }
 
    @Override
    public void conditionsApply() {
        stateTime += Gdx.graphics.getDeltaTime();

        if (args.contains("buttonOn")) {
            currentFrame = onAnimation[0].getKeyFrame(0, false);
        }
        else {
            currentFrame = offAnimation[0].getKeyFrame(0, false);
        }
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
        if (AnimationArgs.get("buttonOn")) args.add("buttonOn");
        else                               args.remove("buttonOn");
    }
}
//@formatter:on