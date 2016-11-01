package kvel.boxstorm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Map;
import java.util.Set;

//@formatter:off
public abstract class AnimationManager {

    protected int FRAME_COLS;
    protected int FRAME_ROWS;

    protected int OBJECT_HEIGHT;
    protected int OBJECT_WIDTH;

    protected SpriteBatch spriteBatch;
    protected TextureRegion currentFrame;
    protected float stateTime;

    public Texture player       = new Texture(Gdx.files.internal("hero_anim.png"));
    public Texture laser        = new Texture(Gdx.files.internal("laser_anim.png"));
    public Texture box          = new Texture(Gdx.files.internal("box_anim.png"));
    public Texture button       = new Texture(Gdx.files.internal("button_anim.png"));
    public Texture laserbox     = new Texture(Gdx.files.internal("laserbox_anim.png"));
    protected Set<String> args;

    public abstract void conditionsApply();
    public abstract void create();
    public abstract TextureRegion getTexture();
    public abstract int getObjectHeight();
    public abstract int getObjectWidth();
    public abstract void newConditions(Map<String, Boolean> AnimationArgs);
}
//@formatter:on