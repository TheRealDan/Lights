package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.UIHandler;

public class DMXInterfaceUI implements UI {

    public DMXInterfaceUI() {
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.DMX_INTERFACE);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, setWidth(renderer, "DMX Interface"), Task.TextPosition.CENTER);
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