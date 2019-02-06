package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

public class PanelVisibilityUI implements UI {

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        Live.setSection(Live.Section.PANEL_VISIBILITY);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Panel Visibility"));
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (UI ui : Live.UIs()) {
            if (ui.ignoreVisibilityUI()) continue;
            if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(200))
                    ui.toggleVisibility();
            }
            Util.box(renderer, x, y, width, cellHeight, ui.isVisible() ? LightsCore.DARK_RED : LightsCore.medium(), setWidth(renderer, ui.getName().replace("UI", "")));
            y -= cellHeight;

            if (y - cellHeight < 0) {
                x += width;
                y = getY();
            }
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