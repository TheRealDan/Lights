package me.therealdan.lights.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;

public class Util {

    public static void box(Renderer renderer, float x, float y, float width, float height, Color background, String text) {
        box(renderer, x, y, width, height, background);
        renderer.queue(new Task(x + 4, y - 3 - height / 2f).text(text, Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));
    }

    public static void box(Renderer renderer, float x, float y, float width, float height, Color background) {
        renderer.queue(new Task(x, y - height).rect(width, height).setColor(background));
        renderer.queue(new Task(x, y - height).rectOutline(width, height).setColor(LightsCore.light()));
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