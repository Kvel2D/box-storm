package kvel.boxstorm.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import kvel.boxstorm.BoxStorm;
import kvel.boxstorm.Constants;

public class LevelselectScreen implements Screen {
    // This should be uploaded in some general class
    Texture allMaps      = new Texture(Gdx.files.internal("levelIcons.png"));
    TextureRegion maps[][];

    // Coordinates of the menu screen
    public float x1, x2;
    public float y1, y2;
    // Maps variety
    private final int X_VARY = 3;
    private final int Y_VARY = 3;

    // This is Select object
    // It stores the location of current selection on the menu
    // and the appropriate button number.
    class Select {

        // Coordinates of the selected button
        public float sx1, sx2;
        public float sy1, sy2;

        public int currentButtonX;
        public int currentButtonY;

        public Select() {
            startingCoordinates();
            currentButtonX = 1;
            currentButtonY = 1;
        }

        private void startingCoordinates() {
            sx1 = x1;
            sy1 = y2 - maps[0][0].getRegionHeight() * Constants.MENU_SCALE;
            sx2 = sx1 + maps[0][0].getRegionWidth() * Constants.MENU_SCALE;
            sy2 = y2;
        }

        // Update method makes sure that select object can only occupy coordinates
        // of menu buttons and that the currentButton number is updated constantly.
        public void update() {
            if (currentButtonX <= 0)        currentButtonX = X_VARY;
            if (currentButtonX > X_VARY)    currentButtonX = 1;
            if (currentButtonY <= 0)        currentButtonY = Y_VARY;
            if (currentButtonY > Y_VARY)    currentButtonY = 1;
        }

        private void renderMaps(SpriteBatch batch) {
            startingCoordinates();

            for (int i = 0; i < Y_VARY; i++) {
                for (int j = 0; j < X_VARY; j++) {
                    if (j == currentButtonX - 1 && i == currentButtonY - 1)
                        batch.draw(maps[i*2 + 1][j], sx1 + j * intervalX, sy1 - i * intervalY, sx2 - sx1, sy2 - sy1);
                    else
                        batch.draw(maps[i*2][j], sx1 + j * intervalX, sy1 - i * intervalY, sx2 - sx1, sy2 - sy1);
                }
            }
        }
    }

    private Texture menuBackgroundImage;

    private Game game;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    public Select select;

    private float intervalX;
    private float intervalY;

    public LevelselectScreen(Game game) {
        this.game = game;
        batch = new SpriteBatch();
        maps = TextureRegion.split(allMaps, allMaps.getWidth() / X_VARY, allMaps.getHeight() / (Y_VARY * 2));

        menuBackgroundImage = new Texture(Gdx.files.internal("mainMenu_background.png"));
        x1 = Constants.SCREEN_HORIZONTAL_RATIO * Constants.TILE_SIZE * Constants.SCREEN_SIZE / 2 - menuBackgroundImage.getWidth() / 2 * Constants.MENU_SCALE;
        y1 = Constants.SCREEN_VERTICAL_RATIO * Constants.TILE_SIZE * Constants.SCREEN_SIZE / 2 - menuBackgroundImage.getHeight() / 2 * Constants.MENU_SCALE;
        x2 = x1 + menuBackgroundImage.getWidth() * Constants.MENU_SCALE;
        y2 = y1 + menuBackgroundImage.getHeight() * Constants.MENU_SCALE;

        // Set the interval between buttons. First - game title.
        // 4 others are buttons.
        intervalX = menuBackgroundImage.getWidth() / (X_VARY - 1) * Constants.MENU_SCALE - maps[0][0].getRegionWidth() * Constants.MENU_SCALE / (X_VARY - 1);
        intervalY = menuBackgroundImage.getHeight() / (Y_VARY - 1) * Constants.MENU_SCALE - maps[0][0].getRegionHeight() * Constants.MENU_SCALE / (Y_VARY - 1);

        select = new Select();
        select.currentButtonX = 1;
        select.currentButtonY = 1;

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
        batch.begin();
        batch.draw(menuBackgroundImage,
                Constants.SCREEN_HORIZONTAL_RATIO * Constants.TILE_SIZE * Constants.SCREEN_SIZE / 2 - menuBackgroundImage.getWidth() / 2 * Constants.MENU_SCALE,
                Constants.SCREEN_VERTICAL_RATIO * Constants.TILE_SIZE * Constants.SCREEN_SIZE / 2 - menuBackgroundImage.getHeight() / 2 * Constants.MENU_SCALE,
                menuBackgroundImage.getWidth() * Constants.MENU_SCALE,
                menuBackgroundImage.getHeight() * Constants.MENU_SCALE);
        select.renderMaps(batch);
        batch.end();
    }

    private void getInput() {
        // Determine where in the menu we are and respond to selection based on that
        // SELECT MOVEMENT
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                select.currentButtonY++;
                select.update();
                System.out.println("Current level: (" + select.currentButtonX + ", " + select.currentButtonY + ")");
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                select.currentButtonY--;
                select.update();
                System.out.println("Current level: (" + select.currentButtonX + ", " + select.currentButtonY + ")");
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                select.currentButtonX++;
                select.update();
                System.out.println("Current level: (" + select.currentButtonX + ", " + select.currentButtonY + ")");
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                select.currentButtonX--;
                select.update();
                System.out.println("Current level: (" + select.currentButtonX + ", " + select.currentButtonY + ")");
            }

            // Exit via ESCAPE
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                select.currentButtonX = 1;
                select.currentButtonY = 1;
                game.setScreen(BoxStorm.menuScreen);
            }

            // Selecting
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                System.out.println("Level selected: (" + select.currentButtonX + ", " + select.currentButtonY + ")");
                game.setScreen(BoxStorm.menuScreen);
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