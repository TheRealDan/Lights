package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.settings.Setting;
import me.therealdan.lights.ui.Live;
import me.therealdan.lights.util.Util;

public class SettingsUI implements UI {

    public SettingsUI() {
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.SETTINGS);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Settings"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (Setting setting : Setting.settings()) {
            switch (setting.getType()) {
                case LONG:
                    Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), setWidth(renderer, setting.getName() + ": " + setting.getValue()));
                    if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.leftMouseReady(100)) {
                            if (Util.containsMouse(x, y, width / 2, cellHeight)) {
                                setting.increment(1);
                            } else {
                                setting.decrement(1);
                            }
                        }
                    }
                    break;
                case BOOLEAN:
                    Util.box(renderer, x, y, width, cellHeight, setting.isTrue() ? LightsCore.DARK_GREEN : LightsCore.medium(), setWidth(renderer, setting.getName()));
                    if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.leftMouseReady(250))
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