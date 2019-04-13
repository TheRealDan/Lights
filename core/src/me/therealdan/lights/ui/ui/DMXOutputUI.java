package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.Lights;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.Live;
import me.therealdan.lights.util.Util;

public class DMXOutputUI implements UI {

    private String dmxToDisplay = "VISUALISER";
    private boolean displayInCells = true;

    public DMXOutputUI() {
        // TODO - Save and load above settings
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.DMX_OUTPUT);
        boolean interacted = false;
        boolean heightSet = false;

        float x = getX();
        float y = getY();
        float width = getWidth();
        float cellHeight = 30;

        if (!displayInCells()) setWidth(0);

        Util.box(renderer, x, y, width, cellHeight, Lights.DARK_BLUE, setWidth(renderer, "DMX Output"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, Lights.medium(), setWidth(renderer, "DMX: " + getDmxToDisplay()));
        if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(500))
                next();
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, displayInCells() ? Lights.DARK_RED : Lights.medium(), setWidth(renderer, "Display In Cells"));
        if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(500))
                toggleDisplayInCells();
        }
        y -= cellHeight;

        DMX dmx = DMX.get(getDmxToDisplay());
        if (displayInCells()) {
            for (int address : dmx.active()) {
                Util.box(renderer, x, y, cellHeight, cellHeight, Lights.medium(), Integer.toString(address));
                drag(x, y, cellHeight, cellHeight);
                float fill = cellHeight * dmx.get(address) / 255f;
                Util.box(renderer, x, y - cellHeight + fill, cellHeight, fill, Lights.DARK_RED);
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
                    Util.box(renderer, x, y, width, cellHeight, Lights.medium(), setWidth(renderer, address + ": " + dmx.get(address)));
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