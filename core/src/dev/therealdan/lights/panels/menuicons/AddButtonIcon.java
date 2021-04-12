package dev.therealdan.lights.panels.menuicons;

import com.badlogic.gdx.graphics.Color;
import dev.therealdan.lights.controllers.Button;
import dev.therealdan.lights.panels.MenuIcon;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;

public class AddButtonIcon implements MenuIcon {

    private Color _defaultButtonColor;

    public AddButtonIcon(Color defaultButtonColor) {
        _defaultButtonColor = defaultButtonColor;
    }

    @Override
    public boolean click(Panel panel) {
        Button.add(new Button("New Button", _defaultButtonColor));
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