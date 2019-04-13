package me.therealdan.lights.ui;

import com.badlogic.gdx.InputProcessor;
import me.therealdan.lights.renderer.Renderer;

import java.util.LinkedList;

public class TheInputProcessor implements InputProcessor {

    private LinkedList<Visual> active = new LinkedList<>();

    public TheInputProcessor() {

    }

    public void draw(Renderer renderer) {
        for (Visual visual : active)
            visual.draw(renderer);
    }

    public void resize(int width, int height) {
        for (Visual visual : active)
            visual.resize(width, height);
    }

    public void add(Visual visual) {
        active.add(visual);
    }

    public void remove(Visual visual) {
        active.remove(visual);
    }

    public void clear() {
        active.clear();
    }

    public boolean isActive(Visual visual) {
        return active.contains(visual);
    }

    @Override
    public boolean keyDown(int keycode) {
        for (Visual visual : active)
            if (!visual.keyDown(keycode)) return false;

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (Visual visual : active)
            if (!visual.keyUp(keycode)) return false;

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        for (Visual visual : active)
            if (!visual.keyTyped(character)) return false;

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (Visual visual : active)
            if (!visual.touchDown(screenX, screenY, pointer, button)) return false;

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (Visual visual : active)
            if (!visual.touchUp(screenX, screenY, pointer, button)) return false;

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        for (Visual visual : active)
            if (!visual.touchDragged(screenX, screenY, pointer)) return false;

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        for (Visual visual : active)
            if (!visual.mouseMoved(screenX, screenY)) return false;

        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        for (Visual visual : active)
            if (!visual.scrolled(amount)) return false;

        return true;
    }
}