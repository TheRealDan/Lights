package me.therealdan.lights.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Mouse {

    private boolean leftMouseUp = true;
    private boolean rightMouseUp = true;
    private long lastAction = System.currentTimeMillis();

    protected Mouse() {

    }

    public void update() {
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) leftMouseUp = true;
        if (!Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) rightMouseUp = true;
    }

    public boolean leftClicked(long milliseconds, boolean override) {
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) return false;
        if (override) milliseconds = 0;
        return leftReady(milliseconds);
    }

    public boolean leftReady(long milliseconds) {
        if (milliseconds == -1) return leftMouseUp;

        if (leftMouseUp || System.currentTimeMillis() - lastAction > milliseconds) {
            leftMouseUp = false;
            lastAction = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public boolean rightClicked(long milliseconds, boolean override) {
        if (!Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) return false;
        if (override) milliseconds = 0;
        return rightReady(milliseconds);
    }

    public boolean rightReady(long milliseconds) {
        if (milliseconds == -1) return rightMouseUp;

        if (rightMouseUp || System.currentTimeMillis() - lastAction > milliseconds) {
            rightMouseUp = false;
            lastAction = System.currentTimeMillis();
            return true;
        }
        return false;
    }
}