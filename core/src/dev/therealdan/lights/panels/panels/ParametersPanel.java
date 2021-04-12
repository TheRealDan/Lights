package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.fixtures.fixture.profile.Channel;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.programmer.Programmer;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;

import java.text.DecimalFormat;

public class ParametersPanel implements Panel {

    private static ParametersPanel parametersUI;

    private static float WIDTH = 500;
    private static float HEIGHT = 690;

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private int page = 1;
    private Channel.Category category;
    private Channel.Type channelType = null;

    public ParametersPanel() {
        parametersUI = this;

        register(new CloseIcon());
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        boolean interacted = false;

        setWidth(ParametersPanel.WIDTH);
        setHeight(ParametersPanel.HEIGHT);

        float x = getX();
        float y = getY();
        float width = getWidth() / 4;
        float cellHeight = 30;

        renderer.box(x, y, getWidth(), getHeight(), Lights.theme.DARK);
        renderer.box(x, y, getWidth(), cellHeight, Lights.theme.DARK_BLUE, getFriendlyName(), Task.TextPosition.CENTER);
        drag(x, y, getWidth(), cellHeight);
        y -= cellHeight;

        for (Channel.Category category : Programmer.availableChannelTypeCategories()) {
            renderer.box(x, y, width, cellHeight, category.equals(getCategory()) ? Lights.theme.DARK_GREEN : Lights.theme.MEDIUM, category.getName(), Task.TextPosition.CENTER);
            if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
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
                if (Lights.mouse.contains(x, y, width, parameterHeight) && canInteract()) setChannelType(channelType);
                renderer.box(x, y, width, cellHeight, Lights.theme.DARK_BLUE, channelType.getName(), Task.TextPosition.CENTER);
                drag(x, y, width, cellHeight);
                y -= cellHeight;

                for (float percentage = 1.0f; percentage >= -0.01f; percentage -= 0.05f) {
                    float level = Float.parseFloat(decimalFormat.format(percentage * 100.0));
                    renderer.box(x, y, width, cellHeight, isSet(channelType, parameter) && getLevel(channelType, parameter) == level ? Lights.theme.DARK_RED : Lights.theme.MEDIUM, Float.toString(level).replace("-", "").replace(".0", "") + "%", Task.TextPosition.CENTER);
                    if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
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