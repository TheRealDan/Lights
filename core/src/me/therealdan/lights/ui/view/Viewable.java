package me.therealdan.lights.ui.view;

import com.badlogic.gdx.InputProcessor;
import me.therealdan.lights.renderer.Renderer;

import java.util.HashSet;

public interface Viewable extends InputProcessor {

    HashSet<Viewable> focus = new HashSet<>();

    default void resize(int width, int height) {}

    default void setFocus(boolean focus) {
        if (focus) Viewable.focus.add(this);
        if (!focus) Viewable.focus.remove(this);
    }

    default boolean hasFocus() {
        return Viewable.focus.contains(this);
    }

    @Override
    default boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    default boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    default boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    default boolean scrolled(int amount) {
        return true;
    }

    @Override
    default boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    default boolean keyDown(int keycode) {
        return true;
    }

    @Override
    default boolean keyUp(int keycode) {
        return true;
    }

    @Override
    default boolean keyTyped(char character) {
        return true;
    }

    void draw(Renderer renderer, float x, float y, float width, float height);

    default String getName() {
        return getClass().getSimpleName();
    }

    default Split getSplit() {
        return Split.NONE;
    }

    static Viewable byName(String name) {
        for (Tab tab : Tab.values())
            if (tab.getName().equals(name))
                return tab;

        return null;
    }

    enum Split {
        NONE,
        VERTICAL,
        HORIZONTAL,
    }
}