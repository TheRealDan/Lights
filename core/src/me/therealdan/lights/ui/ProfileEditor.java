package me.therealdan.lights.ui;

import com.badlogic.gdx.Input;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;

public class ProfileEditor implements Visual {

    // TODO - Finish Profile editor; Channels, Models and how they interact

    @Override
    public boolean draw(Renderer renderer) {
        float X = 0;
        float Y = Gdx.graphics.getHeight();
        float WIDTH = Gdx.graphics.getWidth();
        float cellHeight = 30;

        float x = X;
        float y = Y;
        float width = WIDTH;

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