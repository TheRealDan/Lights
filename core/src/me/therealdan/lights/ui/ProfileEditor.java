package me.therealdan.lights.ui;

import com.badlogic.gdx.Input;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;

public class ProfileEditor implements Visual {

    // TODO - Finish Profile editor; Channels, Models and how they interact

    @Override
    public void draw(Renderer renderer) {

    }

    @Override
    public void resize(int width, int height) {

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

    public void edit(Profile profile) {

    }
}