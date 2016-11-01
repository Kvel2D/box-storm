package kvel.boxstorm.GameObjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import javafx.util.Pair;
import kvel.boxstorm.BoxStorm;
import kvel.boxstorm.Constants;
import kvel.boxstorm.animations.PlayerAnimation;
import kvel.boxstorm.screens.GameScreen;

import java.util.HashMap;
import java.util.Map;

//@formatter:off
public class Player {

    public float WIDTH;
    public float HEIGHT;

    public PlayerAnimation playerAnimation;
    public TextureRegion playerTexture;

    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();
    public Vector2 previousVelocity = new Vector2();

    public Vector2 defaultPosition = new Vector2();

    public boolean pushing = false;
    public boolean dead = false;
    public boolean noJump = false;
    public boolean grounded = false;
    public boolean spacePressed = false;
    // left: -1; right = 1
    public int direction = 0;
    private Map<String, Boolean> AnimationArgs = new HashMap<String, Boolean>();

    public Player(Vector2 pos) {
        playerAnimation = new PlayerAnimation();
        playerAnimation.create();

        WIDTH  = 1 / (float) Constants.TILE_SIZE * (playerAnimation.getObjectWidth());
        HEIGHT = 1 / (float) Constants.TILE_SIZE * (playerAnimation.getObjectHeight());

        position.set(pos);
        defaultPosition.set(pos);
    }

    public void render(Batch batch) {
        dead = (GameScreen.state == GameScreen.GameStates.FAIL);
        playerAnimation.conditionsApply();
        playerTexture = playerAnimation.getTexture();

        AnimationArgs.clear();
        AnimationArgs.put("grounded", grounded);
        AnimationArgs.put("pushing", pushing);
        AnimationArgs.put("dead", dead);
        AnimationArgs.put("positiveX", velocity.x > 0);
        AnimationArgs.put("negativeX", velocity.x < 0);
        AnimationArgs.put("positiveY", velocity.y > 0);
        AnimationArgs.put("negativeY", velocity.y < 0);
        playerAnimation.newConditions(AnimationArgs);

        batch.begin();
        batch.draw(playerTexture, position.x, position.y, WIDTH, HEIGHT);
        batch.end();
    }

    public void update(float deltaTime) {
        // sync with deltaTime
        if (deltaTime == 0) return;

        pushing = false;
        spacePressed = false;
        direction = 0;

        if (GameScreen.state == GameScreen.GameStates.GAME) {
            getInput();
        }

        if (velocity.y < -0.1)
            grounded = false;

        if (GameScreen.state == GameScreen.GameStates.GAME) {
             // gravity
            if (spacePressed)   velocity.add(0, Constants.GRAVITY);
            // fall faster if no space pressed
            else                velocity.add(0, Constants.GRAVITY*3);
        }

        // cap velocity
        velocity.x = Math.min(Constants.MAX_VELOCITY, Math.abs(velocity.x)) *
                     Math.signum(velocity.x);
        // cap fall
        velocity.y = Math.max(Constants.MAX_FALL_PLAYER, velocity.y);

        if (pushing) {
            if (Math.abs(velocity.x) < Constants.MAX_VELOCITY / 2)
                velocity.x = 0;
        } else {
            if (Math.abs(velocity.x) < Constants.FRICTION)
                velocity.x = 0;

        }
    }

    private void getInput() {
        if (GameScreen.state == GameScreen.GameStates.GAME) {
            // JUMPING:
            // can jump only from the ground
            // jumps as soon as SPACE is hit
            if (Gdx.input.isKeyPressed(Keys.E)     && grounded) pushing = true;
            if (Gdx.input.isKeyJustPressed(Keys.E) && grounded) pushing = false;
            if (pushing) {
                if ((Gdx.input.isKeyPressed(Keys.LEFT)       || Gdx.input.isKeyPressed(Keys.A)) &&
                   !(Gdx.input.isKeyJustPressed(Keys.RIGHT)  || Gdx.input.isKeyJustPressed(Keys.D))) {
                        direction = -1;
                        velocity.x = -Constants.MAX_VELOCITY / 2f;
                }
                if ((Gdx.input.isKeyPressed(Keys.RIGHT)      || Gdx.input.isKeyPressed(Keys.D)) &&
                   !(Gdx.input.isKeyJustPressed(Keys.LEFT)   || Gdx.input.isKeyJustPressed(Keys.A))) {
                        direction = 1;
                        velocity.x = Constants.MAX_VELOCITY / 2f;
                }
            }
            if (!pushing) {
                if (Gdx.input.isKeyPressed(Keys.SPACE) && !grounded) {
                    noJump = true;
                    spacePressed = true;
                }
                if (Gdx.input.isKeyPressed(Keys.SPACE) && grounded && !noJump) {
                    velocity.y += Constants.JUMP_VELOCITY;
                    grounded = false;
                    noJump = true;
                    spacePressed = true;
                    GameScreen.jumpSound.play(BoxStorm.volume);
                }
                if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                    noJump = false;
                    spacePressed = false;
                }

                // MOVEMENT
                // moves as long as the buttons are pressed
                if (Gdx.input.isKeyPressed(Keys.LEFT)  || Gdx.input.isKeyPressed(Keys.A)) {
                    velocity.x = previousVelocity.x - Constants.ACCELERATION;
                    previousVelocity.x = velocity.x;
                    if (velocity.x > 0) velocity.x = 0;
                    direction = -1;
                }
                if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
                    velocity.x = previousVelocity.x + Constants.ACCELERATION;
                    previousVelocity.x = velocity.x;
                    if (velocity.x < 0) velocity.x = 0;
                    direction = 1;
                }

                if (velocity.x == 0)
                    previousVelocity.x = 0;

                // Centers the player on the closest whole x coordinate
                // used for getting into holes
                if ((Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) &&
                        Math.abs(position.x - Math.round(position.x)) < 1) {
                    if      (position.x - Math.round(position.x) > 0.5)       position.x = Math.round(position.x) + 1;
                    else if (position.x - Math.round(position.x) < 0.5)       position.x = Math.round(position.x);
                }

                if (Gdx.input.isKeyPressed(Keys.F)) {
                    System.out.println(Gdx.graphics.getFramesPerSecond());
                }
            }
        }

        AnimationArgs.clear();
        AnimationArgs.put("grounded", grounded);
        AnimationArgs.put("pushing", pushing);
        AnimationArgs.put("dead", dead);
        AnimationArgs.put("positiveX", velocity.x > 0);
        AnimationArgs.put("negativeX", velocity.x < 0);
        AnimationArgs.put("positiveY", velocity.y > 0);
        AnimationArgs.put("negativeY", velocity.y < 0);
        playerAnimation.newConditions(AnimationArgs);
        playerAnimation.conditionsApply();
        playerTexture = playerAnimation.getTexture();
    }
}
//@formatter:on