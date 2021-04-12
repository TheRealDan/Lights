package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.controllers.Button;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.settings.Control;
import dev.therealdan.lights.settings.ControlsStore;

import static dev.therealdan.lights.util.sorting.Sortable.Sort.POSITION;

public class ControlsPanel implements Panel {

    private ControlsStore _controlsStore;

    private Control.Category selectedCategory;
    private Control selectedControl = null;

    public ControlsPanel(ControlsStore controlsStore) {
        _controlsStore = controlsStore;

        register(new CloseIcon());
    }

    @Override
    public boolean draw(Mouse mouse, Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        boolean interacted = false;

        float x = getX();
        float y = getY();
        float width = getWidth();
        float cellHeight = 30;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().DARK_BLUE, setWidth(renderer, getFriendlyName()), Task.TextPosition.CENTER);
        drag(mouse, x, y, width, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, setWidth(renderer, "Category: " + getSelectedCategory().format()), Task.TextPosition.LEFT_CENTER);
        if (mouse.within(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(500)) {
                select(true);
            } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && mouse.rightReady(500)) {
                select(false);
            }
        }
        y -= cellHeight;

        if (getSelectedCategory().equals(Control.Category.BUTTONS)) {
            for (Button button : Button.buttons(POSITION)) {
                Control control = _controlsStore.getByButton(button);
                setWidth(renderer, button.getName(), 2);
                renderer.box(x, y, width / 2, cellHeight, isSelected(control) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, button.getName());
                renderer.box(x + width / 2, y, width / 2, cellHeight, isSelected(control) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, control.formatKeycode());
                if (mouse.within(x, y, width, cellHeight) && canInteract()) {
                    interacted = true;
                    if (mouse.leftClicked(500, getSelectedControl() != null && !control.equals(getSelectedControl()))) {
                        select(isSelected(control) ? null : control);
                    }
                }
                y -= cellHeight;
            }
        } else {
            for (Control control : _controlsStore.getControls(getSelectedCategory())) {
                setWidth(renderer, control.getName(), 2);
                renderer.box(x, y, width / 2, cellHeight, isSelected(control) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, control.getName());
                renderer.box(x + width / 2, y, width / 2, cellHeight, isSelected(control) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, control.formatKeycode());
                if (mouse.within(x, y, width, cellHeight) && canInteract()) {
                    interacted = true;
                    if (mouse.leftClicked(500, getSelectedControl() != null && !control.equals(getSelectedControl()))) {
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
        for (Control.Category category : Control.Category.values()) {
            if (getSelectedCategory().equals(category)) {
                i += next ? 1 : -1;
                if (i >= Control.Category.values().length) i = 0;
                if (i < 0) i = Control.Category.values().length - 1;
                select(Control.Category.values()[i]);
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