package dev.therealdan.lights.panels.panels;

import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.ui.UIHandler;

public class FrozenPanel implements Panel {

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.FROZEN);
        boolean interacted = false;

        if (!Lights.output.isFrozen()) return interacted;

        float cellHeight = 30;

        float x = getX();
        float y = getY();

        float width = getWidth();

        renderer.box(x, y, width, cellHeight,
                System.currentTimeMillis() % 1000 > 500 ? Lights.color.DARK_BLUE : Lights.color.RED,
                System.currentTimeMillis() % 1000 > 500 ? Lights.color.TEXT : Lights.color.BLACK,
                setWidth(renderer, "OUTPUT FROZEN"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, setWidth(renderer, "Press Escape to unfreeze"), Task.TextPosition.CENTER);
        y -= cellHeight;

        setHeightBasedOnY(y);
        return interacted;
    }

    @Override
    public boolean ignoreVisibilityUI() {
        return true;
    }
}