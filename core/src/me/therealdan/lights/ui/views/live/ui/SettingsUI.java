package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.dmx.Output;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.ui.views.live.Visualiser3D;
import me.therealdan.lights.util.Util;

public class SettingsUI implements UI {

    public SettingsUI() {
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
        UI.super.save();

        FileHandle fileHandle = Gdx.files.local("Lights/Settings/Settings.txt");
        fileHandle.writeString("", false);
        for (Setting setting : Setting.values())
            fileHandle.writeString(setting.toString() + ": " + setting.getValue() + "\r\n", true);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.SETTINGS);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Settings"));
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (Setting setting : Setting.values()) {
            switch (setting.getType()) {
                case INT:
                case LONG:
                    Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), setWidth(renderer, setting.getName() + ": " + setting.getValue()));
                    if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(100)) {
                            if (Util.containsMouse(x, y, width / 2, cellHeight)) {
                                setting.increment();
                            } else {
                                setting.decrement();
                            }
                        }
                    }
                    break;
                case BOOLEAN:
                    Util.box(renderer, x, y, width, cellHeight, setting.getBoolean() ? LightsCore.DARK_GREEN : LightsCore.medium(), setWidth(renderer, setting.getName()));
                    if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(250))
                            setting.toggle();
                    }
                    break;
            }
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        return interacted;
    }

    public enum Setting {
        INTERVAL,
        CONNECTION_WAIT,
        NEW_READ_TIMEOUT,
        NEW_WRITE_TIMEOUT,
        CHANNELS_PER_SEND,
        BUTTON_PANEL_WIDTH,
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

        public Setting.Type getType() {
            switch (this) {
                case INTERVAL:
                case CONNECTION_WAIT:
                    return Setting.Type.LONG;

                case NEW_READ_TIMEOUT:
                case NEW_WRITE_TIMEOUT:
                case CHANNELS_PER_SEND:
                case BUTTON_PANEL_WIDTH:
                    return Setting.Type.INT;

                case SHOW_DMX_SEND_DEBUG:
                case CONTINUOUS:
                case LIMIT_LED_STRIPS:
                case DRAW_DMX:
                case REMEMBER_CAMERA_POSITION:
                    return Setting.Type.BOOLEAN;
            }
            return Setting.Type.BOOLEAN;
        }

        public enum Type {
            BOOLEAN,
            LONG,
            INT
        }
    }
}