package kvel.boxstorm.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import kvel.boxstorm.Constants;
import kvel.boxstorm.animations.BoxAnimation;
import kvel.boxstorm.screens.GameScreen;

//@formatter:off
public class Box {

    public float WIDTH;
    public float HEIGHT;
    public BoxAnimation boxAnimation;
    public TextureRegion boxTexture;

    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();

    public Vector2 defaultPosition = new Vector2();

    public boolean canBePushed = true;
    public boolean isPushed = false;

    public Box(Vector2 pos) {
        boxAnimation = new BoxAnimation();
        boxAnimation.create();

        WIDTH  = 1 / (float) Constants.TILE_SIZE * boxAnimation.getObjectWidth();
        HEIGHT = 1 / (float) Constants.TILE_SIZE * boxAnimation.getObjectHeight();
        position.set(pos);
        defaultPosition.set(pos);
    }

    public void render(Batch batch) {
        batch.begin();
        batch.draw(boxTexture, position.x, position.y, WIDTH, HEIGHT);
        batch.end();
    }

    public void update(float delta) {
        // sync with deltaTime
        if (delta == 0) return;
        if (velocity.y != 0) isPushed = false;

        velocity.add(0, Constants.GRAVITY); // gravity

        if (canBePushed) getInput();

        // cap fall
        velocity.y = Math.max(velocity.y, Constants.MAX_FALL_BOX);

        if (!isPushed) velocity.x = 0;
        if (velocity.y < -1) {
            if (position.x - Math.round(position.x) < 0.1)
                position.x = Math.round(position.x);
            if (position.x - Math.round(position.x) > 0.1)
                position.x = Math.round(position.x) + 1;
        }

        boxAnimation.conditionsApply();
        boxTexture = boxAnimation.getTexture();
    }

    public void getInput() {
        // MOVEMENT
        // moves as long as the buttons are pressed
        if (Gdx.input.isKeyPressed(Keys.LEFT)  || Gdx.input.isKeyPressed(Keys.A)) {
            velocity.x = -Constants.MAX_VELOCITY / 3f;
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
            velocity.x =  Constants.MAX_VELOCITY / 3f;
        }
    }
}
//@formatter:on