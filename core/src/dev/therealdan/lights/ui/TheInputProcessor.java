package dev.therealdan.lights.ui;

import com.badlogic.gdx.InputProcessor;
import dev.therealdan.lights.renderer.Renderer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class TheInputProcessor implements InputProcessor {

    private Deque<Visual> active = new ArrayDeque<>();

    public void draw(Renderer renderer) {
        for (Visual visual : active)
            if (!visual.draw(renderer))
                return;
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
        Iterator<Visual> iterator = active.descendingIterator();
        while (iterator.hasNext())
            if (!iterator.next().keyDown(keycode)) return false;

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        Iterator<Visual> iterator = active.descendingIterator();
        while (iterator.hasNext())
            if (!iterator.next().keyUp(keycode)) return false;

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        Iterator<Visual> iterator = active.descendingIterator();
        while (iterator.hasNext())
            if (!iterator.next().keyTyped(character)) return false;

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Iterator<Visual> iterator = active.descendingIterator();
        while (iterator.hasNext())
            if (!iterator.next().touchDown(screenX, screenY, pointer, button)) return false;

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Iterator<Visual> iterator = active.descendingIterator();
        while (iterator.hasNext())
            if (!iterator.next().touchUp(screenX, screenY, pointer, button)) return false;

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Iterator<Visual> iterator = active.descendingIterator();
        while (iterator.hasNext())
            if (!iterator.next().touchDragged(screenX, screenY, pointer)) return false;

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Iterator<Visual> iterator = active.descendingIterator();
        while (iterator.hasNext())
            if (!iterator.next().mouseMoved(screenX, screenY)) return false;

        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        Iterator<Visual> iterator = active.descendingIterator();
        while (iterator.hasNext())
            if (!iterator.next().scrolled(amount)) return false;

        return true;
    }
}