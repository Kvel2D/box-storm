package kvel.boxstorm.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import kvel.boxstorm.BoxStorm;
import kvel.boxstorm.Constants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		// Window options
		config.title = "BoxStorm";
		config.resizable = false;
		if (arg.length != 0) Constants.SCREEN_SIZE = Integer.parseInt(arg[0]);
		Constants.MENU_SCALE = Constants.SCREEN_SIZE / 2f;
		config.width = Constants.SCREEN_HORIZONTAL_RATIO * Constants.TILE_SIZE * Constants.SCREEN_SIZE;
		config.height = Constants.SCREEN_VERTICAL_RATIO * Constants.TILE_SIZE * Constants.SCREEN_SIZE;

		new LwjglApplication(new BoxStorm(), config);
	}
}
