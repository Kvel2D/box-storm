package kvel.boxstorm.GameObjects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import kvel.boxstorm.BoxStorm;
import kvel.boxstorm.screens.GameScreen;
import kvel.boxstorm.Constants;


// Physics has:
// Collision methods: checkPlayerCollisions, checkBoxCollisions, checkLaserCollisions
// Getter methods: getTiles, getOtherBoxes, getAllBoxes
// Collision methods check for collisions between objects and change their properties
// accordingly. Getter methods are used in collision methods to get Rectangles of objects
//@formatter:off
public class Physics {
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    // Rectangles for collision detection purposes
    private Array<Rectangle> tiles = new Array<Rectangle>();
    private Array<Rectangle> boxes = new Array<Rectangle>();
    private TiledMap map;

    public Physics(TiledMap ma) {
        map = ma;
    }

    public void update(float deltaTime) {
        // Multiply velocity by deltaTime, so that it's in sync with the frame
        GameScreen.player.velocity.scl(deltaTime);
        for (Box box : GameScreen.Movables)
            box.velocity.scl(deltaTime);

        // Do collision detection for all objects
        checkPlayerCollisions();
        for (Box box : GameScreen.Movables)
            checkBoxCollisions(box);
        // Reset laser to default before processing new range
        for (Laser laser : GameScreen.Lasers) {
            laser.reset();
        }
        for (Laser laser : GameScreen.Lasers)
            checkLaserCollisions(laser);
        for (Button button : GameScreen.Buttons)
            checkButtonCollisions(button);

        // Set latest position for player and boxes
        GameScreen.player.position.add(GameScreen.player.velocity);
        for (Box box : GameScreen.Movables)
            box.position.add(box.velocity);

        // Unscale velocity
        GameScreen.player.velocity.scl(1 / deltaTime);
        for (Box box : GameScreen.Movables)
            box.velocity.scl(1 / deltaTime);

        // Apply damping to movement, so that objects don't keep moving forever
        GameScreen.player.velocity.x *= Constants.DAMPING;
        for (Box box : GameScreen.Movables)
            box.velocity.x *= Constants.DAMPING;
    }


