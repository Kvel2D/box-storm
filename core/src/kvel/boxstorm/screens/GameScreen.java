package kvel.boxstorm.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import kvel.boxstorm.*;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import kvel.boxstorm.GameObjects.*;

import com.badlogic.gdx.utils.Timer;

//@formatter:off
public class GameScreen extends ScreenAdapter {

    public enum GameStates {GAME, PAUSE, FAIL, WIN}

    public static GameStates state;

    private GameCamera camera;
    private Physics physics;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private MapProperties objectProperties;
    private MapObjects objects;
    private MapLayer layer;

    private int mapWidth;
    private int mapHeight;

    public Texture pauseWindowTexture;
    public Texture deathWindowTexture;
    public Texture winWindowTexture;

    public static Player player;
    public static WinBox winBox;
    public static Array<Box> Movables   = new Array<Box>();
    public static Array<Laser> Lasers   = new Array<Laser>();
    public static Array<Button> Buttons = new Array<Button>();
    public static Array<Spike> Spikes   = new Array<Spike>();

    public static Timer timer = new Timer();;
    private boolean timerExpired;
    public int levelx;
    public int levely;

    public static Sound jumpSound;
    //public static Sound pushSound;
    public static Sound deathSound;
    public static Sound winSound;
    public static Sound laserOffSound;
    public static Sound laserOnSound;
    public float soundDelay;

    Game game;

