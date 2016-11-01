package kvel.boxstorm.GameObjects;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import kvel.boxstorm.AnimationManager;
import kvel.boxstorm.Constants;
import kvel.boxstorm.animations.ButtonAnimation;
import kvel.boxstorm.screens.GameScreen;

import java.util.HashMap;
import java.util.Map;

public class Button {

    public float WIDTH;
    public float HEIGHT;
    public ButtonAnimation buttonAnimation;
    public TextureRegion buttonTexture;

    public int id;
    public boolean buttonOn = false;
    public int x;
    public int y;
    Array<Laser> myLasers = new Array<Laser>();

    public boolean soundPlayed = false;
    public boolean onSoundPlayed = false;

    private Array<Laser> Lasers = new Array<Laser>();
    private Map<String, Boolean> AnimationArgs = new HashMap<String, Boolean>();

    public Button(int x, int y, int id) {
        buttonAnimation = new ButtonAnimation();
        buttonAnimation.create();

        WIDTH   = 1 / (float) Constants.TILE_SIZE * buttonAnimation.getObjectWidth();
        HEIGHT  = 1 / (float) Constants.TILE_SIZE * buttonAnimation.getObjectHeight();
        this.id = id;
        this.x  = x;
        this.y  = y;
        // Assigning all the lasers that have the same id as the button and
        // are therefore controllable by the but             mGameScreen.Lasers.get(id)rs }
        for (Laser laser : GameScreen.Lasers) {
            if (laser.id == id) {
                Lasers.add(laser);
            }
        }
    }

    public void render(Batch batch) {
        AnimationArgs.clear();
        AnimationArgs.put("buttonOn", buttonOn);
        buttonAnimation.newConditions(AnimationArgs);
        buttonAnimation.conditionsApply();
        buttonTexture = buttonAnimation.getTexture();

        batch.begin();
        batch.draw(buttonTexture, x, y - 1, 1, 1);
        batch.end();
    }

    public void update(float deltaTime) {
        if (deltaTime == 0) return;
        // Turns off connected lasers if button is pressed
        if(buttonOn) {
            if (!soundPlayed) {
                GameScreen.laserOffSound.play();
                soundPlayed = true;
                onSoundPlayed = true;
            }
            for (Laser laser : Lasers)
                laser.laserOn = false;
        }
        else if (onSoundPlayed){
            GameScreen.laserOnSound.play();
            soundPlayed = false;
            onSoundPlayed = false;
        }
        buttonOn = false;

    }
}