    // --------------------------------------------------------------------------------------------
    //                                   COLLISION METHODS
    // --------------------------------------------------------------------------------------------
    private void checkPlayerCollisions() {
        int startX, startY, endX, endY;
        float sX, sY, eX, eY;
        int rangeLeft, rangeRight;
        int x, y;
        // Get rectangles from the pool and set it's position and dimensions to match player's
        Rectangle playerRect = rectPool.obtain();
        playerRect.set(GameScreen.player.position.x, GameScreen.player.position.y, GameScreen.player.WIDTH,
        GameScreen.player.HEIGHT - 0.15f);


        // --------------------------------------------------------------------
        // HORIZONTAL AXIS COLLISION
        // --------------------------------------------------------------------
        // First we determine the direction of player's movement
        // Then we set the search area for nearby obstacles(boxes)
        // to match the movement, i.e. if player is moving right we search
        // an area of 1x2 to the right of the GameScreen.player.
        if (GameScreen.player.velocity.x > 0) {
            // right side of the sprite
            startX = endX = (int) (GameScreen.player.position.x + GameScreen.player.WIDTH + GameScreen.player.velocity.x);
            sX = (GameScreen.player.position.x + GameScreen.player.WIDTH + GameScreen.player.velocity.x) - 1;
            eX = sX + 2;
        } else {
            // left side of the sprite
            startX = endX = (int) (GameScreen.player.position.x + GameScreen.player.velocity.x);
            sX = (GameScreen.player.position.x + GameScreen.player.velocity.x) - 1;
            eX = sX + 2;
        }
        startY = (int) (GameScreen.player.position.y);
        endY = (int) (GameScreen.player.position.y + GameScreen.player.HEIGHT);
        sY = (GameScreen.player.position.y) - 1;
        eY = (GameScreen.player.position.y + GameScreen.player.HEIGHT) + 1;
        // Update player Rectangle by adding his speed this predicts player's position
        // in the next frame so we can resolve collisions before they happen on screen
        playerRect.x += GameScreen.player.velocity.x;

        // Use getter method for tiles in the search area, then check if any tile rectangle
        // overlaps with the player rectangle. If so, stop the player, by setting velocity to 0.
        getTiles(startX, startY, endX, endY, tiles);
        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                GameScreen.player.velocity.x = 0;
                // If player is close enough and moving towards the tile
                // move him right next to the tile. Without this player
                // is unable to come right next to the tile
                if (Math.abs(GameScreen.player.position.y - tile.y) < 0.5)
                    GameScreen.player.position.x = tile.x
                    + Math.signum(GameScreen.player.position.x - tile.x);
                break;
            }
        }

        // Same for boxes, but with floats, since box positions unlike boxes are in floats
        getAllBoxes(sX, sY, eX, eY, boxes);
        for (Rectangle box : boxes) {
            if (playerRect.overlaps(box)) {
                // GameScreen.player.pushing = false;
                GameScreen.player.velocity.x = 0;
                // same as above but for boxes
                if (Math.abs(GameScreen.player.position.y - box.y) < 0.5)
                    GameScreen.player.position.x = box.x
                    + Math.signum(GameScreen.player.position.x - box.x);
                break;
            }
        }
        // Update player position based on resolved collision, so that vertical axis
        // collision is done correctly
        playerRect.x = GameScreen.player.position.x;


        // --------------------------------------------------------------------
        // VERTICAL AXIS COLLISION
        // --------------------------------------------------------------------
        if (GameScreen.player.velocity.y > 0) {
            startY = endY = (int) (GameScreen.player.position.y + GameScreen.player.HEIGHT + GameScreen.player.velocity.y);
            sY = (GameScreen.player.position.y + GameScreen.player.velocity.y);
            eY = (GameScreen.player.position.y + GameScreen.player.HEIGHT + GameScreen.player.velocity.y);
        } else {
            startY = endY = (int) (GameScreen.player.position.y + GameScreen.player.velocity.y);
            sY = (GameScreen.player.position.y + GameScreen.player.velocity.y) - 1;
            eY = sY + 1;
        }
        startX = (int) (GameScreen.player.position.x);
        endX = (int) (GameScreen.player.position.x + GameScreen.player.WIDTH);
        sX = (GameScreen.player.position.x) - 1;
        eX = sX + 2;
        playerRect.y += GameScreen.player.velocity.y;

        getAllBoxes(sX, sY, eX, eY, boxes);
        for (Rectangle box : boxes) {
            if (playerRect.overlaps(box)) {
                if (GameScreen.player.velocity.y > 0) {
                    GameScreen.player.position.y = box.y - GameScreen.player.HEIGHT;
                } else {
                    GameScreen.player.position.y = box.y + box.height;
                    GameScreen.player.grounded = true;
                }
                GameScreen.player.velocity.y = 0;
                break;
            }
        }

        getTiles(startX, startY, endX, endY, tiles);
        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                // Reset player position to above/below it collided with, prevents spazz
                if (GameScreen.player.velocity.y > 0) {
                    GameScreen.player.position.y = tile.y - GameScreen.player.HEIGHT;
                } else {
                    GameScreen.player.position.y = tile.y + tile.height;
                    // If we hit the ground, mark us as grounded so we can jump
                    GameScreen.player.grounded = true;
                }
                GameScreen.player.velocity.y = 0;
                break;
            }
        }


        // This loop checks all boxes for their "pushability"
        // To be considered being pushed, a box has to be next to the player
        // the direction of player's push has to match the side of
        // the player on which the box is located(so that we're pushing, not pulling)
        // and of course the player has to be pushing.Any other boxes are set
        // to not being pushed, this takes care of boxes that player have stopped pushing.
        for (Box box : GameScreen.Movables) {
            box.isPushed = (Math.abs(GameScreen.player.position.x - box.position.x) <=
                    (17f / Constants.TILE_SIZE)
                    && GameScreen.player.position.y == box.position.y
                    && (GameScreen.player.position.x - box.position.x) * GameScreen.player.direction < 0
                    && GameScreen.player.pushing);
        }


        if (GameScreen.state == GameScreen.GameStates.GAME) { // VERY IMPORTANT TO DO DEATH DETECTION ONLY DURING GAME
            // Detects if the player has been hit by any of the lasers
            for (Laser laser : GameScreen.Lasers) if (laser.laserOn){
                rangeLeft = laser.rangeLeft;
                rangeRight = laser.rangeRight;
                x = laser.x;
                y = laser.y;

                if (laser.horizontal) {
                    // Left side first
                    if (rangeLeft != x // Making sure that the side is not blocked off completely
                        && rangeLeft <= GameScreen.player.position.x && GameScreen.player.position.x < (x - 0.5)
                        && (y - 0.5) <= GameScreen.player.position.y && GameScreen.player.position.y <= (y)) {

                        GameScreen.state = GameScreen.GameStates.FAIL;
                        GameScreen.player.velocity.x = 0;
                        GameScreen.timer.start();
                        GameScreen.deathSound.play(BoxStorm.volume);
                    }
                    // Now check right side
                    if (rangeRight != x // Making sure that the side is not blocked off completely
                        && (x + 0.5) <= GameScreen.player.position.x && GameScreen.player.position.x < (rangeRight - 0.5)
                        && (y - 0.5) <= GameScreen.player.position.y && GameScreen.player.position.y <= (y + 1)) {// add one here because lasers should kill when stepped on

                        GameScreen.state = GameScreen.GameStates.FAIL;
                        GameScreen.player.velocity.x = 0;
                        GameScreen.timer.start();
                        GameScreen.deathSound.play(BoxStorm.volume);
                    }
                } else {
                    // Top side first
                    if (rangeLeft!=y
                        && rangeLeft <= GameScreen.player.position.y && GameScreen.player.position.y <= (y - 0.5)
                        && (x - 0.5) <= GameScreen.player.position.x && GameScreen.player.position.x <= (x + 0.5)) {// here don't add anything because you can stand next to lasers

                        GameScreen.state = GameScreen.GameStates.FAIL;
                        GameScreen.player.velocity.x = 0;
                        GameScreen.timer.start();
                        GameScreen.deathSound.play(BoxStorm.volume);
                    }
                    // Bottom
                    if (rangeRight!=y
                        && (y + 0.5) <= GameScreen.player.position.y && GameScreen.player.position.y <= (rangeRight - 0.5)
                        && (x - 0.5) <= GameScreen.player.position.x && GameScreen.player.position.x <= (x + 0.5)) {

                        GameScreen.state = GameScreen.GameStates.FAIL;
                        GameScreen.player.velocity.x = 0;
                        GameScreen.timer.start();
                        GameScreen.deathSound.play(BoxStorm.volume);
                    }
                }
            }

            for (Spike spike : GameScreen.Spikes) {

                if (   (spike.x - 0.6) <= GameScreen.player.position.x && GameScreen.player.position.x <= (spike.x + 0.6)
                    && (spike.y + 1) >= GameScreen.player.position.y && spike.y <= GameScreen.player.position.y
                    && GameScreen.player.grounded && spike.id == 1) {

                    System.out.println("DEATH_BY_SPIKES: 1");
                    GameScreen.state = GameScreen.GameStates.FAIL;
                    GameScreen.timer.start();
                    GameScreen.deathSound.play(BoxStorm.volume);
                }
                if (   (spike.y - 0.6) <= GameScreen.player.position.y && GameScreen.player.position.y <= (spike.y + 0.6)
                    && (spike.x - 1) <= GameScreen.player.position.x && spike.x >= GameScreen.player.position.x
                    && GameScreen.player.direction == 1 && spike.id == 2) {

                    System.out.println("DEATH_BY_SPIKES: 2");
                    GameScreen.state = GameScreen.GameStates.FAIL;
                    GameScreen.timer.start();
                    GameScreen.deathSound.play(BoxStorm.volume);
                }
                if (   (spike.x - 0.6) <= GameScreen.player.position.x && GameScreen.player.position.x <= (spike.x + 0.6)
                    && (spike.y - 1) == GameScreen.player.position.y && spike.y >= GameScreen.player.position.y
                    && !GameScreen.player.grounded && spike.id == 3) {

                    System.out.println("DEATH_BY_SPIKES: 3");
                    GameScreen.state = GameScreen.GameStates.FAIL;
                    GameScreen.timer.start();
                    GameScreen.deathSound.play(BoxStorm.volume);
                }
                if (   (spike.y - 0.6) <= GameScreen.player.position.y && GameScreen.player.position.y <= (spike.y + 0.6)
                    && (spike.x + 1) >= GameScreen.player.position.x && spike.x <= GameScreen.player.position.x
                    && GameScreen.player.direction == -1 && spike.id == 4) {

                    System.out.println("DEATH_BY_SPIKES: 4");
                    GameScreen.state = GameScreen.GameStates.FAIL;
                    GameScreen.timer.start();
                    GameScreen.deathSound.play(BoxStorm.volume);
                }
            }
        }

        if (GameScreen.winBox.x - 0.5 <= GameScreen.player.position.x && GameScreen.player.position.x <= GameScreen.winBox.x + 0.5
            && GameScreen.winBox.y - 0.4 <= GameScreen.player.position.y && GameScreen.player.position.y <= GameScreen.winBox.y + 0.4) {
            GameScreen.state = GameScreen.GameStates.WIN;
            GameScreen.winSound.play(BoxStorm.volume);
        }
        // Frees the rectangle to save memory
        rectPool.free(playerRect);
    }

    private void checkBoxCollisions(Box thisBox) {
        // Most of the method is the same as player's collision
        // except the positive y-axis collision, since boxes never go up
        Rectangle boxRect = rectPool.obtain();
        boxRect.set(thisBox.position.x, thisBox.position.y, thisBox.WIDTH,
        thisBox.HEIGHT);

        int startX, startY, endX, endY;
        float sX, sY, eX, eY;

        // --------------------------------------------------------------------
        // HORIZONTAL AXIS COLLISION
        // --------------------------------------------------------------------
        if (thisBox.velocity.x > 0) {
            // right side of the sprite
            startX = endX = (int) (thisBox.position.x + thisBox.WIDTH + thisBox.velocity.x);
            sX = (thisBox.position.x + thisBox.WIDTH + thisBox.velocity.x) - 1;
            eX = sX + 2;
        } else {
            // left side of the sprite
            startX = endX = (int) (thisBox.position.x + thisBox.velocity.x);
            sX = (thisBox.position.x + thisBox.velocity.x) - 1;
            eX = sX + 2;
        }
        startY = (int) (thisBox.position.y);
        endY = (int) (thisBox.position.y + thisBox.HEIGHT);
        sY = (thisBox.position.y);
        eY = (thisBox.position.y + thisBox.HEIGHT);

        boxRect.x += thisBox.velocity.x;

        getTiles(startX, startY, endX, endY, tiles);
        getOtherBoxes(sX, sY, eX, eY, boxes, thisBox);
        for (Rectangle tile : tiles) {
            if (boxRect.overlaps(tile)) {
                thisBox.velocity.x = 0;
                // If box is close enough and moving towards the tile move it right next to the tile
                if (Math.abs(thisBox.position.y - tile.y) < 0.5)
                    thisBox.position.x = tile.x
                    + Math.signum(thisBox.position.x - tile.x);
                thisBox.isPushed = false;
                break;
            }
        }

        thisBox.canBePushed = true;

        for (Rectangle box : boxes) {
            if (boxRect.overlaps(box)) {
                thisBox.velocity.x = 0;
                thisBox.isPushed = false;

                if(thisBox.velocity.y == 0)
                    thisBox.canBePushed = false;

                GameScreen.player.pushing = false;
                if (Math.abs(thisBox.position.y - box.y) < 0.5)
                    thisBox.position.x = box.x
                    + Math.signum(thisBox.position.x - box.x);
                break;

            }
        }


        // --------------------------------------------------------------------
        // VERTICAL AXIS COLLISION
        // --------------------------------------------------------------------
        startY = endY = (int) (thisBox.position.y + thisBox.velocity.y);
        startX = (int) (thisBox.position.x);
        endX = (int) (thisBox.position.x + thisBox.WIDTH);
        sY = (thisBox.position.y + thisBox.velocity.y) - 1;
        eY = sY + 1;
        sX = (thisBox.position.x) - 1;
        eX = sX + 2;
        boxRect.y += thisBox.velocity.y;

        getTiles(startX, startY, endX, endY, tiles);
        getOtherBoxes(sX, sY, eX, eY, boxes, thisBox);
        for (Rectangle tile : tiles) {
            if (boxRect.overlaps(tile)) {
                thisBox.position.y = tile.y + tile.height;
                thisBox.velocity.y = 0;
                break;
            }
        }

        for (Rectangle box : boxes) {
            if (boxRect.overlaps(box)) {
                thisBox.position.y = box.y + box.height;
                thisBox.velocity.y = 0;
                break;
            }
        }

        // This checks for boxes on top of the box and sets canBePushed false,
        // which makes it impossible to push it for the rest of the game.
        sY = (thisBox.position.y);
        eY = sY + 1;
        getOtherBoxes(sX, sY, eX, eY, boxes, thisBox);
        for (Rectangle box : boxes) {
            if (box.y - thisBox.position.y < 1.5 && thisBox.velocity.y == 0) {
                thisBox.canBePushed = false;
                break;
            }
        }

        rectPool.free(boxRect);
    }


    public void checkLaserCollisions(Laser thisLaser) {
        // This method only checks for boxes, since tiles colliding with laser is
        // taken care of in initLasers() in GameScreen
        float startX, startY, endX, endY;
        int rangeLeft = thisLaser.rangeLeft;
        int rangeRight = thisLaser.rangeRight;
        int x = thisLaser.x;
        int y = thisLaser.y;
        int newRangeLeft = rangeLeft;
        int newRangeRight = rangeRight;

        // Determine the direction, then set search area to fit current laser locations.
        // Get boxes in that area and check the size. If there's a box changes
        // range, so that the laser doesn't go past the box.
        if (thisLaser.horizontal) {
            startX = rangeLeft;
            endX = rangeRight;
            startY = y - 0.5f;
            endY = y + 0.5f;

            getAllBoxes(startX, startY, x, endY, boxes);
            if (boxes.size != 0) {
                for (Rectangle box : boxes) {
                    if (newRangeLeft < box.x)
                        newRangeLeft = (int) box.x + 1;
                }
            }
            getAllBoxes(x, startY, endX, endY, boxes);
            if (boxes.size != 0) {
                for (Rectangle box : boxes) {
                    if (newRangeRight > box.x)
                        newRangeRight = (int) box.x;
                }
            }
        } else {
            startY = rangeLeft;
            endY = rangeRight;
            startX = x - 0.5f;
            endX = x + 0.5f;

            getAllBoxes(startX, startY, endX, y, boxes);
            if (boxes.size != 0) {
                for (Rectangle box : boxes) {
                    if (newRangeLeft >= box.y)
                        newRangeLeft = (int) box.y + 1;
                }
            }
            getAllBoxes(startX, y, endX, endY, boxes);
            if (boxes.size != 0) {
                for (Rectangle box : boxes) {
                    if (newRangeRight <= box.y + 2)
                        newRangeRight = (int) box.y;
                }
            }


        }

        thisLaser.updateRange(newRangeLeft, newRangeRight);
    }


    // --------------------------------------------------------------------------------------------
    //                                   GETTER METHODS
    // --------------------------------------------------------------------------------------------
    private void getTiles(float startX, float startY, float endX, float endY,
                          Array<Rectangle> tiles) {
        // get all tiles in "walls" layer of tileMap
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(
        "walls");
        rectPool.freeAll(tiles);
        tiles.clear();
        for (float y = startY; y <= endY; y++) {
            for (float x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell((int) x, (int) y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                }
            }
        }
    }

    private void getOtherBoxes(float startX, float startY, float endX,
                               float endY, Array<Rectangle> boxes, Box thisBox) {
        float x, y;
        // get all boxes near thisBox, but not thisBox itself
        rectPool.freeAll(boxes);
        boxes.clear();
        for (float i = 0; i < GameScreen.Movables.size; i++) {
            Box box = GameScreen.Movables.get((int) i);
            if (startX <= box.position.x && box.position.x <= endX
            && startY <= box.position.y && box.position.y <= endY
            && box != thisBox) {
                x = box.position.x;
                y = box.position.y;
                Rectangle rect = rectPool.obtain();
                rect.set(x, y, 1, 1);
                boxes.add(rect);
            }
        }
    }

    private void getAllBoxes(float startX, float startY, float endX,
                             float endY, Array<Rectangle> boxes) {
        float x, y;
        // get all boxes near the player
        rectPool.freeAll(boxes);
        boxes.clear();
        for (int i = 0; i < GameScreen.Movables.size; i++) {
            Box box = GameScreen.Movables.get(i);
            if (startX <= box.position.x && box.position.x <= endX
            && startY <= box.position.y && box.position.y <= endY) {
                x = box.position.x;
                y = box.position.y;
                Rectangle rect = rectPool.obtain();
                rect.set(x, y, 1, 1);
                boxes.add(rect);
            }
        }
    }

    private void checkButtonCollisions(Button button) {
        if (button.x - 0.4 < GameScreen.player.position.x && GameScreen.player.position.x < button.x + 0.4
            && button.y - 0.5 < GameScreen.player.position.y && GameScreen.player.position.y < button.y + 0.1)

            button.buttonOn = true;
        for (Box box : GameScreen.Movables)
            if (button.x - 0.4 < box.position.x && box.position.x < button.x + 0.4
            && button.y - 0.5 < box.position.y && box.position.y < button.y + 0.1)

                button.buttonOn = true;
    }
}
// @formatter:on