package me.therealdan.lights.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Keyboard {

    protected Keyboard() {

    }

    public boolean isShift() {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
    }
}