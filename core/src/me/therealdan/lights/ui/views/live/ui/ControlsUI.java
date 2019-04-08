package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.settings.Control;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

import java.util.List;

public class ControlsUI implements UI {

    // TODO - Implement way to change key bindings

    private Control.Category selectedCategory;

    public ControlsUI() {

    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        Live.setSection(Live.Section.CONTROLS);
        boolean interacted = false;

        float x = getX();
        float y = getY();
        float width = getWidth();
        float cellHeight = 30;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Controls"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), setWidth(renderer, "Category: " + getSelectedCategory().formatString()), Task.TextPosition.LEFT_CENTER);
        if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.leftMouseReady(500)) {
                select(true);
            }
        }
        y -= cellHeight;

        for (Control control : getSelectedCategory().getControls()) {
            setWidth(renderer, control.getName().formatString(), 2);
            Util.box(renderer, x, y, width / 2, cellHeight, LightsCore.medium(), control.getName().formatString());
            Util.box(renderer, x + width / 2, y, width / 2, cellHeight, LightsCore.medium(), control.formatKeycode());
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        return interacted;
    }

    public void select(boolean next) {
        int i = 0;
        List<Control.Category> categories = Control.categories();
        for (Control.Category category : categories) {
            if (getSelectedCategory().equals(category)) {
                i += next ? 1 : -1;
                if (i >= categories.size()) i = 0;
                if (i < 0) i = categories.size() - 1;
                select(categories.get(i));
                return;
            }
            i++;
        }
    }

    public void select(Control.Category category) {
        this.selectedCategory = category;
    }

    public Control.Category getSelectedCategory() {
        if (selectedCategory == null) selectedCategory = Control.Category.GLOBAL;
        return selectedCategory;
    }
}