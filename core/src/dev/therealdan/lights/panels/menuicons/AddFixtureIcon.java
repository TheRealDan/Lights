package dev.therealdan.lights.panels.menuicons;

import dev.therealdan.lights.panels.MenuIcon;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.DisplayHandler;

public class AddFixtureIcon implements MenuIcon {

    private DisplayHandler _displayHandler;

    public AddFixtureIcon(DisplayHandler displayHandler) {
        _displayHandler = displayHandler;
    }

    @Override
    public boolean click(Panel panel) {
        _displayHandler.setFocus(DisplayHandler.Focus.FIXTURE_EDITOR);
        return true;
    }

    @Override
    public boolean draw(Renderer renderer, float x, float y, float width, float height, int index, boolean hover, boolean click) {
        float offset = 4;
        renderer.queue(new Task(x + offset, y - height / 2).line(x + width - offset, y - height / 2).setColor(hover ? renderer.getTheme().GREEN : renderer.getTheme().WHITE));
        renderer.queue(new Task(x + width / 2, y - offset).line(x + width / 2, y - height + offset).setColor(hover ? renderer.getTheme().GREEN : renderer.getTheme().WHITE));
        return false;
    }
}