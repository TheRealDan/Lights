package me.therealdan.lights.util;

import com.badlogic.gdx.Gdx;

public class Util {

    public static boolean containsMouse(float x, float y, float width, float height) {
        y = Gdx.graphics.getHeight() - y;

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    public static String getPercentage(float value) {
        return Float.toString(value * 100.0f).replace(".", "X").split("X")[0] + "%";
    }
}