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
import kvel.boxstorm.Constants;

//@formatter:off
public class MenuScreen implements Screen {
    // Coordinates of the menu screen
    public float x1, x2;
    public float y1, y2;
    // Buttons variety
    private final int X_VARY = 1;
    private final int Y_VARY = 5;

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
            sx1 = x1 + (x2 - x1) / 2 - buttons[0][0].getRegionWidth() / 2 * Constants.MENU_SCALE ;
            sy1 = (y2 - buttons[0][0].getRegionHeight() * Constants.MENU_SCALE);
            sx2 = sx1 + buttons[0][0].getRegionWidth() * Constants.MENU_SCALE;
            sy2 = sy1 + buttons[0][0].getRegionHeight() * Constants.MENU_SCALE;
        }

        // Update method makes sure that select object can only occupy coordinates
        // of menu buttons and that the currentButton number is updated constantly.
        public void update() {
            if (currentButton <= 0) currentButton = Y_VARY - 1;
            if (currentButton >= Y_VARY) currentButton = 1;
        }

        private void renderButtons(SpriteBatch batch) {
            startingCoordinates();

            for (int i = 0; i < 5; i++) {
                if (i == currentButton)
                    batch.draw(buttons[i*2 + 1][0], sx1, sy1 - i * interval, sx2 - sx1, sy2 - sy1);
                else
                    batch.draw(buttons[i*2][0], sx1, sy1 - i * interval, sx2 - sx1, sy2 - sy1);

            }
        }
    }

    private Texture allButtons;
    private TextureRegion buttons[][];
    private Texture menuBackgroundImage;
    private Texture tipsWindow;

    private Game game;
    private LevelselectScreen level;
    private OptionsScreen options;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Select select;
    private float interval;

    private boolean tipsOn = true;

    public MenuScreen(Game game) {
        menuBackgroundImage = new Texture(Gdx.files.internal("mainMenu_background.png"));
        tipsWindow          = new Texture(Gdx.files.internal("tipsWindow.png"));
        allButtons          = new Texture(Gdx.files.internal("mainMenu_buttons.png"));

        this.game = game;
        level = new LevelselectScreen(game);
        options = new OptionsScreen(game);
        batch = new SpriteBatch();
        buttons = TextureRegion.split(allButtons, allButtons.getWidth(), allButtons.getHeight() / (Y_VARY * 2));

        x1 = Constants.SCREEN_HORIZONTAL_RATIO * Constants.TILE_SIZE * Constants.SCREEN_SIZE / 2 - menuBackgroundImage.getWidth() / 2 * Constants.MENU_SCALE;
        y1 = Constants.SCREEN_VERTICAL_RATIO * Constants.TILE_SIZE * Constants.SCREEN_SIZE / 2 - menuBackgroundImage.getHeight() / 2 * Constants.MENU_SCALE;
        x2 = x1 + menuBackgroundImage.getWidth() * Constants.MENU_SCALE;
        y2 = y1 + menuBackgroundImage.getHeight() * Constants.MENU_SCALE;

        // Set the interval between buttons. First - game title.
        // 4 others are buttons.
        interval = menuBackgroundImage.getHeight() / (Y_VARY - 1) * Constants.MENU_SCALE - buttons[0][0].getRegionHeight() * Constants.MENU_SCALE / (Y_VARY - 1);
        select = new Select();
        select.currentButton = 1;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.SCREEN_HORIZONTAL_RATIO * Constants.SCREEN_SIZE,
                                 Constants.SCREEN_VERTICAL_RATIO * Constants.SCREEN_SIZE);
    }

    @Override
    public void render(float delta) {
        if (delta == 0) return;

        camera.update();
        getInput();

        Gdx.gl.glClearColor(0.78125f, 0.78125f, 0.78125f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (tipsOn) {
            batch.draw(tipsWindow, x1, y1, x2 - x1, y2 - y1);
        }
        else {
            batch.draw(menuBackgroundImage, x1, y1, x2 - x1, y2 - y1);
            select.renderButtons(batch);
        }
        batch.end();
    }

    private void getInput() {
        // Determine where in the menu we are and respond to selection based on that
        // SELECT MOVEMENT
        if (tipsOn) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                tipsOn = false;
            }
        }
        else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                select.currentButton++;
                select.update();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                select.currentButton--;
                select.update();
            }

            // Exit via ESCAPE
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
                Gdx.app.exit();

            // Selecting
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                switch (select.currentButton) {
                    case 4:
                        System.out.println("MainMenu: EXIT_SELECT - " + select.currentButton);
                        Gdx.app.exit();
                        break;
                    case 3:
                        System.out.println("MainMenu: OPTIONS_SELECT - " + select.currentButton);
                        game.setScreen(options);
                        break;
                    case 2:
                        System.out.println("MainMenu: LEVEL_SELECT - " + select.currentButton);
                        System.out.println("Level selected: (" + level.select.currentButtonX + ", " + level.select.currentButtonY + ")");
                        game.setScreen(level);
                        break;
                    case 1:
                        System.out.println("MainMenu: START_SELECT - " + select.currentButton);
                        System.out.println("Level selected MENU: (" + level.select.currentButtonX + ", " + level.select.currentButtonY + ")");
                        game.setScreen(new GameScreen(game, level.select.currentButtonX, level.select.currentButtonY)); // start game(from last level played?)
                        break;
                    default:
                        System.out.println("MainMenu: ERROR_SELECT - " + select.currentButton);
                        break;
                }
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
//@formatter:on