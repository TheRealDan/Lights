package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.PanelHandler;

public class DMXInterfacePanel implements Panel {

    public DMXInterfacePanel() {
        register(new CloseIcon());
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) PanelHandler.setSection(PanelHandler.Section.DMX_INTERFACE);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, setWidth(renderer, getFriendlyName()), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (String port : Lights.output.openPorts()) {
            if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                    if (Lights.output.getActivePort().equals(port)) {
                        Lights.output.disconnect();
                    } else {
                        Lights.output.setActivePort(port);
                    }
                }
            }
            renderer.box(x, y, width, cellHeight, Lights.output.getActivePort().equals(port) ? Lights.color.DARK_RED : Lights.color.MEDIUM, setWidth(renderer, port));
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        return interacted;
    }
}