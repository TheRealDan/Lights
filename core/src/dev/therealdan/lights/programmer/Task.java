package dev.therealdan.lights.programmer;

import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.fixtures.fixture.profile.Channel;
import dev.therealdan.lights.store.FixturesStore;

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

    public boolean equals(Task task) {
        return equals(task, false);
    }

    public boolean equals(Task task, boolean ignoreValue) {
        if (task.fixture.getID() != fixture.getID()) return false;
        if (!task.channelType.getName().equals(channelType.getName())) return false;
        if (task.parameter != parameter) return false;

        if (!ignoreValue)
            if (task.value != value) return false;

        return true;
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
        FixturesStore fixturesStore = null; // todo review and implement
        String[] args = string.split(";");
        return new Task(
                fixturesStore.getFixtureByID(Integer.parseInt(args[0])),
                Channel.Type.valueOf(args[1]),
                Integer.parseInt(args[2]),
                Float.parseFloat(args[3])
        );
    }

    public Task clone() {
        return new Task(fixture, channelType, parameter, value);
    }
}