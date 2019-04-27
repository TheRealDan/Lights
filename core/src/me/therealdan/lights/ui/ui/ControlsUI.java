package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.controllers.Button;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.settings.Control;
import me.therealdan.lights.ui.UIHandler;

import java.util.List;

public class ControlsUI implements UI {

    private Control.Category selectedCategory;
    private Control selectedControl = null;

    public ControlsUI() {

    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.CONTROLS);
        boolean interacted = false;

        float x = getX();
        float y = getY();
        float width = getWidth();
        float cellHeight = 30;

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, setWidth(renderer, "Controls"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, setWidth(renderer, "Category: " + getSelectedCategory().formatString()), Task.TextPosition.LEFT_CENTER);
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                select(true);
            } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && Lights.mouse.rightReady(500)) {
                select(false);
            }
        }
        y -= cellHeight;

        if (getSelectedCategory().equals(Control.Category.BUTTONS)) {
            for (Button button : Button.buttons(Button.SortBy.POSITION)) {
                Control control = Control.byButton(button);
                setWidth(renderer, button.getName(), 2);
                renderer.box(x, y, width / 2, cellHeight, isSelected(control) ? Lights.color.DARK_RED : Lights.color.MEDIUM, button.getName());
                renderer.box(x + width / 2, y, width / 2, cellHeight, isSelected(control) ? Lights.color.DARK_RED : Lights.color.MEDIUM, control.formatKeycode());
                if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
                    interacted = true;
                    if (Lights.mouse.leftClicked(500, getSelectedControl() != null && !control.equals(getSelectedControl()))) {
                        select(isSelected(control) ? null : control);
                    }
                }
                y -= cellHeight;
            }
        } else {
            for (Control control : getSelectedCategory().getControls()) {
                setWidth(renderer, control.getName(), 2);
                renderer.box(x, y, width / 2, cellHeight, isSelected(control) ? Lights.color.DARK_RED : Lights.color.MEDIUM, control.getName());
                renderer.box(x + width / 2, y, width / 2, cellHeight, isSelected(control) ? Lights.color.DARK_RED : Lights.color.MEDIUM, control.formatKeycode());
                if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
                    interacted = true;
                    if (Lights.mouse.leftClicked(500, getSelectedControl() != null && !control.equals(getSelectedControl()))) {
                        select(isSelected(control) ? null : control);
                    }
                }
                y -= cellHeight;
            }
        }

        setHeightBasedOnY(y);
        return interacted;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (selectedControl == null) return true;

        getSelectedControl().setKeycode(keycode);
        return false;
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

    public void select(Control control) {
        this.selectedControl = control;
    }

    public void deselectControl() {
        this.selectedControl = null;
    }

    public boolean isSelected(Control control) {
        return control.equals(getSelectedControl());
    }

    public Control.Category getSelectedCategory() {
        if (selectedCategory == null) selectedCategory = Control.Category.GLOBAL;
        return selectedCategory;
    }

    public Control getSelectedControl() {
        return selectedControl;
    }
}