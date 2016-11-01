package kvel.boxstorm.GameObjects;


import com.badlogic.gdx.graphics.OrthographicCamera;
import kvel.boxstorm.Constants;
import kvel.boxstorm.screens.GameScreen;

public class GameCamera extends OrthographicCamera{
    // Bounds are measured in tiles and determine the area in the center of
    // the screen, in which player movement is ignored by the camera.
    // For example bounds of 4x2 would mean that the player can move freely
    // in an area of 8x4 located in the center of the screen without camera
    // following the player
    private int mapWidth;
    private int mapHeight;

    public GameCamera(float playerX, float playerY, int mapW, int mapH) {
        super(Constants.SCREEN_HORIZONTAL_RATIO, Constants.SCREEN_VERTICAL_RATIO);
        this.setToOrtho(false, this.viewportWidth, this.viewportHeight);
        this.position.x = playerX;
        this.position.y = playerY;
        this.update();
        mapWidth = mapW;
        mapHeight = mapH;
    }

    public void update() {
        followPlayer();
        super.update();
    }

    private void followPlayer() {
        // Camera settings
        // Here we define how much the camera follows the player and the speed of
        // camera's movement. Higher numbers make it less comfortable to watch
        while (this.position.x - GameScreen.player.position.x > Constants.HORIZONTAL_BOUND)  this.position.x -= Constants.CAMERA_SPEED;
        while (this.position.x - GameScreen.player.position.x < -Constants.HORIZONTAL_BOUND) this.position.x += Constants.CAMERA_SPEED;
        while (this.position.y - GameScreen.player.position.y > Constants.VERTICAL_BOUND)    this.position.y -= Constants.CAMERA_SPEED;
        while (this.position.y - GameScreen.player.position.y < -Constants.VERTICAL_BOUND)   this.position.y += Constants.CAMERA_SPEED;
        // Here we make sure that camera doesn't move outside of the bounds of the level
        // Horizontal Bounds
        if(this.position.x < Constants.SCREEN_HORIZONTAL_RATIO/2) this.position.x = Constants.SCREEN_HORIZONTAL_RATIO/2;
        if(this.position.x > mapWidth - Constants.SCREEN_HORIZONTAL_RATIO/2) this.position.x = mapWidth - Constants.SCREEN_HORIZONTAL_RATIO/2;
        // Vertical Bounds
        if(this.position.y < Constants.SCREEN_VERTICAL_RATIO/2 + 0.5f) this.position.y = Constants.SCREEN_VERTICAL_RATIO/2 + 0.5f;
        if(this.position.y > mapHeight - Constants.SCREEN_VERTICAL_RATIO/2 - 0.5f) this.position.y = mapHeight - Constants.SCREEN_VERTICAL_RATIO/2 - 0.5f;
        // All settings are interconnected, so be careful with editing
    }
}
