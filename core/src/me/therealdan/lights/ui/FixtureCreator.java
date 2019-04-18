package me.therealdan.lights.ui;

import com.badlogic.gdx.Input;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;

public class FixtureCreator implements Visual {

    public FixtureCreator() {

    }

    @Override
    public boolean draw(Renderer renderer) {
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE:
                Lights.openMainView();
                return false;
        }

        return true;
    }
}