package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.fixtures.Group;
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.ui.views.Patch;
import me.therealdan.lights.util.Util;

public class GroupsUI implements UI {

    public GroupsUI() {
        setLocation(120, 20);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.GROUPS);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Groups"));
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (Group group : Patch.groups()) {
            Util.box(renderer, x, y, width, cellHeight, Programmer.isSelected(group) ? LightsCore.DARK_RED : LightsCore.medium(), setWidth(renderer, group.getName()));
            if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(200)) {
                    if (Programmer.isSelected(group)) {
                        Programmer.deselect(group);
                    } else {
                        Programmer.select(group);
                    }
                }
            }
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        return interacted;
    }
}