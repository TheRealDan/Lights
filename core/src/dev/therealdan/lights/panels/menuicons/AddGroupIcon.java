package dev.therealdan.lights.panels.menuicons;

import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.fixtures.Group;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.MenuIcon;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.programmer.Programmer;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;

public class AddGroupIcon implements MenuIcon {

    @Override
    public boolean click(Panel panel) {
        Group group = new Group("New Group");
        for (Fixture fixture : Programmer.getSelectedFixtures())
            group.add(fixture);
        Group.add(group);
        return true;
    }

    @Override
    public boolean draw(Renderer renderer, float x, float y, float width, float height, int index, boolean hover, boolean click) {
        float offset = 4;
        renderer.queue(new Task(x + offset, y - height / 2).line(x + width - offset, y - height / 2).setColor(hover ? Lights.theme.GREEN : Lights.theme.WHITE));
        renderer.queue(new Task(x + width / 2, y - offset).line(x + width / 2, y - height + offset).setColor(hover ? Lights.theme.GREEN : Lights.theme.WHITE));
        return false;
    }

    @Override
    public boolean isVisible() {
        return Programmer.countSelectedFixtures() > 0;
    }
}