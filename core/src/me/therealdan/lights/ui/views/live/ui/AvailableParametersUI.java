package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

import java.util.HashMap;

public class AvailableParametersUI implements UI {

    public AvailableParametersUI() {
        setLocation(400, 20);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.AVAILABLE_PARAMETERS);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Available Parameters"));
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        HashMap<Channel.Type, Integer> parameters = Programmer.getAvailableParameters();
        for (Channel.Type channelType : parameters.keySet()) {
            for (int parameter = 1; parameter <= parameters.get(channelType); parameter++) {
                Util.box(renderer, x, y, width, cellHeight, Programmer.isSelected(channelType, parameter) ? LightsCore.DARK_RED : LightsCore.medium(), setWidth(renderer, channelType.getName() + " " + parameter));
                if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                        if (Programmer.isSelected(channelType, parameter)) {
                            Programmer.deselect(channelType, parameter);
                        } else {
                            Programmer.select(channelType, parameter);
                        }
                    }
                }
                y -= cellHeight;
            }
        }

        setHeightBasedOnY(y);
        return interacted;
    }
}