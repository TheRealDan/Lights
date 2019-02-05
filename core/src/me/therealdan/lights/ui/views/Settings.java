package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.dmx.Output;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.ui.views.live.Visualiser3D;
import me.therealdan.lights.ui.views.live.ui.ButtonsUI;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Settings implements Tab {

    private static Settings settingsInstance;

    private float dmxInterfaceWidth = 200;
    private float settingWidth = 200;
    private float checkboxSize = 15;

    private List<String> debugInfo = new ArrayList<>();

    public Settings() {
        settingsInstance = this;

        FileHandle fileHandle = Gdx.files.local("Lights/Settings/Settings.txt");
        if (fileHandle.exists()) {
            for (String line : fileHandle.readString().split("\\r?\\n")) {
                String[] args = line.split(": ");
                Setting.valueOf(args[0]).set(args[1]);
            }
        }
    }

    @Override
    public void save() {
        FileHandle fileHandle = Gdx.files.local("Lights/Settings/Settings.txt");
        fileHandle.writeString("", false);
        for (Setting setting : Setting.values()) {
            fileHandle.writeString(setting.toString() + ": " + setting.getValue() + "\r\n", true);
        }
    }

    @Override
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        float x = X + LightsCore.edge();
        float y = Y + HEIGHT - LightsCore.edge();

        float height = HEIGHT - (LightsCore.edge() * 2f);

        // DMX Interface
        dmxInterface(renderer, x, y, dmxInterfaceWidth);

        // Divider
        x += dmxInterfaceWidth + LightsCore.edge();
        divider(renderer, x, y, height);

        // Settings
        x += LightsCore.edge();
        settings(renderer, x, y, settingWidth);

        // Divider
        x += settingWidth + LightsCore.edge();
        divider(renderer, x, y, height);

        // Debug Info
        x += LightsCore.edge();
        debugInfo(renderer, x, y);
    }

    private void dmxInterface(Renderer renderer, float x, float y, float width) {
        String connected = "Connected: " + Output.getActivePort();
        float stringWidth = renderer.getWidth(connected);
        if (stringWidth > dmxInterfaceWidth) dmxInterfaceWidth = stringWidth;
        renderer.queue(new Task(x, y).text(connected).setColor(LightsCore.text()));
        y -= 20;
        renderer.queue(new Task(x, y).text("BaudRate: " + Output.BAUDRATE).setColor(LightsCore.text()));
        y -= 20;
        if (Output.isConnected()) {
            renderer.queue(new Task(x, y).text("Disconnect").setColor(LightsCore.RED));
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, 20))
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                    Output.disconnect();
        }
        y -= 20;
        y -= 20;
        renderer.queue(new Task(x, y).text("Ports:").setColor(LightsCore.text()));
        y -= 20;
        for (String port : Output.openPorts()) {
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, 20)) {
                renderer.queue(new Task(LightsCore.BLUE));
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                    Output.setActivePort(port);
            } else {
                renderer.queue(new Task(LightsCore.text()));
            }
            renderer.queue(new Task(x, y).text(port));
            y -= 20;
        }
    }

    private void settings(Renderer renderer, float x, float y, float width) {
        for (Setting setting : Setting.values()) {
            switch (setting.getType()) {
                case INT:
                case LONG:
                    y -= checkboxSize;
                    if (Util.containsMouse(x, Gdx.graphics.getHeight() - y - checkboxSize, checkboxSize, checkboxSize))
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(400))
                            setting.decrement();
                    if (Util.containsMouse(x + checkboxSize, Gdx.graphics.getHeight() - y - checkboxSize, checkboxSize, checkboxSize))
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(400))
                            setting.increment();
                    renderer.queue(new Task(x, y).rect(checkboxSize, checkboxSize).setColor(LightsCore.medium()));
                    renderer.queue(new Task(x + checkboxSize, y).rect(checkboxSize, checkboxSize).setColor(LightsCore.medium()));
                    renderer.queue(new Task(x, y).rectOutline(checkboxSize * 2, checkboxSize).setColor(LightsCore.light()));
                    renderer.queue(new Task(x + 8, y + checkboxSize - 1).text("- +"));
                    renderer.queue(new Task(x + checkboxSize * 3 - checkboxSize / 2, y + (checkboxSize / 2) - 3).text(setting.getName() + ": " + (setting.getType().equals(Setting.Type.INT) ? setting.getInt() : setting.getLong()), Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));
                    y -= 12;
                    break;
                case BOOLEAN:
                    y -= checkboxSize;
                    if (Util.containsMouse(x, Gdx.graphics.getHeight() - y - checkboxSize, checkboxSize, checkboxSize))
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(400))
                            setting.toggle();
                    renderer.queue(new Task(x, y).rect(checkboxSize, checkboxSize).setColor(setting.getBoolean() ? LightsCore.light() : LightsCore.medium()));
                    renderer.queue(new Task(x, y).rectOutline(checkboxSize, checkboxSize).setColor(LightsCore.light()));
                    renderer.queue(new Task(x + checkboxSize + checkboxSize / 2, y + (checkboxSize / 2) - 3).text(setting.getName(), Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));
                    y -= 10;
                    break;
            }
        }
    }

    private void debugInfo(Renderer renderer, float x, float y) {
        for (String line : debugInfo) {
            renderer.queue(new Task(x, y).text(line).setColor(LightsCore.text()));
            y -= 20;
        }
    }

    private void divider(Renderer renderer, float x, float y, float height) {
        renderer.queue(new Task(x, y).line(x, y - height).setColor(LightsCore.light()));
    }

    public static void add(String info) {
        settingsInstance.debugInfo.add(info);
    }

    public static void set(String info, int index) {
        while (settingsInstance.debugInfo.size() <= index) {
            add("");
        }

        settingsInstance.debugInfo.set(index, info);
    }

    public enum Setting {
        INTERVAL,
        CONNECTION_WAIT,
        NEW_READ_TIMEOUT,
        NEW_WRITE_TIMEOUT,
        CHANNELS_PER_SEND,
        BUTTON_PANEL_WIDTH,
        BUTTON_PANEL_HEIGHT,
        SHOW_DMX_SEND_DEBUG,
        CONTINUOUS,
        LIMIT_LED_STRIPS,
        DRAW_DMX,
        REMEMBER_CAMERA_POSITION;

        public void set(String string) {
            switch (getType()) {
                case BOOLEAN:
                    setBoolean(Boolean.parseBoolean(string));
                    return;
                case LONG:
                    setLong(Long.parseLong(string));
                    return;
                case INT:
                    setInt(Integer.parseInt(string));
                    return;
            }
        }

        public void increment() {
            increment(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 10 : 1);
        }

        public void increment(long amount) {
            switch (this) {
                case INTERVAL:
                    Output.INTERVAL += amount;
                    break;
                case CONNECTION_WAIT:
                    Output.CONNECTION_WAIT += amount;
                    break;
                case NEW_READ_TIMEOUT:
                    Output.NEW_READ_TIMEOUT += amount;
                    break;
                case NEW_WRITE_TIMEOUT:
                    Output.NEW_WRITE_TIMEOUT += amount;
                    break;
                case CHANNELS_PER_SEND:
                    Output.CHANNELS_PER_SEND += amount;
                    break;
                case BUTTON_PANEL_WIDTH:
                    ButtonsUI.WIDTH += amount;
                    break;
                case BUTTON_PANEL_HEIGHT:
                    ButtonsUI.HEIGHT += amount;
                    break;
            }
        }

        public void decrement() {
            decrement(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 10 : 1);
        }

        public void decrement(long amount) {
            switch (this) {
                case INTERVAL:
                    Output.INTERVAL -= amount;
                    break;
                case CONNECTION_WAIT:
                    Output.CONNECTION_WAIT -= amount;
                    break;
                case NEW_READ_TIMEOUT:
                    Output.NEW_READ_TIMEOUT -= amount;
                    break;
                case NEW_WRITE_TIMEOUT:
                    Output.NEW_WRITE_TIMEOUT -= amount;
                    break;
                case CHANNELS_PER_SEND:
                    Output.CHANNELS_PER_SEND -= amount;
                    break;
                case BUTTON_PANEL_WIDTH:
                    ButtonsUI.WIDTH -= amount;
                    break;
                case BUTTON_PANEL_HEIGHT:
                    ButtonsUI.HEIGHT -= amount;
                    break;
            }
        }

        public void setBoolean(boolean bool) {
            if (getBoolean() != bool) toggle();
        }

        public void setLong(long value) {
            if (value > getLong()) {
                increment(value - getLong());
            } else if (value < getLong()) {
                decrement(getLong() - value);
            }
        }

        public void setInt(long value) {
            if (value > getLong()) {
                increment(value - getInt());
            } else if (value < getLong()) {
                decrement(getInt() - value);
            }
        }

        public void toggle() {
            switch (this) {
                case SHOW_DMX_SEND_DEBUG:
                    Output.SHOW_DMX_SEND_DEBUG = !Output.SHOW_DMX_SEND_DEBUG;
                    break;
                case CONTINUOUS:
                    Output.CONTINUOUS = !Output.CONTINUOUS;
                    break;
                case LIMIT_LED_STRIPS:
                    DMX.LIMIT_LED_STRIPS = !DMX.LIMIT_LED_STRIPS;
                    break;
                case DRAW_DMX:
                    DMX.DRAW_DMX = !DMX.DRAW_DMX;
                    break;
                case REMEMBER_CAMERA_POSITION:
                    Visualiser3D.REMEMBER_CAMERA_POSITION = !Visualiser3D.REMEMBER_CAMERA_POSITION;
                    break;
            }
        }

        public boolean getBoolean() {
            switch (this) {
                case SHOW_DMX_SEND_DEBUG:
                    return Output.SHOW_DMX_SEND_DEBUG;
                case CONTINUOUS:
                    return Output.CONTINUOUS;
                case LIMIT_LED_STRIPS:
                    return DMX.LIMIT_LED_STRIPS;
                case DRAW_DMX:
                    return DMX.DRAW_DMX;
                case REMEMBER_CAMERA_POSITION:
                    return Visualiser3D.REMEMBER_CAMERA_POSITION;
            }
            return false;
        }

        public long getInt() {
            switch (this) {
                case NEW_READ_TIMEOUT:
                    return Output.NEW_READ_TIMEOUT;
                case NEW_WRITE_TIMEOUT:
                    return Output.NEW_WRITE_TIMEOUT;
                case CHANNELS_PER_SEND:
                    return Output.CHANNELS_PER_SEND;
                case BUTTON_PANEL_WIDTH:
                    return (int) ButtonsUI.WIDTH;
                case BUTTON_PANEL_HEIGHT:
                    return (int) ButtonsUI.HEIGHT;
            }
            return 0L;
        }

        public long getLong() {
            switch (this) {
                case INTERVAL:
                    return Output.INTERVAL;
                case CONNECTION_WAIT:
                    return Output.CONNECTION_WAIT;
            }
            return 0L;
        }

        public String getValue() {
            switch (getType()) {
                case INT:
                    return "" + getInt();
                case LONG:
                    return "" + getLong();
                case BOOLEAN:
                    return "" + getBoolean();
            }
            return null;
        }

        public String getName() {
            return this.toString().substring(0, 1) + this.toString().substring(1).toLowerCase().replace("_", " ");
        }

        public Type getType() {
            switch (this) {
                case INTERVAL:
                case CONNECTION_WAIT:
                    return Type.LONG;

                case NEW_READ_TIMEOUT:
                case NEW_WRITE_TIMEOUT:
                case CHANNELS_PER_SEND:
                case BUTTON_PANEL_WIDTH:
                case BUTTON_PANEL_HEIGHT:
                    return Type.INT;

                case SHOW_DMX_SEND_DEBUG:
                case CONTINUOUS:
                case LIMIT_LED_STRIPS:
                case DRAW_DMX:
                case REMEMBER_CAMERA_POSITION:
                    return Type.BOOLEAN;
            }
            return Type.BOOLEAN;
        }

        public enum Type {
            BOOLEAN,
            LONG,
            INT
        }
    }
}