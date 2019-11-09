package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.fixtures.Group;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.programmer.Programmer;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;

import static dev.therealdan.lights.util.sorting.Sortable.Sort.NAME;

public class GroupsPanel implements Panel {

    private int groupsPerRow = 8;

    public GroupsPanel() {
        register(new CloseIcon());

        setWidth(800);
        setHeight(200);
    }

    @Override
    public boolean drawContent(Renderer renderer, float x, float y, float width, float height, boolean interacted) {
        int groups = Group.count();
        while (groups % getGroupsPerRow() != 0) groups++;

        float groupWidth = width / getGroupsPerRow();
        float groupHeight = height / (groups / getGroupsPerRow());

        for (Group group : Group.groups(NAME)) {
            renderer.box(x, y, groupWidth, groupHeight, Programmer.isSelected(group) ? Lights.color.DARK_RED : Lights.color.MEDIUM, setWidth(renderer, group.getName()), Task.TextPosition.CENTER);
            if (Lights.mouse.contains(x, y, groupWidth, groupHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                    if (Programmer.isSelected(group)) {
                        Programmer.deselect(group);
                    } else {
                        Programmer.select(group);
                    }
                }
            }
            x += groupWidth;

            if (x + groupWidth > getX() + width) {
                x = getX();
                y -= groupHeight;
            }
        }

        return interacted;
    }

    @Override
    public boolean isResizeable() {
        return true;
    }

    public void setGroupsPerRow(int groupsPerRow) {
        this.groupsPerRow = groupsPerRow;
    }

    public int getGroupsPerRow() {
        return groupsPerRow;
    }
}