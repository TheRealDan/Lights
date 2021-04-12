package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.interfaces.CustomSerialInterface;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;

public class DMXInterfacePanel implements Panel {

    private CustomSerialInterface _customSerialInterface;

    public DMXInterfacePanel(CustomSerialInterface customSerialInterface) {
        _customSerialInterface = customSerialInterface;

        register(new CloseIcon());
    }

    @Override
    public boolean draw(Mouse mouse, Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        renderer.box(x, y, width, cellHeight, renderer.getTheme().DARK_BLUE, setWidth(renderer, getFriendlyName()), Task.TextPosition.CENTER);
        drag(mouse, x, y, width, cellHeight);
        y -= cellHeight;

        for (String port : _customSerialInterface.openPorts()) {
            if (mouse.within(x, y, width, cellHeight) && canInteract()) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(500)) {
                    if (_customSerialInterface.getActivePort().equals(port)) {
                        _customSerialInterface.disconnect();
                    } else {
                        _customSerialInterface.setActivePort(port);
                    }
                }
            }
            renderer.box(x, y, width, cellHeight, _customSerialInterface.getActivePort().equals(port) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, setWidth(renderer, port));
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        return interacted;
    }
}