package dev.therealdan.lights.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.PanelHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface Panel {

    float MENU_HEIGHT = 30;
    float CELL_HEIGHT = 30;

    HashMap<String, Float> panelData = new HashMap<>();
    HashMap<String, List<MenuIcon>> menuIcons = new HashMap<>();
    HashMap<String, String> title = new HashMap<>();
    HashSet<String> hidden = new HashSet<>();
    HashSet<String> allowInteract = new HashSet<>();

    default void load() {
        FileHandle fileHandle = Gdx.files.local("Lights/Panels/" + getName() + ".txt");
        if (fileHandle.exists()) {
            String[] lines = fileHandle.readString().split("\\r?\\n");
            setLocation(
                    Float.parseFloat(lines[0].replace("X: ", "")),
                    Float.parseFloat(lines[1].replace("Y: ", "")));
            setWidth(Float.parseFloat(lines[2].replace("Width: ", "")));
            setHeight(Float.parseFloat(lines[3].replace("Height: ", "")));
            if (!ignoreVisibilityUI() && lines.length > 4)
                setVisible(Boolean.parseBoolean(lines[4].replace("Visible: ", "")));
        } else {
            if (!ignoreVisibilityUI()) setVisible(false);
        }
    }

    default void save() {
        Gdx.files.local("Lights/Panels/" + getName() + ".txt").writeString("X: " + getX() + "\r\n", false);
        Gdx.files.local("Lights/Panels/" + getName() + ".txt").writeString("Y: " + getYString() + "\r\n", true);
        Gdx.files.local("Lights/Panels/" + getName() + ".txt").writeString("Width: " + getWidth() + "\r\n", true);
        Gdx.files.local("Lights/Panels/" + getName() + ".txt").writeString("Height: " + getHeight() + "\r\n", true);
        if (!ignoreVisibilityUI())
            Gdx.files.local("Lights/Panels/" + getName() + ".txt").writeString("Visible: " + isVisible(), true);
    }

    default void scrolled(int amount) {
    }

    default boolean keyUp(int keycode) {
        return true;
    }

    default boolean keyDown(int keycode) {
        return true;
    }

    // Return whether hovering over resize area
    default boolean drawBackground(Renderer renderer, float x, float y, float width, float height) {
        renderer.box(x, y, width, height, Lights.theme.DARK);
        if (isResizeable())
            return Lights.mouse.contains(x + width - 20, y - height + 20, 20, 20);
        return false;
    }

    // Return whether menu bar was interacted with
    default boolean drawMenuBar(Renderer renderer, float x, float y, float width, float height, boolean interacted) {
        renderer.box(x, y, width, height, Lights.theme.DARK_BLUE, getTitle(), Task.TextPosition.CENTER);
        return interacted;
    }

    // Return whether icons were interacted with
    default boolean drawMenuIcons(Renderer renderer, float x, float y, float width, float height, float menuIconWidth, float menuIconHeight, float spacing, boolean interacted) {
        int index = 0;
        float mx = x + width;
        float my = y - spacing;
        for (MenuIcon menuIcon : getMenuIcons()) {
            if (!menuIcon.isVisible()) continue;
            boolean hover = false, click = false;
            mx -= spacing + menuIconWidth;
            if (Lights.mouse.contains(mx, my, menuIconWidth, menuIconHeight) && canInteract()) {
                interacted = true;
                hover = true;
                if (Lights.mouse.leftClicked(1000)) {
                    click = true;
                }
            }
            menuIcon.draw(renderer, mx, my, menuIconWidth, menuIconHeight, index++, hover, click);
            if (click && !click(menuIcon)) menuIcon.click(this);
        }

        if (!interacted) drag(x, y, width, height);
        return interacted;
    }

    // Return whether Panel was interacted with
    default boolean drawContent(Renderer renderer, float x, float y, float width, float height, boolean interacted) {
        return interacted;
    }

    // Return whether Panel was interacted with
    @Deprecated
    default boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        return false;
    }

    // Return whether click caused an action - MenuIcon.click() will only be called if this returns false
    default boolean click(MenuIcon menuIcon) {
        return false;
    }

    default void setLocation(float x, float y) {
        set(getName() + "_X", x);
        set(getName() + "_Y", y);
    }

    default void resize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    default void setTitle(String title) {
        Panel.title.put(getName(), title);
    }

    default String getTitle() {
        if (!title.containsKey(getName())) title.put(getName(), getFriendlyName());
        return title.get(getName());
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
        if (width < getMinimumWidth()) width = getMinimumWidth();
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
        if (height < getMinimumHeight()) height = getMinimumHeight();
        set(getName() + "_HEIGHT", height);
    }

    default void setHeightBasedOnY(float y) {
        setHeight(getY() - y);
    }

    default void setMinimumWidth(float width) {
        set(getName() + "_MIN_WIDTH", width);
    }

    default void setMinimumHeight(float height) {
        set(getName() + "_MIN_HEIGHT", height);
    }

    default boolean containsMouse() {
        return Lights.mouse.contains(getX(), getY(), getWidth(), getHeight());
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

    default boolean isResizeable() {
        return false;
    }

    default void setAllowInteract(boolean allowInteract) {
        if (allowInteract) {
            Panel.allowInteract.add(getName());
        } else {
            Panel.allowInteract.remove(getName());
        }
    }

    default boolean canInteract(boolean interacted) {
        if (interacted) setAllowInteract(false);
        return canInteract();
    }

    default boolean canInteract() {
        return allowInteract.contains(getName());
    }

    default boolean drag(float x, float y, float width, float height) {
        if (canInteract() && isVisible() && Lights.mouse.contains(x, y, width, height) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            PanelHandler.drag(this);
            return true;
        }
        return false;
    }

    default boolean isDragging() {
        return PanelHandler.getDragging().equals(this);
    }

    default void register(MenuIcon menuIcon) {
        List<MenuIcon> menuIcons = getMenuIcons();
        menuIcons.add(menuIcon);
        Panel.menuIcons.put(getName(), menuIcons);
    }

    default List<MenuIcon> getMenuIcons() {
        if (!menuIcons.containsKey(getName())) menuIcons.put(getName(), new ArrayList<>());
        return menuIcons.get(getName());
    }

    default String getFriendlyName() {
        StringBuilder stringBuilder = new StringBuilder();
        for (char letter : getName().replace("DMX", "[dmx]").toCharArray()) {
            if (Character.isUpperCase(letter) && stringBuilder.length() > 0) stringBuilder.append(" ");
            stringBuilder.append(letter);
        }
        return stringBuilder.toString().replace("[dmx]", "DMX");
    }

    default String getName() {
        return getClass().getSimpleName().substring(0, getClass().getSimpleName().length() - 5);
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

    default float getMinimumWidth() {
        return get(getName() + "_MIN_WIDTH", 50);
    }

    default float getMinimumHeight() {
        return get(getName() + "_MIN_HEIGHT", MENU_HEIGHT * 2);
    }

    default float getWidth() {
        return get(getName() + "_WIDTH", getMinimumWidth());
    }

    default float getHeight() {
        return get(getName() + "_HEIGHT", getMinimumHeight());
    }

    static void set(String key, float value) {
        panelData.put(key, value);
    }

    static float get(String key, float defaultValue) {
        if (!panelData.containsKey(key)) set(key, defaultValue);
        return panelData.get(key);
    }
}