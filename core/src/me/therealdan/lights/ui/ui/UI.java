package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.UIHandler;
import me.therealdan.lights.util.Util;

import java.util.HashMap;
import java.util.HashSet;

public interface UI {

    HashSet<String> hidden = new HashSet<>();
    HashSet<String> allowInteract = new HashSet<>();
    HashMap<String, Float> uiLocation = new HashMap<>();

    default void load() {
        FileHandle fileHandle = Gdx.files.local("Lights/UI/" + getName() + ".txt");
        if (fileHandle.exists()) {
            String[] lines = fileHandle.readString().split("\\r?\\n");
            setLocation(
                    Float.parseFloat(lines[0].replace("X: ", "")),
                    Float.parseFloat(lines[1].replace("Y: ", "")));
            if (!ignoreVisibilityUI() && lines.length > 2) setVisible(Boolean.parseBoolean(lines[2].replace("Visible: ", "")));
        } else {
            if (!ignoreVisibilityUI()) setVisible(false);
        }
    }

    default void save() {
        Gdx.files.local("Lights/UI/" + getName() + ".txt").writeString("X: " + getX() + "\r\n", false);
        Gdx.files.local("Lights/UI/" + getName() + ".txt").writeString("Y: " + getYString() + "\r\n", true);
        if (!ignoreVisibilityUI()) Gdx.files.local("Lights/UI/" + getName() + ".txt").writeString("Visible: " + isVisible(), true);
    }

    default void scrolled(int amount) {
    }

    default void keyUp(int keycode) {
    }

    default boolean keyDown(int keycode) {
        return false;
    }

    default boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        Util.box(renderer, getX(), getY(), getWidth(), getHeight(), Lights.medium());
        return true;
    }

    default void setLocation(float x, float y) {
        set(getName() + "_X", x);
        set(getName() + "_Y", y);
    }

    default void resize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    default String setWidth(Renderer renderer, String text) {
        return setWidth(renderer, text, 1);
    }

    default String setWidth(Renderer renderer, String text, float multiplier) {
        float length = (renderer.getWidth(text) + 10) * multiplier;
        if (getWidth() < length) setWidth(length);
        return text;
    }

    default void setWidth(float width) {
        set(getName() + "_WIDTH", width);
    }

    default void setWidth(float width, boolean ifLarger) {
        if (ifLarger)
            if (width <= getWidth())
                return;

        setWidth(width);
    }

    default void setWidthBasedOnX(float x) {
        setWidth(getX() - x);
    }

    default void setHeight(float height) {
        set(getName() + "_HEIGHT", height);
    }

    default void setHeightBasedOnY(float y) {
        setHeight(getY() - y);
    }

    default boolean containsMouse() {
        return Util.containsMouse(getX(), getY(), getWidth(), getHeight());
    }

    default void toggleVisibility() {
        setVisible(!isVisible());
    }

    default void setVisible(boolean visibile) {
        if (visibile) {
            hidden.remove(getName());
        } else {
            hidden.add(getName());
        }
    }

    default boolean isVisible() {
        return !hidden.contains(getName());
    }

    default boolean ignoreVisibilityUI() {
        return false;
    }

    default void setAllowInteract(boolean allowInteract) {
        if (allowInteract) {
            UI.allowInteract.add(getName());
        } else {
            UI.allowInteract.remove(getName());
        }
    }

    default boolean canInteract() {
        return allowInteract.contains(getName());
    }

    default void drag(float x, float y, float width, float cellHeight) {
        if (canInteract() && isVisible() && Util.containsMouse(x, y, width, cellHeight) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) UIHandler.drag(this);
    }

    default boolean isDragging() {
        return UIHandler.getDragging().equals(this);
    }

    default String getName() {
        return getClass().getSimpleName().substring(0, getClass().getSimpleName().length() - 2);
    }

    default float getX() {
        return get(getName() + "_X", 10);
    }

    default float getY() {
        return Gdx.graphics.getHeight() - get(getName() + "_Y", 10);
    }

    default float getYString() {
        return get(getName() + "_Y", 10);
    }

    default float getWidth() {
        return get(getName() + "_WIDTH", 10);
    }

    default float getHeight() {
        return get(getName() + "_HEIGHT", 10);
    }

    static void set(String key, float value) {
        uiLocation.put(key, value);
    }

    static float get(String key, float defaultValue) {
        if (!uiLocation.containsKey(key)) set(key, defaultValue);
        return uiLocation.get(key);
    }
}