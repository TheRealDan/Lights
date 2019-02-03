package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.programmer.Frame;
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

import java.util.List;

public class SelectedChannelsUI implements UI {

    public SelectedChannelsUI() {
        setLocation(200, 1100);

        setHeight(250);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.SELECTED_CHANNELS);
        boolean interacted = false;

        List<Channel.Type> channelTypes = Programmer.getSelectedChannelTypes();
        if (channelTypes.size() == 0) return interacted;

        Frame frame = SequenceProgrammerUI.getInstance().getSelectedFrame();
        if (frame == null) return interacted;
        List<Frame> frames = SequenceProgrammerUI.getInstance().getSelectedFrames();

        float cellHeight = 30;

        float x = getX();
        float y = Gdx.graphics.getHeight() - getY();
        float width = 0;
        float height = getHeight() - cellHeight;

        for (Channel.Type channelType : channelTypes) {
            width = Math.max(width, renderer.getWidth(channelType.getName()) + 10);
            Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, channelType.getName());
            y -= cellHeight;

            float percentage = 0;
            for (Fixture fixture : Programmer.getSelectedFixtures())
                for (int parameter : Programmer.getSelectedParameters(channelType))
                    percentage = Math.max(percentage, frame.getValue(fixture, channelType, parameter) / 255f);

            Util.box(renderer, x, y, width, height, LightsCore.medium(), Util.getPercentage(percentage));
            float fill = (percentage * height);
            Util.box(renderer, x, y - height + fill, width, fill, LightsCore.DARK_RED);

            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, height) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                interacted = true;
                float bottom = y - height + 15;
                float value = Math.min(Math.max((Gdx.graphics.getHeight() - Gdx.input.getY() - bottom) / (y - 15 - bottom), 0), 1) * 255f;
                for (Fixture fixture : Programmer.getSelectedFixtures())
                    for (int parameter : Programmer.getSelectedParameters(channelType))
                        for (Frame each : frames)
                            each.set(fixture, channelType, value, parameter);
            }

            y = Gdx.graphics.getHeight() - getY();
            x += width;
        }

        setWidth(x - getX());
        return interacted;
    }
}