    public GameScreen(Game game, int levelx, int levely) {
        this.levelx = levelx;
        this.levely = levely;
        this.game = game;
        state = GameStates.GAME;

        timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                timerExpired = true;
            }
        }, 3, 3); //seconds
        timer.stop();

        map = new TmxMapLoader().load("level" + Integer.toString(levelx) + '-' + Integer.toString(levely) + ".tmx");
        objectProperties = map.getProperties();
        mapWidth  = objectProperties.get("width", Integer.class);
        mapHeight = objectProperties.get("height", Integer.class);

        renderer = new OrthogonalTiledMapRenderer(map, 1 / (float)Constants.TILE_SIZE);

        initPlayer();
        initBoxes();
        initLasers();
        initButtons();
        initSpikes();
        initWinBox();

        physics = new Physics(map);

        camera = new GameCamera(player.position.x, player.position.y, mapWidth, mapHeight);

        pauseWindowTexture = new Texture(Gdx.files.internal("pauseWindow.png"));
        deathWindowTexture = new Texture(Gdx.files.internal("deathWindow.png"));
        winWindowTexture = new Texture(Gdx.files.internal("winWindow.png"));

        // SOUNDS
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.ogg"));
        //pushSound = Gdx.audio.newSound(Gdx.files.internal("push.ogg"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("death.ogg"));
        winSound = Gdx.audio.newSound(Gdx.files.internal("win.ogg"));
        laserOffSound = Gdx.audio.newSound(Gdx.files.internal("laserOff.ogg"));
        laserOnSound = Gdx.audio.newSound(Gdx.files.internal("laserOn.ogg"));
    }

    public void render(float delta) {
        getInput();

        // Get deltaTime - time since the last time render() was called
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (state == GameStates.GAME || state == GameStates.PAUSE) {
            // Clear the screen to erase previous renders
            Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Update all objects
            player.update(deltaTime);
            for (Box box : Movables) {
                box.update(deltaTime);
            }
            for (Laser laser : Lasers) {
                laser.update(deltaTime);
            }
            for (Button button : Buttons) {
                button.update(deltaTime);
            }

            // Update physics of the level
            physics.update(deltaTime);

            // Update camera, this also calls camera's followPlayer() method
            camera.update();

            // Render TileMap
            renderer.setView(camera);
            renderer.render();

            // Render all objects, order of rendering determines the order of display
            Batch batch = renderer.getBatch();
            player.render(batch);

            for (Laser laser : Lasers){
                laser.renderLaserbox(batch);
                if (laser.laserOn)
                    laser.renderLaser(batch);
            }
            for (Box box : Movables) {
                box.render(batch);
            }
            for (Button button : Buttons)
                button.render(batch);

            if (state == GameStates.PAUSE) {
                batch.begin();
                batch.draw(pauseWindowTexture,
                        camera.position.x - 8,
                        camera.position.y - 4.5f,
                        Constants.SCREEN_HORIZONTAL_RATIO,
                        Constants.SCREEN_VERTICAL_RATIO);
                batch.end();
            }
        }

        if (state == GameStates.FAIL) {
            Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            if(!timerExpired){
                player.update(deltaTime);
                physics.update(deltaTime);
            }

            renderer.render();

            Batch batch = renderer.getBatch();
            player.render(batch);

            for (Laser laser : Lasers){
                laser.renderLaserbox(batch);
                if (laser.laserOn)
                    laser.renderLaser(batch);
            }
            for (Box box : Movables)
                box.render(batch);
            for (Button button : Buttons)
                button.render(batch);

            if (timerExpired) {
                batch.begin();
                batch.draw(deathWindowTexture,
                        camera.position.x - 8,
                        camera.position.y - 4.5f,
                        Constants.SCREEN_HORIZONTAL_RATIO,
                        Constants.SCREEN_VERTICAL_RATIO);
                batch.end();
            }
        }

        if (state == GameStates.WIN) {
            Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            camera.update();

            // Render TileMap
            renderer.setView(camera);
            renderer.render();

            Batch batch = renderer.getBatch();

            for (Laser laser : Lasers){
                laser.renderLaserbox(batch);
                if (laser.laserOn)
                    laser.renderLaser(batch);
            }
            for (Box box : Movables)
                box.render(batch);
            for (Button button : Buttons)
                button.render(batch);

            batch.begin();
            batch.draw(winWindowTexture,
                        camera.position.x - 8,
                        camera.position.y - 4.5f,
                        Constants.SCREEN_HORIZONTAL_RATIO,
                        Constants.SCREEN_VERTICAL_RATIO);
            batch.end();
        }

    }

    private void initPlayer() {
        float x, y;
        layer   = map.getLayers().get("player");
        objects = layer.getObjects();
        MapObject object = objects.get("player");
        MapProperties playerProperties = object.getProperties();
        x = Float.parseFloat(playerProperties.get("x").toString());
        y = Float.parseFloat(playerProperties.get("y").toString());
        x /= Constants.TILE_SIZE;
        y /= Constants.TILE_SIZE;
        player = new Player(new Vector2(x, y));
    }

    private void initBoxes() {
        int x, y;
        layer   = map.getLayers().get("movables");
        objects = layer.getObjects();
        for (MapObject object : objects) {
            objectProperties = object.getProperties();
            x = (int)Float.parseFloat(objectProperties.get("x").toString());
            y = (int)Float.parseFloat(objectProperties.get("y").toString());
            x /= Constants.TILE_SIZE;
            y /= Constants.TILE_SIZE;
            Box box = new Box(new Vector2(x, y));
            Movables.add(box);
        }
    }

    private void initLasers() {
        int x, y;
        int range1, range2;
        int id;
        String direction;
        Cell cell;
        layer   = map.getLayers().get("lasers");
        objects = layer.getObjects();
        TiledMapTileLayer walls = (TiledMapTileLayer) map.getLayers().get("walls");


        for (MapObject object : objects) {
            objectProperties = object.getProperties();
            x = (int) Float.parseFloat(objectProperties.get("x").toString());
            y = (int) Float.parseFloat(objectProperties.get("y").toString());
            x /= Constants.TILE_SIZE;
            y /= Constants.TILE_SIZE;

            id = Integer.parseInt(objectProperties.get("type").toString());

            direction = object.getName();
            range1 = range2 = 0;
            if (direction == null) {
                for (int i = 0; i < mapHeight; i++) {
                    cell = walls.getCell(x, i);
                    if (cell != null && i < y)
                        range1 = i;
                    if (cell != null && i > y) {
                        range2 = i;
                        break;
                    }
                }
            } else {
                 for (int i = 0; i < mapWidth; i++) {
                    cell = walls.getCell(i, y);
                    if (cell != null && i < x)
                        range1 = i;
                    if (cell != null && i > x) {
                        range2 = i;
                        break;
                    }
                }
            }
            Laser laser = new Laser(range1 + 1, range2 - 1, x, y, direction, id);
            Lasers.add(laser);
        }
    }

    public void initButtons() {
        int x, y;
        int id;
        MapLayer layer = map.getLayers().get("buttons");
        objects = layer.getObjects();
        for (MapObject object : objects) {
            objectProperties = object.getProperties();
            x = (int) Float.parseFloat(objectProperties.get("x").toString());
            y = (int) Float.parseFloat(objectProperties.get("y").toString());
            x /= Constants.TILE_SIZE;
            y /= Constants.TILE_SIZE;

            id = Integer.parseInt(objectProperties.get("type").toString());

            Button button = new Button(x, y, id);
            Buttons.add(button);
        }
    }

    public void initSpikes() {
        int x, y;
        int id;
        Object temp;
        MapLayer layer = map.getLayers().get("spikes");
        objects = layer.getObjects();
        for (MapObject object : objects) {
            objectProperties = object.getProperties();
            x = (int) Float.parseFloat(objectProperties.get("x").toString());
            y = (int) Float.parseFloat(objectProperties.get("y").toString());
            x /= Constants.TILE_SIZE;
            y /= Constants.TILE_SIZE;

            temp = objectProperties.get("type");
            if (temp == null)
                id = 1;
            else
                id = Integer.parseInt(temp.toString());

            Spike spike = new Spike(x, y, id);
            Spikes.add(spike);
        }
    }

    public void initWinBox() {
        int x, y;
        MapLayer layer = map.getLayers().get("winbox");
        objects = layer.getObjects();
        for (MapObject object : objects) {
            objectProperties = object.getProperties();
            x = (int) Float.parseFloat(objectProperties.get("x").toString());
            y = (int) Float.parseFloat(objectProperties.get("y").toString()) / Constants.TILE_SIZE;
            x /= Constants.TILE_SIZE;

            winBox = new WinBox(x, y);
        }
    }

    public void restartLevel() {
        player.position.set(player.defaultPosition.x, player.defaultPosition.y);
        player.velocity.set(0, 0);

        for (Box box: Movables) {
            box.position.set(box.defaultPosition);
            box.velocity.set(0, 0);
            box.canBePushed = true;
        }
    }

    public void deleteObjects() {
        Movables.clear();
        Spikes.clear();
        Buttons.clear();
        Lasers.clear();
    }

    public void getInput() {
        if (state == GameStates.GAME) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

                state = GameStates.PAUSE;
                player.velocity.x = 0;
                player.velocity.y = 0;
                }
            }
        else if (state == GameStates.FAIL) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

                dispose();
                deleteObjects();
                game.setScreen(BoxStorm.menuScreen);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                timerExpired = false;
                timer.stop();
                state = GameStates.GAME;
                restartLevel();
            }
        }
        else if (state == GameStates.PAUSE) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

                dispose();
                deleteObjects();
                game.setScreen(BoxStorm.menuScreen);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

                state = GameStates.GAME;
                restartLevel();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

                state = GameStates.GAME;
            }
        }
        else if (state == GameStates.WIN) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
                Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

                dispose();
                deleteObjects();
                game.setScreen(BoxStorm.menuScreen);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

                state = GameStates.GAME;
                restartLevel();
            }
        }
    }

    @Override
    public void dispose() {
        map.dispose();
    }
}

//@formatter:on