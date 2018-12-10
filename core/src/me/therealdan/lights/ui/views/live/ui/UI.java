package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.util.Util;

import java.util.HashMap;
import java.util.HashSet;

public interface UI {

    HashSet<String> hidden = new HashSet<>();
    HashMap<String, Float> uiLocation = new HashMap<>();

    default void load() {
        FileHandle fileHandle = Gdx.files.local("Lights/UI/" + getName() + ".txt");
        if (fileHandle.exists()) {
            String[] location = fileHandle.readString().split(";");
            setLocation(Float.parseFloat(location[0]), Float.parseFloat(location[1]));
        }
    }

    default void save() {
        Gdx.files.local("Lights/UI/" + getName() + ".txt").writeString(getX() + ";" + getY(), false);
    }

    default void keyUp(int keycode) {
    }

    default void keyDown(int keycode) {
    }

    default boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        Util.box(renderer, getX(), Gdx.graphics.getHeight() - getY(), getWidth(), getHeight(), LightsCore.medium());
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

    default void setHeight(float height) {
        set(getName() + "_HEIGHT", height);
    }

    default boolean containsMouse() {
        return Util.containsMouse(getX(), getY(), getWidth(), getHeight());
    }

    default boolean isVisible() {
        return !hidden.contains(getName());
    }

    default String getName() {
        return getClass().getSimpleName();
    }

    default float getX() {
        return get(getName() + "_X", 10);
    }

    default float getY() {
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