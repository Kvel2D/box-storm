package kvel.boxstorm.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import kvel.boxstorm.BoxStorm;
import kvel.boxstorm.Constants;

public class PauseScreen implements Screen {
    // This should be uploaded in some general class
    Texture allButtons      = new Texture(Gdx.files.internal("pauseMenu_buttons.png"));
    TextureRegion buttons[][];

    // Coordinates of the pause screen
    public float x1, x2;
    public float y1, y2;
    // Buttons variety
    private final int X_VARY = 1;
    private final int Y_VARY = 4;

    // This is Select object
    // It stores the location of current selection on the menu
    // and the appropriate button number.
    class Select {

        // Coordinates of the selected button
        public float sx1, sx2;
        public float sy1, sy2;

        public int currentButton;

        public Select() {
            startingCoordinates();
            currentButton = 1;
        }

        private void startingCoordinates() {
            sx1 = x1;
            sy1 = y2 - buttons[0][0].getRegionHeight() * Constants.MENU_SCALE;
            sx2 = sx1 + buttons[0][0].getRegionWidth() * Constants.MENU_SCALE;
            sy2 = y2;
        }

        // Update method makes sure that select object can only occupy coordinates
        // of menu buttons and that the currentButton number is updated constantly.
        public void update() {
            if (currentButton <= 0)        currentButton = Y_VARY - 1;
            if (currentButton > Y_VARY - 1)    currentButton = 1;
        }

        private void renderButtons(Batch batch) {
            startingCoordinates();

            for (int i = 0; i < Y_VARY; i++) {
                if (i == currentButton - 1)
                    batch.draw(buttons[i * 2 + 1][0], sx1, sy1 - i * interval, sx2 - sx1, sy2 - sy1);
                else
                    batch.draw(buttons[i * 2][0],     sx1, sy1 - i * interval, sx2 - sx1, sy2 - sy1);
            }
        }
    }

    private Texture pauseBackgroundImage;

    private Game game;
    private OrthographicCamera camera;
    private Batch batch;
    private GameScreen currentGame;

    public Select select;

    private float interval;
    private int levelx;
    private int levely;

    public PauseScreen(GameScreen currentGame) {
        this.currentGame = currentGame;
        //this.currentGame.singleRender();
        game = currentGame.game;
        //batch = currentGame.batch;
        levelx = currentGame.levelx;
        levely = currentGame.levely;

        System.out.println("Pause screen activated");
        Color color = batch.getColor();
        batch.setColor(color.r/2, color.g/2, color.b/2, 1);
        buttons = TextureRegion.split(allButtons, allButtons.getWidth() / X_VARY, allButtons.getHeight() / (Y_VARY * 2));

        pauseBackgroundImage = new Texture(Gdx.files.internal("pauseMenu_background.png"));
        x1 = Constants.SCREEN_HORIZONTAL_RATIO * Constants.TILE_SIZE * Constants.SCREEN_SIZE / 2 - pauseBackgroundImage.getWidth() / 2 * Constants.MENU_SCALE;
        y1 = Constants.SCREEN_VERTICAL_RATIO * Constants.TILE_SIZE * Constants.SCREEN_SIZE / 2 - pauseBackgroundImage.getHeight() / 2 * Constants.MENU_SCALE;
        x2 = x1 + pauseBackgroundImage.getWidth() * Constants.MENU_SCALE;
        y2 = y1 + pauseBackgroundImage.getHeight() * Constants.MENU_SCALE;

        // Set the interval between buttons. First - game title.
        // 4 others are buttons.
        interval = pauseBackgroundImage.getHeight() / (Y_VARY - 1) * Constants.MENU_SCALE - buttons[0][0].getRegionHeight() * Constants.MENU_SCALE / (Y_VARY - 1);

        select = new Select();
        select.currentButton = 1;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.SCREEN_HORIZONTAL_RATIO,
                                 Constants.SCREEN_VERTICAL_RATIO);
    }

    @Override
    public void render(float delta) {
        if (delta == 0) return;

        getInput();

        batch.begin();
        batch.draw(pauseBackgroundImage,
                1000,
                1000,
                pauseBackgroundImage.getWidth() * Constants.MENU_SCALE / (Constants.TILE_SIZE * Constants.SCREEN_SIZE),
                pauseBackgroundImage.getHeight() * Constants.MENU_SCALE / (Constants.TILE_SIZE * Constants.SCREEN_SIZE));
        select.renderButtons(batch);
        batch.end();
    }

    private void getInput() {
        // Determine where in the menu we are and respond to selection based on that
        // SELECT MOVEMENT
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            select.currentButton++;
            select.update();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            select.currentButton--;
            select.update();
        }

        // Exit via ESCAPE
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(currentGame);
        }

        // Selecting
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch(select.currentButton) {
                case 1: game.setScreen(currentGame);
                    System.out.println("RESUME");
                    break;
                case 2: game.setScreen(new GameScreen(game, levelx, levely));
                    System.out.println("RESTART");
                    break;
                case 3: game.setScreen(BoxStorm.menuScreen);
                    System.out.println("EXIT");
                    break;
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
