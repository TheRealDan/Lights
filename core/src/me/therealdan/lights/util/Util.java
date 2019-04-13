package me.therealdan.lights.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;

public class Util {

    public static void box(Renderer renderer, float x, float y, float width, float height, Color background, String text) {
        box(renderer, x, y, width, height, background, Lights.color.TEXT, text);
    }

    public static void box(Renderer renderer, float x, float y, float width, float height, Color background, String text, Task.TextPosition textPosition) {
        box(renderer, x, y, width, height, background, Lights.color.TEXT, text, textPosition);
    }

    public static void box(Renderer renderer, float x, float y, float width, float height, Color background, Color textColor, String text) {
        box(renderer, x, y, width, height, background, textColor, text, Task.TextPosition.LEFT_CENTER);
    }

    public static void box(Renderer renderer, float x, float y, float width, float height, Color background, Color textColor, String text, Task.TextPosition textPosition) {
        box(renderer, x, y, width, height, background);
        renderer.queue(new Task(x, y).text(text, textPosition, width, height).setColor(textColor));
    }

    public static void box(Renderer renderer, float x, float y, float width, float height, Color background) {
        renderer.queue(new Task(x, y - height).rect(width, height).setColor(background));
        renderer.queue(new Task(x, y - height).rectOutline(width, height).setColor(Lights.color.LIGHT));
    }

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