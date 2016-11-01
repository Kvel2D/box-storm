package kvel.boxstorm.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kvel.boxstorm.BoxStorm;
import kvel.boxstorm.Constants;

public class OptionsScreen implements Screen {

    // This is Select object
    // It stores the location of current selection on the menu
    // and the appropriate button number.
    class Select {

        public int currentButtonX;
        public int currentButtonY;

        public Select() {
            currentButtonX = 1;
            currentButtonY = 1;
        }

        // Update method makes sure that select object can only occupy coordinates
        // of menu buttons and that the currentButton number is updated constantly.
        public void update() {
            if (currentButtonX <= 0)   currentButtonX = 1;
            if (currentButtonX > 3)    currentButtonX = 3;
            if (currentButtonY <= 0)   currentButtonY = 1;
            if (currentButtonY > 3)    currentButtonY = 3;
        }

    }

    private Texture opt11;
    private Texture opt12;
    private Texture opt13;
    private Texture opt21;
    private Texture opt22;
    private Texture opt23;
    private Texture opt31;

    private Game game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    public Select select;
    private boolean selecting;
    private int choice;

    public OptionsScreen(Game game) {
        this.game = game;
        batch = new SpriteBatch();

        opt11 = new Texture(Gdx.files.internal("opt11.png"));
        opt12 = new Texture(Gdx.files.internal("opt12.png"));
        opt13 = new Texture(Gdx.files.internal("opt13.png"));
        opt21 = new Texture(Gdx.files.internal("opt21.png"));
        opt22 = new Texture(Gdx.files.internal("opt22.png"));
        opt23 = new Texture(Gdx.files.internal("opt23.png"));
        opt31 = new Texture(Gdx.files.internal("opt31.png"));


        select = new Select();
        selecting = false;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.SCREEN_HORIZONTAL_RATIO,
        Constants.SCREEN_VERTICAL_RATIO);
    }

    @Override
    public void render(float delta) {
        if (delta == 0) return;

        camera.update();
        getInput();

        Gdx.gl.glClearColor(0.78125f, 0.78125f, 0.78125f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        choice = select.currentButtonY*10 + select.currentButtonX;
        batch.begin();
        switch (choice) {
            case 11 : batch.draw(opt11, 0, 0); break;
            case 12 : batch.draw(opt12, 0, 0); break;
            case 13 : batch.draw(opt13, 0, 0); break;
            case 21 : batch.draw(opt21, 0, 0); break;
            case 22 : batch.draw(opt22, 0, 0); break;
            case 23 : batch.draw(opt23, 0, 0); break;
            case 31 : batch.draw(opt31, 0, 0); break;
        }
        batch.end();
    }

    private void getInput() {
        // Determine where in the menu we are and respond to selection based on that
        // SELECT MOVEMENT
        if (!selecting) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                select.currentButtonY++;
                select.update();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                select.currentButtonY--;
                select.update();
            }
            // Selecting
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                // BACK button
                if (select.currentButtonY == 3) {
                    game.setScreen(BoxStorm.menuScreen);
                    select.currentButtonX = 1;
                    select.currentButtonY = 1;
                } else {
                    select.currentButtonX = 2;
                    selecting = true;
                    return;
                }
            }
        }
        if (selecting) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                select.currentButtonX++;
                select.update();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && select.currentButtonX == 3) {
                select.currentButtonX--;
                select.update();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (select.currentButtonY == 1) {
                    if (select.currentButtonX == 2)
                        Gdx.graphics.setDisplayMode(1280, 720,true);
                    if (select.currentButtonX == 3)
                        Gdx.graphics.setDisplayMode(1280, 720,false);
                }
                if (select.currentButtonY == 2) {
                    if (select.currentButtonX == 2)
                        BoxStorm.setVolume(1f);
                    if (select.currentButtonX == 3)
                        BoxStorm.setVolume(0f);
                }
                selecting = false;
                select.currentButtonX = 1;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}

// System.out.println(select.currentLevelX + " x");
// System.out.println(select.currentLevelY + " y");