package com.zalzer.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.zalzer.game.PruetDefender;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = PruetDefender.WIDTH;
        config.height = PruetDefender.HEIGHT;
        config.title = PruetDefender.TITLE;
		config.resizable = false;
		config.samples = 16;
		new LwjglApplication(new PruetDefender(), config);
	}
}
