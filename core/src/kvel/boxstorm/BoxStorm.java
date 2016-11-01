package kvel.boxstorm;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import kvel.boxstorm.screens.MenuScreen;

public class BoxStorm extends Game {

	public static MenuScreen menuScreen;
	public static float volume = 1f;

	@Override
	public void create () {
		menuScreen = new MenuScreen(this);
        setScreen(menuScreen);
    }

	public static void setVolume(float i){volume = i;}
}

