package kvel.boxstorm.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import kvel.boxstorm.AnimationManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;

//@formatter:off
public class LaserAnimation extends AnimationManager {
 
    private static final int H_VARY = 1;
    private static final int W_VARY = 1;
 
    private Animation[] hAnimation;
    private Animation[] wAnimation;
 
    private TextureRegion[][] hFrames;
    private TextureRegion[][] wFrames;
 
    @Override
    public void create() {
 
        FRAME_COLS = 8;
        FRAME_ROWS = 2;
 
        TextureRegion[][] tmp = TextureRegion.split(laser, laser.getWidth() / FRAME_COLS,
                                                           laser.getHeight() / FRAME_ROWS);
 
        OBJECT_HEIGHT = laser.getHeight() / FRAME_ROWS;
        OBJECT_WIDTH = laser.getWidth() / FRAME_COLS;
 
        hFrames = new TextureRegion[H_VARY][FRAME_ROWS];
        wFrames = new TextureRegion[W_VARY][FRAME_ROWS];
 
        hFrames[0] = java.util.Arrays.copyOfRange(tmp[0], 0, 8);
        wFrames[0] = java.util.Arrays.copyOfRange(tmp[1], 0, 8);
 
        hAnimation = new Animation[H_VARY];
        wAnimation = new Animation[W_VARY];
 
        hAnimation[0] = new Animation(0.5f, hFrames[0]);
        wAnimation[0] = new Animation(0.5f, wFrames[0]);
 
        args = new HashSet<String>();
    }
 
    @Override
    public void conditionsApply() {
        Random state = new Random();
 
        if (args.contains("horizontal")) {
            currentFrame = hAnimation[0].getKeyFrame(state.nextFloat()*4f, true);
        } else {
            currentFrame = wAnimation[0].getKeyFrame(state.nextFloat()*4f, true);
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
        if (AnimationArgs.get("horizontal")) args.add("horizontal");
        else                                 args.remove("horizontal");
    }
}
//@formatter:on