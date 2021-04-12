package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.dmx.DMX;
import dev.therealdan.lights.dmx.Output;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.settings.SettingsStore;

public class DMXOutputPanel implements Panel {

    private SettingsStore _settingsStore;
    private Output _output;

    private DMX _dmx;
    private boolean displayInCells = true;

    public DMXOutputPanel(SettingsStore settingsStore, Output output) {
        _settingsStore = settingsStore;
        _output = output;

        register(new CloseIcon());
        // TODO - Save and load above settings

        _dmx = output.getDMX().get(0);
    }

    @Override
    public boolean draw(Mouse mouse, Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        boolean interacted = false;
        boolean heightSet = false;

        float x = getX();
        float y = getY();
        float width = getWidth();
        float cellHeight = 30;

        if (!displayInCells()) setWidth(0);

        renderer.box(x, y, width, cellHeight, renderer.getTheme().DARK_BLUE, setWidth(renderer, getFriendlyName()), Task.TextPosition.CENTER);
        drag(mouse, x, y, width, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, setWidth(renderer, "DMX: " + getDMX().getLevel()));
        if (mouse.within(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(500))
                next();
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, displayInCells() ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, setWidth(renderer, "Display In Cells"));
        if (mouse.within(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(500))
                toggleDisplayInCells();
        }
        y -= cellHeight;

        if (displayInCells()) {
            for (int address : getDMX().active()) {
                renderer.box(x, y, cellHeight, cellHeight, renderer.getTheme().MEDIUM, Integer.toString(address));
                drag(mouse, x, y, cellHeight, cellHeight);
                float fill = cellHeight * getDMX().get(address) / 255f;
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
            for (int address : getDMX().active()) {
                if (getDMX().get(address) > 0) {
                    shown++;
                    renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, setWidth(renderer, address + ": " + _dmx.get(address)));
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
        boolean next = false;
        for (DMX dmx : _output.getDMX()) {
            if (next) {
                _dmx = dmx;
                return;
            }
            if (getDMX().getLevel().equals(dmx.getLevel())) next = true;
        }
        _dmx = _output.getDMX().get(0);
    }

    private DMX getDMX() {
        return _dmx;
    }

    private void toggleDisplayInCells() {
        displayInCells = !displayInCells;
    }

    private boolean displayInCells() {
        return displayInCells;
    }
}