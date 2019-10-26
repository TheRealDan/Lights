package dev.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.fixtures.Group;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.programmer.Programmer;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.UIHandler;

import static dev.therealdan.lights.util.sorting.Sortable.Sort.NAME;

public class GroupsUI implements UI {

    public static float WIDTH = 800;
    public static int GROUPS_PER_ROW = 10;

    public GroupsUI() {
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.GROUPS);
        boolean interacted = false;

        setWidth(GroupsUI.WIDTH);

        float cellHeight = 30;
        float cellSize = GroupsUI.WIDTH / GroupsUI.GROUPS_PER_ROW;

        float x = getX();
        float y = getY();
        float width = getWidth();

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, setWidth(renderer, "Groups"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (Group group : Group.groups(NAME)) {
            renderer.box(x, y, cellSize, cellSize, Programmer.isSelected(group) ? Lights.color.DARK_RED : Lights.color.MEDIUM, setWidth(renderer, group.getName()), Task.TextPosition.CENTER);
            if (Lights.mouse.contains(x, y, cellSize, cellSize) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                    if (Programmer.isSelected(group)) {
                        Programmer.deselect(group);
                    } else {
                        Programmer.select(group);
                    }
                }
            }
            x += cellSize;

            if (x + cellSize > getX() + getWidth()) {
                x = getX();
                y -= cellSize;
            }
        }
        y -= cellSize;

        setHeightBasedOnY(y);
        return interacted;
    }
}