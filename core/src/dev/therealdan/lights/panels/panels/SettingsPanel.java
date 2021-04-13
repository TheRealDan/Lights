package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.settings.Setting;
import dev.therealdan.lights.store.SettingsStore;

public class SettingsPanel implements Panel {

    private SettingsStore _settingsStore;

    public SettingsPanel(SettingsStore settingsStore) {
        _settingsStore = settingsStore;
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

        for (Setting setting : _settingsStore.getSettings()) {
            switch (setting.getKey().getType()) {
                case LONG:
                    renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, setWidth(renderer, setting.getKey() + ": " + setting.getValue()));
                    if (mouse.within(x, y, width, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(100)) {
                            if (mouse.within(x, y, width / 2, cellHeight)) {
                                setting.increment(1);
                            } else {
                                setting.decrement(1);
                            }
                        }
                    }
                    break;
                case BOOLEAN:
                    renderer.box(x, y, width, cellHeight, setting.isTrue() ? renderer.getTheme().DARK_GREEN : renderer.getTheme().MEDIUM, setWidth(renderer, setting.getKey().getName()));
                    if (mouse.within(x, y, width, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(250))
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