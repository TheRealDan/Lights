package me.therealdan.lights.renderer;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
}