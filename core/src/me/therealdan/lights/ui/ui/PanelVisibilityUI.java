package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.Live;
import me.therealdan.lights.util.Util;

import java.util.List;

public class PanelVisibilityUI implements UI {

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.PANEL_VISIBILITY);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        Util.box(renderer, x, y, width, cellHeight, Lights.DARK_BLUE, setWidth(renderer, "Panel Visibility"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        List<UI> UIs = Live.UIs();
        while (UIs.size() > 0) {
            UI ui = null;
            for (UI each : UIs) {
                if (ui == null || ui.getName().compareTo(each.getName()) > 0) {
                    ui = each;
                }
            }

            UIs.remove(ui);
            if (ui.ignoreVisibilityUI()) continue;
            if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(200))
                    ui.toggleVisibility();
            }
            Util.box(renderer, x, y, width, cellHeight, ui.isVisible() ? Lights.DARK_GREEN : Lights.medium(), setWidth(renderer, ui.getName()));
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        return interacted;
    }

    @Override
    public boolean isVisible() {
        return Gdx.input.isKeyPressed(Input.Keys.TAB);
    }

    @Override
    public boolean ignoreVisibilityUI() {
        return true;
    }
}