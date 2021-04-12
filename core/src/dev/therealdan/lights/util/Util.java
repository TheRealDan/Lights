package dev.therealdan.lights.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public abstract class Util {

    public static String getPercentage(float value) {
        return Float.toString(value * 100.0f).replace(".", "X").split("X")[0] + "%";
    }

    public static boolean isShiftHeld() {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
    }

    public static String format(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String piece : string.split("_")) {
            stringBuilder.append(" ");
            stringBuilder.append(piece.substring(0, 1).toUpperCase());
            stringBuilder.append(piece.substring(1).toLowerCase());
        }
        return stringBuilder.toString().substring(1);
    }
}