package dev.therealdan.lights.panels.menuicons;

import dev.therealdan.lights.controllers.Button;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.MenuIcon;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;

public class AddButtonIcon implements MenuIcon {

    @Override
    public boolean click(Panel panel) {
        Button.add(new Button("New Button", Lights.theme.MEDIUM));
        return true;
    }

    @Override
    public boolean draw(Renderer renderer, float x, float y, float width, float height, int index, boolean hover, boolean click) {
        float offset = 4;
        renderer.queue(new Task(x + offset, y - height / 2).line(x + width - offset, y - height / 2).setColor(hover ? Lights.theme.GREEN : Lights.theme.WHITE));
        renderer.queue(new Task(x + width / 2, y - offset).line(x + width / 2, y - height + offset).setColor(hover ? Lights.theme.GREEN : Lights.theme.WHITE));
        return false;
    }
}