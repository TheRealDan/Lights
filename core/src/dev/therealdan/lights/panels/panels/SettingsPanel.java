package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.settings.Setting;
import dev.therealdan.lights.ui.PanelHandler;

public class SettingsPanel implements Panel {

    public SettingsPanel() {
        register(new CloseIcon());
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) PanelHandler.setSection(PanelHandler.Section.SETTINGS);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, setWidth(renderer, getFriendlyName()), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (Setting setting : Setting.settings()) {
            switch (setting.getType()) {
                case LONG:
                    renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, setWidth(renderer, setting.getName() + ": " + setting.getValue()));
                    if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(100)) {
                            if (Lights.mouse.contains(x, y, width / 2, cellHeight)) {
                                setting.increment(1);
                            } else {
                                setting.decrement(1);
                            }
                        }
                    }
                    break;
                case BOOLEAN:
                    renderer.box(x, y, width, cellHeight, setting.isTrue() ? Lights.color.DARK_GREEN : Lights.color.MEDIUM, setWidth(renderer, setting.getName()));
                    if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(250))
                            setting.toggle();
                    }
                    break;
            }
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        return interacted;
    }
}