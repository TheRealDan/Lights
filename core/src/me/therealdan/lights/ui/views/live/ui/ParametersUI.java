package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

import java.text.DecimalFormat;

public class ParametersUI implements UI {

    private static ParametersUI parametersUI;

    private static float WIDTH = 500;
    private static float HEIGHT = 690;

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private int page = 1;
    private Channel.Category category;
    private Channel.Type channelType = null;

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
        float width = getWidth() / 4;
        float cellHeight = 30;

        Util.box(renderer, x, y, getWidth(), getHeight(), LightsCore.dark());
        Util.box(renderer, x, y, getWidth(), cellHeight, LightsCore.DARK_BLUE, "Parameters");
        drag(x, y, getWidth(), cellHeight);
        y -= cellHeight;

        for (Channel.Category category : Programmer.availableChannelTypeCategories()) {
            Util.box(renderer, x, y, width, cellHeight, category.equals(getCategory()) ? LightsCore.DARK_GREEN : LightsCore.medium(), category.getName());
            if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                    if (category.equals(getCategory())) {
                        setPage(getPage() + 1);
                    } else {
                        setCategory(category);
                    }
                }
            }
            y -= cellHeight;
        }

        y = getY() - cellHeight;
        x += width;

        int seen = 0;
        int skipped = 0;
        float parameterHeight = getHeight() - cellHeight - cellHeight;
        for (Channel.Type channelType : Programmer.availableChannelTypes()) {
            if (seen >= 3) break;
            if (!channelType.getCategory().equals(getCategory())) continue;
            for (int parameter : Programmer.availableParameters(channelType)) {
                if (seen >= 3) break;
                if (skipped < getPage() * 3 - 3) {
                    skipped++;
                    continue;
                }
                seen++;
                if (Util.containsMouse(x, y, width, parameterHeight) && canInteract()) setChannelType(channelType);
                Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, channelType.getName());
                drag(x, y, width, cellHeight);
                y -= cellHeight;

                for (float percentage = 1.0f; percentage >= -0.01f; percentage -= 0.05f) {
                    float level = Float.parseFloat(decimalFormat.format(percentage * 100.0));
                    Util.box(renderer, x, y, width, cellHeight, isSet(channelType, parameter) && getLevel(channelType, parameter) == level ? LightsCore.DARK_RED : LightsCore.medium(), Float.toString(level).replace("-", "").replace(".0", "") + "%");
                    if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(-1)) {
                            setValue(channelType, parameter, percentage * 255.0f);
                        }
                    }
                    y -= cellHeight;
                }

                x += width;
                y = getY() - cellHeight;
            }
        }
        if (seen == 0) setPage(1);

        return interacted;
    }

    public void setValue(Channel.Type channelType, int parameter, float value) {
        for (Fixture fixture : Programmer.getSelectedFixtures()) {
            Programmer.set(fixture, channelType, value, parameter);
        }
    }

    public boolean isSet(Channel.Type channelType, int parameter) {
        for (Fixture fixture : Programmer.getSelectedFixtures()) {
            if (Programmer.selectedFramesHaveValueFor(fixture, channelType, parameter)) {
                return true;
            }
        }
        return false;
    }

    public float getLevel(Channel.Type channelType, int parameter) {
        float value = getValue(channelType, parameter);
        float percentage = value / 255.0f;
        return Float.parseFloat(decimalFormat.format(percentage * 100.0));
    }

    public float getValue(Channel.Type channelType, int parameter) {
        for (Fixture fixture : Programmer.getSelectedFixtures()) {
            if (Programmer.selectedFramesHaveValueFor(fixture, channelType, parameter)) {
                return Programmer.getSelectedFramesValueFor(fixture, channelType, parameter);
            }
        }
        return 0;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public void setCategory(Channel.Category category) {
        this.category = category;
    }

    public Channel.Category getCategory() {
        return category;
    }

    public void setChannelType(Channel.Type channelType) {
        this.channelType = channelType;
    }

    public Channel.Type getChannelType() {
        return channelType;
    }
}