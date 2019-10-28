package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.programmer.Sequence;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.ui.UIHandler;

public class ActiveSequencesPanel implements Panel {

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.ACTIVE_SEQUENCES);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, setWidth(renderer, getFriendlyName()), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (int priority = 0; priority <= UIHandler.getTopPriority(); priority++) {
            Sequence sequence = UIHandler.getSequence(priority);
            if (sequence == null) continue;
            float priorityWidth = renderer.getWidth(Integer.toString(priority)) + 10;
            renderer.box(x, y, priorityWidth, cellHeight, Lights.color.MEDIUM, Integer.toString(priority));
            if (Lights.mouse.contains(x, y, priorityWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                    UIHandler.clearSequence(priority);
                }
            }
            renderer.box(x + priorityWidth, y, width - priorityWidth, cellHeight, Lights.color.MEDIUM, sequence.getName());
            if (Lights.mouse.contains(x, y, width - priorityWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                    UIHandler.clearSequence(priority);
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