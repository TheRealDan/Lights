package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.dmx.DMX;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;

public class DMXOutputPanel implements Panel {

    private String dmxToDisplay = "VISUALISER";
    private boolean displayInCells = true;

    public DMXOutputPanel() {
        register(new CloseIcon());
        // TODO - Save and load above settings
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        boolean interacted = false;
        boolean heightSet = false;

        float x = getX();
        float y = getY();
        float width = getWidth();
        float cellHeight = 30;

        if (!displayInCells()) setWidth(0);

        renderer.box(x, y, width, cellHeight, renderer.getTheme().DARK_BLUE, setWidth(renderer, getFriendlyName()), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, setWidth(renderer, "DMX: " + getDmxToDisplay()));
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500))
                next();
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, displayInCells() ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, setWidth(renderer, "Display In Cells"));
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500))
                toggleDisplayInCells();
        }
        y -= cellHeight;

        DMX dmx = DMX.get(getDmxToDisplay());
        if (displayInCells()) {
            for (int address : dmx.active()) {
                renderer.box(x, y, cellHeight, cellHeight, renderer.getTheme().MEDIUM, Integer.toString(address));
                drag(x, y, cellHeight, cellHeight);
                float fill = cellHeight * dmx.get(address) / 255f;
                renderer.box(x, y - cellHeight + fill, cellHeight, fill, renderer.getTheme().DARK_RED);
                x += cellHeight;

                if (address % 16 == 0) {
                    x = getX();
                    y -= cellHeight;
                }
            }
            setWidth(cellHeight * 16);
            setHeightBasedOnY(y);
        } else {
            int shown = 0;
            for (int address : dmx.active()) {
                if (dmx.get(address) > 0) {
                    shown++;
                    renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, setWidth(renderer, address + ": " + dmx.get(address)));
                    y -= cellHeight;

                    if (shown == 13 || (shown - 13) % 16 == 0) {
                        if (!heightSet) {
                            setHeightBasedOnY(y);
                            heightSet = true;
                        }
                        x += width;
                        y = getY();
                    }
                }
            }
        }

        return interacted;
    }

    private void next() {
        String current = getDmxToDisplay();
        boolean next = false;
        for (String level : DMX.levels()) {
            if (next) {
                dmxToDisplay = level;
                return;
            }
            if (current.equals(level)) next = true;
        }
        dmxToDisplay = DMX.levels().get(0);
    }

    private String getDmxToDisplay() {
        return dmxToDisplay;
    }

    private void toggleDisplayInCells() {
        displayInCells = !displayInCells;
    }

    private boolean displayInCells() {
        return displayInCells;
    }
}