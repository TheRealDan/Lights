package me.therealdan.lights.programmer;

import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.ui.views.live.ui.PatchUI;

import java.util.List;

public class Task {

    private Fixture fixture;
    private Channel.Type channelType;
    private int parameter;
    private float value;

    public Task(Fixture fixture, Channel.Type channelType, int parameter, float value) {
        this.fixture = fixture;
        this.channelType = channelType;
        this.parameter = parameter;
        this.value = value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public Channel.Type getChannelType() {
        return channelType;
    }

    public int getParameter() {
        return parameter;
    }

    public float getValue() {
        return value;
    }

    public List<Integer> getAddresses() {
        return fixture.getAddresses(channelType, parameter);
    }

    public String getInfo() {
        return getFixture().getName() + " - " + getChannelType().getName() + " " + getParameter() + " = " + (int) getValue();
    }

    @Override
    public String toString() {
        return getFixture().getID() + ";" +
                getChannelType().toString() + ";" +
                getParameter() + ";" +
                getValue();
    }

    public static Task fromString(String string) {
        String[] args = string.split(";");
        return new Task(
                PatchUI.fixtureByID(Integer.parseInt(args[0])),
                Channel.Type.valueOf(args[1]),
                Integer.parseInt(args[2]),
                Float.parseFloat(args[3])
        );
    }
}