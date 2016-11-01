package kvel.boxstorm;

public class Constants {
    public static final int TILE_SIZE               = 16;
    public static final int SCREEN_HORIZONTAL_RATIO = 16;
    public static final int SCREEN_VERTICAL_RATIO   = 9;
    public static int SCREEN_SIZE                   = 6;
    public static float MENU_SCALE                  = 3f;
    public static final float GRAVITY               = -1f;
    public static final float FRICTION              = 5f;
    public static final float MAX_VELOCITY          = 8f;
    public static final float DAMPING               = 0.87f;
    public static final float MAX_FALL_PLAYER       = -20f;
    public static final float MAX_FALL_BOX          = -30f;
    public static final float JUMP_VELOCITY         = 20f;
    public static final float ACCELERATION          = 1f;

    // Bounds are measured in tiles and determine the area in the center of
    // the screen, in which player movement is ignored by the camera.
    // For example bounds of 4x2 would mean that the player can move freely
    // in an area of 8x4 located in the center of the screen without camera
    // following the player
    public static final float HORIZONTAL_BOUND = 1;
    public static final float VERTICAL_BOUND = 1;

    // Speed value is in tiles/render call, higher = jerkier, lower = smoother
    public static final float CAMERA_SPEED = 0.05f;
}