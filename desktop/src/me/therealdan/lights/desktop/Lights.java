package me.therealdan.lights.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.therealdan.lights.LightsCore;

public class Lights {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Lights";
		new LwjglApplication(new LightsCore(), config);
	}
}
