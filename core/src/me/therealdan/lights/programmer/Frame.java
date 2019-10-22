package me.therealdan.lights.programmer;

import com.badlogic.gdx.graphics.Color;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.fixtures.fixture.profile.Channel;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.renderer.Renderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class Frame {

    private LinkedHashSet<Task> tasks = new LinkedHashSet<>();

    private long frameTime = 500;
    private long fadeTime = 0;

    public Frame() {

    }

    public Frame(long frameTime, long fadeTime) {
        this.frameTime = frameTime;
        this.fadeTime = fadeTime;
    }

    public void draw(Renderer renderer, float x, float y, float width, float height, Color color) {
        List<Integer> values = new ArrayList<>();
        for (int address = 1; address <= DMX.MAX_CHANNELS; address++)
            values.add((int) getValueFor(address));
        DMX.draw(renderer, x, y, width, height, color, values);
    }

    public void set(Fixture fixture, Channel.Type channelType, float value, int... parameters) {
        for (int parameter : parameters) {
            Task task = new Task(fixture, channelType, parameter, value);

            for (Task each : tasks()) {
                if (task.getFixture().equals(each.getFixture()) &&
                        task.getChannelType().equals(each.getChannelType()) &&
                        task.getParameter() == each.getParameter()) {
                    tasks.remove(each);
                }
            }

            tasks.add(task);
        }
    }

    public void clear(Fixture fixture) {
        for (Task task : tasks()) {
            if (task.getFixture().equals(fixture)) {
                tasks.remove(task);
            }
        }
    }

    public void clear(Fixture fixture, Channel.Type channelType) {
        for (Task task : tasks()) {
            if (task.getFixture().equals(fixture) && task.getChannelType().equals(channelType)) {
                tasks.remove(task);
            }
        }
    }

    public void clear(Fixture fixture, Channel.Type channelType, int parameter) {
        for (Task task : tasks()) {
            if (task.getFixture().equals(fixture) && task.getChannelType().equals(channelType) && task.getParameter() == parameter) {
                tasks.remove(task);
            }
        }
    }

    public void clear() {
        tasks.clear();
    }

    public void setFrameTime(long frameTime) {
        this.frameTime = frameTime;
    }

    public void setFadeTime(long fadeTime) {
        this.fadeTime = fadeTime;
    }

    public long getFrameTime() {
        return frameTime;
    }

    public long getFadeTime() {
        return fadeTime;
    }

    public float getValueFor(Fixture fixture, Channel.Type channelType, int parameter) {
        for (Task task : tasks())
            if (task.getFixture().equals(fixture))
                if (task.getChannelType().equals(channelType))
                    if (task.getParameter() == parameter)
                        return task.getValue();

        return 0;
    }

    public float getValueFor(int address) {
        float value = 255f;
        boolean exists = false;

        for (Task task : tasks()) {
            if (task.getAddresses().contains(address)) {
                value *= task.getValue() / 255f;
                exists = true;
            }
        }

        if (!exists) return 0;
        return value;
    }

    public boolean hasValueFor(int address) {
        for (Task task : tasks())
            if (task.getAddresses().contains(address))
                return true;

        return false;
    }

    public boolean hasValueFor(Fixture fixture, Channel.Type channelType, int parameter) {
        for (Task task : tasks())
            if (task.getFixture().equals(fixture))
                if (task.getChannelType().equals(channelType))
                    if (task.getParameter() == parameter)
                        return true;

        return false;
    }

    public String getInfo() {
        StringBuilder info = new StringBuilder();

        int tasks = tasks().size();
        HashSet<Fixture> fixtures = new HashSet<>();
        HashSet<Channel.Type> channelTypes = new HashSet<>();
        HashSet<String> parameters = new HashSet<>();
        for (Task task : tasks()) {
            fixtures.add(task.getFixture());
            channelTypes.add(task.getChannelType());
            parameters.add(task.getChannelType().toString() + "-" + task.getParameter());
        }

        info.append(get("task", tasks)).append("- ");
        info.append(get("fixture", fixtures.size())).append("- ");
        info.append(get("channel type", channelTypes.size())).append("- ");
        info.append(get("parameter", parameters.size()));

        return info.toString();
    }

    public List<Task> tasks() {
        return new ArrayList<>(tasks);
    }

    public boolean equals(Frame frame) {
        if (frameTime != frame.frameTime) return false;
        if (fadeTime != frame.fadeTime) return false;

        for (Task task : tasks()) {
            boolean foundMatch = false;
            for (Task otherTask : frame.tasks()) {
                if (task.equals(otherTask)) {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) return false;
        }

        for (Task task : frame.tasks()) {
            boolean foundMatch = false;
            for (Task otherTask : tasks()) {
                if (task.equals(otherTask)) {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) return false;
        }

        return true;
    }

    @Override
    public Frame clone() {
        Frame frame = new Frame(getFrameTime(), getFadeTime());
        for (Task task : tasks())
            frame.set(task.getFixture(), task.getChannelType(), task.getValue(), task.getParameter());
        return frame;
    }

    private static String get(String name, int total) {
        if (total == 0) return "";
        return total == 1 ? "1 " + name + " " : total + " " + name + "s ";
    }

    public static String format(long milliseconds) {
        long minutes = milliseconds / 1000 / 60;
        long seconds = milliseconds / 1000 % 60;
        milliseconds = milliseconds % 1000;

        StringBuilder stringBuilder = new StringBuilder();

        if (minutes > 0) stringBuilder.append(minutes > 9 ? minutes : "0" + minutes).append(":");

        if (seconds > 0) stringBuilder.append(seconds > 9 ? seconds : "0" + seconds).append(":");
        if (seconds == 0) stringBuilder.append("00").append(":");

        if (milliseconds > 9) {
            stringBuilder.append(milliseconds < 100 ? "0" + milliseconds / 10 : milliseconds / 10);
        } else {
            stringBuilder.append("00");
        }

        return stringBuilder.toString();
    }
}