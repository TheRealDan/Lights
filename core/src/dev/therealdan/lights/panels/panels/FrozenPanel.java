package dev.therealdan.lights.panels.panels;

import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.UIHandler;

public class FrozenPanel implements Panel {

    public FrozenPanel() {
        setHeight(Panel.CELL_HEIGHT * 2);
    }

    @Override
    public boolean drawMenuBar(Renderer renderer, float x, float y, float width, float height, float menuIconWidth, float menuIconHeight, float spacing) {
        boolean flash = System.currentTimeMillis() % 1000 > 500;
        renderer.box(x, y, width, height,
                flash ? Lights.color.DARK_BLUE : Lights.color.RED,
                flash ? Lights.color.TEXT : Lights.color.BLACK,
                setWidth(renderer, "OUTPUT FROZEN"), Task.TextPosition.CENTER);
        drag(x, y, width, height);
        return false;
    }

    @Override
    public boolean drawContent(Renderer renderer, float x, float y, float width, float height) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.FROZEN);

        renderer.box(x, y, width, height, Lights.color.MEDIUM, setWidth(renderer, "Press Escape to unfreeze"), Task.TextPosition.CENTER);
        drag(x, y, width, height);
        return false;
    }

    @Override
    public boolean ignoreVisibilityUI() {
        return true;
    }

    @Override
    public boolean isVisible() {
        return Lights.output.isFrozen();
    }
}