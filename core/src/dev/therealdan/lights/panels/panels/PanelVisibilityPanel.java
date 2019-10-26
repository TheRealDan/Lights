package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.ui.UIHandler;

import java.util.List;

public class PanelVisibilityPanel implements Panel {

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.PANEL_VISIBILITY);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, setWidth(renderer, "Panel Visibility"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        List<Panel> panels = UIHandler.UIs();
        while (panels.size() > 0) {
            Panel panel = null;
            for (Panel each : panels) {
                if (panel == null || panel.getName().compareTo(each.getName()) > 0) {
                    panel = each;
                }
            }

            panels.remove(panel);
            if (panel.ignoreVisibilityUI()) continue;
            if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(200))
                    panel.toggleVisibility();
            }
            renderer.box(x, y, width, cellHeight, panel.isVisible() ? Lights.color.DARK_GREEN : Lights.color.MEDIUM, setWidth(renderer, panel.getName()));
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