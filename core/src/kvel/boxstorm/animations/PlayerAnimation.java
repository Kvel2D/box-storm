package kvel.boxstorm.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import kvel.boxstorm.AnimationManager;

import java.util.HashSet;
import java.util.Map;

//@formatter:off
public class PlayerAnimation extends AnimationManager {

    private static final int WALK_VARY = 2;
    private static final int STAND_VARY = 1;
    private static final int PUSH_VARY = 2;
    private static final int FLY_VARY = 4;
    private static final int DEATH_VARY = 1;

    private Animation[] walkAnimation;
    private Animation[] pushAnimation;
    private Animation[] deathAnimation;

    private TextureRegion[][] walkFrames;
    private TextureRegion[][] standFrames;
    private TextureRegion[][] flyFrames;
    private TextureRegion[][] pushFrames;
    private TextureRegion[][] deathFrames;

    @Override
    public void create() {

        FRAME_COLS = 10;
        FRAME_ROWS = 10;

        TextureRegion[][] tmp = TextureRegion.split(player, player.getWidth() / FRAME_COLS,
                                                            player.getHeight() / FRAME_ROWS);

        OBJECT_HEIGHT    = player.getHeight() / FRAME_ROWS;
        OBJECT_WIDTH     = player.getWidth() / FRAME_COLS;

        walkFrames       = new TextureRegion[WALK_VARY][FRAME_ROWS];
        flyFrames        = new TextureRegion[FLY_VARY][FRAME_ROWS];
        pushFrames       = new TextureRegion[PUSH_VARY][FRAME_ROWS];
        standFrames      = new TextureRegion[STAND_VARY][FRAME_ROWS];
        deathFrames      = new TextureRegion[DEATH_VARY][FRAME_ROWS];

        standFrames[0]   = java.util.Arrays.copyOfRange(tmp[0], 0, 1);
        pushFrames[0]    = java.util.Arrays.copyOfRange(tmp[1], 0, 4);
        pushFrames[1]    = java.util.Arrays.copyOfRange(tmp[1], 4, 8);
        flyFrames[0]     = java.util.Arrays.copyOfRange(tmp[2], 0, 1);
        flyFrames[1]     = java.util.Arrays.copyOfRange(tmp[2], 1, 2);
        flyFrames[2]     = java.util.Arrays.copyOfRange(tmp[2], 2, 3);
        flyFrames[3]     = java.util.Arrays.copyOfRange(tmp[2], 3, 4);
        walkFrames[0]    = java.util.Arrays.copyOfRange(tmp[3], 0, 4);
        walkFrames[1]    = java.util.Arrays.copyOfRange(tmp[3], 4, 8);
        deathFrames[0]   = java.util.Arrays.copyOfRange(tmp[4], 0, 5);

        walkAnimation    = new Animation[WALK_VARY];
        pushAnimation    = new Animation[PUSH_VARY];
        deathAnimation   = new Animation[DEATH_VARY];

        walkAnimation[0] = new Animation(0.075f, walkFrames[0]);
        walkAnimation[1] = new Animation(0.075f, walkFrames[1]);
        pushAnimation[0] = new Animation(0.075f, pushFrames[0]);
        pushAnimation[1] = new Animation(0.075f, pushFrames[1]);
        deathAnimation[0]= new Animation(0.2f, deathFrames[0]);

        args = new HashSet<String>();
        args.add("grounded");
        currentFrame = walkFrames[0][0];
        stateTime = 0f;
    }

    @Override
    public void conditionsApply() {
        stateTime += Gdx.graphics.getDeltaTime();

        if (args.contains("grounded")) {
            if (args.contains("dead")) {
                                                      currentFrame = deathAnimation[0].getKeyFrame(stateTime, false);
                return;
            }
            if (args.contains("pushing")) {
                if (args.contains("negativeX"))       currentFrame = pushAnimation[0].getKeyFrame(stateTime, true);
                else if (args.contains("positiveX"))  currentFrame = pushAnimation[1].getKeyFrame(stateTime, true);
                else                                  currentFrame = standFrames[0][0];
            } else {
                if (args.contains("negativeX"))       currentFrame = walkAnimation[0].getKeyFrame(stateTime, true);
                else if (args.contains("positiveX"))  currentFrame = walkAnimation[1].getKeyFrame(stateTime, true);
                else                                  currentFrame = standFrames[0][0];
            }
        } else {
            if (args.contains("negativeX"))           currentFrame = flyFrames[2][0];
            else if (args.contains("positiveX"))      currentFrame = flyFrames[3][0];
            else if (args.contains("positiveY"))      currentFrame = flyFrames[0][0];
            else                                      currentFrame = flyFrames[1][0];
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
        if (AnimationArgs.get("grounded"))  args.add("grounded");
        else                                args.remove("grounded");
        if (AnimationArgs.get("dead"))      args.add("dead");
        else                                args.remove("dead");
        if (AnimationArgs.get("pushing"))   args.add("pushing");
        else                                args.remove("pushing");
        if (AnimationArgs.get("negativeX")) args.add("negativeX");
        else                                args.remove("negativeX");
        if (AnimationArgs.get("positiveX")) args.add("positiveX");
        else                                args.remove("positiveX");
        if (AnimationArgs.get("positiveY")) args.add("positiveY");
        else                                args.remove("positiveY");
    }
}
//@formatter:on