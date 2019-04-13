package me.therealdan.lights.ui.ui;

import me.therealdan.lights.main.Lights;
import me.therealdan.lights.dmx.Output;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.UIHandler;
import me.therealdan.lights.util.Util;

public class FrozenUI implements UI {

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.FROZEN);
        boolean interacted = false;

        if (!Output.isFrozen()) return interacted;

        float cellHeight = 30;

        float x = getX();
        float y = getY();

        float width = getWidth();

        Util.box(renderer, x, y, width, cellHeight,
                System.currentTimeMillis() % 1000 > 500 ? Lights.DARK_BLUE : Lights.RED,
                System.currentTimeMillis() % 1000 > 500 ? Lights.text() : Lights.BLACK,
                setWidth(renderer, "OUTPUT FROZEN"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, Lights.medium(), setWidth(renderer, "Press Escape to unfreeze"), Task.TextPosition.CENTER);
        y -= cellHeight;

        setHeightBasedOnY(y);
        return interacted;
    }

    @Override
    public boolean ignoreVisibilityUI() {
        return true;
    }
}