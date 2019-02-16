package me.therealdan.lights.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Task {

    private boolean color = false;
    private boolean rect = false;
    private boolean rectOutline = false;
    private boolean line = false;

    private float r, g, b, a;
    private float x, y, x1, y1;
    private float width, height;
    private float textWidth, textHeight;
    private String text;
    private TextPosition textPosition;

    public Task(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Task(Color color) {
        setColor(color);
    }

    public Task(float r, float g, float b, float a) {
        setColor(r, g, b, a);
    }

    public Task setColor(Color color) {
        return setColor(color.r, color.g, color.b, color.a);
    }

    public Task setColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.color = true;
        return this;
    }

    public Task text(String text) {
        return text(text, TextPosition.DEFAULT);
    }

    public Task text(String text, TextPosition textPosition) {
        return text(text, textPosition, 0, 0);
    }

    public Task text(String text, TextPosition textPosition, float width, float height) {
        this.text = text;
        this.textPosition = textPosition;
        this.textWidth = width;
        this.textHeight = height;
        return this;
    }

    public Task rect(float width, float height) {
        this.width = width;
        this.height = height;
        this.rect = true;
        return this;
    }

    public Task rectOutline(float width, float height) {
        this.width = width;
        this.height = height;
        this.rectOutline = true;
        return this;
    }

    public Task line(float x1, float y1) {
        this.x1 = x1;
        this.y1 = y1;
        this.line = true;
        return this;
    }

    public void perform(ShapeRenderer shapeRenderer) {
        if (color) shapeRenderer.setColor(r, g, b, a);
        if (rect) {
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect(x, y, width, height);
        }
        if (rectOutline) {
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(x, y, width, height);
        }
        if (line) {
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.line(x, y, x1, y1);
        }
    }

    public void perform(Renderer renderer, BitmapFont bitmapFont, SpriteBatch spriteBatch) {
        if (color) bitmapFont.setColor(r, g, b, a);
        if (text != null) {
            switch (textPosition) {
                case DEFAULT:
                    bitmapFont.draw(spriteBatch, text, x, y);
                    break;
                case BOTTOM_LEFT:
                    bitmapFont.draw(spriteBatch, text, x, y + bitmapFont.getLineHeight());
                    break;
                case LEFT_CENTER:
                    bitmapFont.draw(spriteBatch, text, x, y + bitmapFont.getLineHeight() / 2);
                    break;
                case CENTER:
                    bitmapFont.draw(spriteBatch, text, x + textWidth / 2 - renderer.getWidth(text) / 2, y - textHeight / 2 + bitmapFont.getLineHeight() / 2);
                    break;
            }
        }
    }

    public enum TextPosition {
        DEFAULT,
        BOTTOM_LEFT,
        LEFT_CENTER,
        CENTER,
    }
}