package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

import java.text.DecimalFormat;
import java.util.HashMap;

public class ParametersUI implements UI {

    private static ParametersUI parametersUI;

    private static float WIDTH = 500;
    private static float HEIGHT = 690;

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private Channel.Type section = null;
    private HashMap<Channel.Type, Float> level = new HashMap<>();

    public ParametersUI() {
        parametersUI = this;
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        Live.setSection(Live.Section.PARAMETERS);
        boolean interacted = false;

        setWidth(ParametersUI.WIDTH);
        setHeight(ParametersUI.HEIGHT);

        float x = getX();
        float y = getY();
        float width = getWidth() / (Channel.Type.values().length + 1);
        float cellHeight = 30;

        Util.box(renderer, x, y, getWidth(), getHeight(), LightsCore.dark());

        Util.box(renderer, x, y, getWidth(), cellHeight, LightsCore.DARK_BLUE, "Parameters");
        drag(x, y, getWidth(), cellHeight);

        y = getY() - cellHeight;
        x += width;

        float parameterHeight = getHeight() - cellHeight - cellHeight;
        for (Channel.Type channelType : Channel.Type.values()) {
            if (Util.containsMouse(x, y, width, parameterHeight) && canInteract()) setSection(channelType);
            Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, channelType.getName());
            drag(x, y, width, cellHeight);
            y -= cellHeight;

            for (float percentage = 1.0f; percentage >= -0.01f; percentage -= 0.05f) {
                float level = Float.parseFloat(decimalFormat.format(percentage * 100.0));
                Util.box(renderer, x, y, width, cellHeight, isSet(channelType) && getLevel(channelType) == level ? LightsCore.DARK_RED : LightsCore.medium(), Float.toString(level).replace("-", "").replace(".0", "") + "%");
                if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(-1)) {
                        setLevel(channelType, level);
                    }
                }
                y -= cellHeight;
            }

            x += width;
            y = getY() - cellHeight;
        }

        return interacted;
    }

    public void setLevel(Channel.Type channelType, float level) {
        this.level.put(channelType, level);
    }

    public boolean isSet(Channel.Type channelType) {
        return level.containsKey(channelType);
    }

    public float getLevel(Channel.Type channelType) {
        if (!level.containsKey(channelType)) return 0;
        return level.get(channelType);
    }

    public void setSection(Channel.Type section) {
        this.section = section;
    }

    public Channel.Type getSection() {
        return section;
    }
}