package dev.therealdan.lights.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dev.therealdan.lights.main.Lights;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
    private SpriteBatch spriteBatch;

    private boolean textBegun = false;

    private List<Task> tasks = new ArrayList<>();

    public Renderer() {
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        spriteBatch = new SpriteBatch();
    }

    // MAIN

    public void draw() {
        if (textBegun) {
            spriteBatch.end();
            textBegun = false;
        }

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        for (Task task : tasks)
            task.perform(shapeRenderer);
        shapeRenderer.end();

        spriteBatch.begin();
        for (Task task : tasks)
            task.perform(this, bitmapFont, spriteBatch);
        spriteBatch.end();

        tasks.clear();
    }

    public void queue(Task task) {
        tasks.add(task);
    }

    public void resize() {
        shapeRenderer.dispose();
        spriteBatch.dispose();

        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
    }

    public void dispose() {
        shapeRenderer.dispose();
        bitmapFont.dispose();
        spriteBatch.dispose();
    }

    public float getWidth(String text) {
        if (!textBegun && !spriteBatch.isDrawing()) {
            spriteBatch.begin();
            textBegun = true;
        }
        return bitmapFont.draw(spriteBatch, text, 0, -100).width;
    }

    // CONVENIENCE

    public void box(float x, float y, float width, float height, Color background, String text) {
        box(x, y, width, height, background, Lights.theme.TEXT, text);
    }

    public void box(float x, float y, float width, float height, Color background, String text, Task.TextPosition textPosition) {
        box(x, y, width, height, background, Lights.theme.TEXT, text, textPosition);
    }

    public void box(float x, float y, float width, float height, Color background, Color textColor, String text) {
        box(x, y, width, height, background, textColor, text, Task.TextPosition.LEFT_CENTER);
    }

    public void box(float x, float y, float width, float height, Color background, Color textColor, String text, Task.TextPosition textPosition) {
        box(x, y, width, height, background);
        queue(new Task(x, y).text(text, textPosition, width, height).setColor(textColor));
    }

    public void box(float x, float y, float width, float height, Color background) {
        queue(new Task(x, y - height).rect(width, height).setColor(background));
        queue(new Task(x, y - height).rectOutline(width, height).setColor(Lights.theme.LIGHT));
    }
}