package dev.therealdan.lights.panels.menuicons;

import dev.therealdan.lights.panels.MenuIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;

public class ResizeIcon implements MenuIcon {

    @Override
    public boolean draw(Renderer renderer, float x, float y, float width, float height, int index, boolean hover, boolean click) {
        if (index == -1) {
            renderer.queue(new Task(x + width - 10, y - height + 1).rect(2, 2).setColor(renderer.getTheme().WHITE));
            renderer.queue(new Task(x + width - 7, y - height + 4).rect(2, 2).setColor(renderer.getTheme().WHITE));
            renderer.queue(new Task(x + width - 7, y - height + 1).rect(2, 2).setColor(renderer.getTheme().WHITE));
            renderer.queue(new Task(x + width - 4, y - height + 7).rect(2, 2).setColor(renderer.getTheme().WHITE));
            renderer.queue(new Task(x + width - 4, y - height + 4).rect(2, 2).setColor(renderer.getTheme().WHITE));
            renderer.queue(new Task(x + width - 4, y - height + 1).rect(2, 2).setColor(renderer.getTheme().WHITE));
        }
        return false;
    }
}