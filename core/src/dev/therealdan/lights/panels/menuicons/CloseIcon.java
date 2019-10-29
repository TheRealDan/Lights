package dev.therealdan.lights.panels.menuicons;

import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.MenuIcon;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;

public class CloseIcon implements MenuIcon {

    @Override
    public boolean click(Panel panel) {
        panel.setVisible(false);
        return true;
    }

    @Override
    public boolean draw(Renderer renderer, float x, float y, float width, float height, int index, boolean hover, boolean click) {
        if (hover) renderer.box(x, y, width, height, Lights.color.RED);

        float offset = 4;
        renderer.queue(new Task(x + offset, y - offset).setColor(Lights.color.WHITE).line(x + width - offset, y - height + offset));
        renderer.queue(new Task(x + width - offset, y - offset).setColor(Lights.color.WHITE).line(x + offset, y - height + offset));
        return false;
    }
}