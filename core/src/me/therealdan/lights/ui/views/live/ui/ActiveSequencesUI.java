package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

public class ActiveSequencesUI implements UI {

    public ActiveSequencesUI() {
        setLocation(560, 20);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.ACTIVE_SEQUENCES);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Active Sequences"));
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (int priority = 0; priority <= Live.getTopPriority(); priority++) {
            Sequence sequence = Live.getSequence(priority);
            if (sequence == null) continue;
            float priorityWidth = renderer.getWidth(Integer.toString(priority)) + 10;
            Util.box(renderer, x, y, priorityWidth, cellHeight, LightsCore.medium(), Integer.toString(priority));
            if (Util.containsMouse(x, y, priorityWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                    Live.clearSequence(priority);
                }
            }
            Util.box(renderer, x + priorityWidth, y, width - priorityWidth, cellHeight, LightsCore.medium(), sequence.getName());
            if (Util.containsMouse(x, y, width - priorityWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                    Live.clearSequence(priority);
                }
            }
            y -= cellHeight;

            if (width - priorityWidth < renderer.getWidth(sequence.getName()) + 10)
                setWidth(width + 1);
        }

        setHeight(getY() - y);
        return interacted;
    }
}