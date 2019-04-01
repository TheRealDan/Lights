package me.therealdan.lights.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Setting {

    private static LinkedHashMap<String, Setting> settings = new LinkedHashMap<>();

    private String name;
    private Type type;
    private String value;

    public Setting(String name, boolean value) {
        this(name, Type.BOOLEAN, value ? "True" : "False");
    }

    public Setting(String name, long value) {
        this(name, Type.LONG, Long.toString(value));
    }

    public Setting(String name, String value) {
        this(name, Type.STRING, value);
    }

    public Setting(String name, Type type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public void toggle() {
        if (!getType().equals(Type.BOOLEAN)) return;

        if (isTrue()) {
            setValue("False");
        } else {
            setValue("True");
        }
    }

    public void increment(long amount) {
        if (!getType().equals(Type.LONG)) return;

        setValue(Long.toString(Long.parseLong(getValue()) + amount));
    }

    public void decrement(long amount) {
        if (!getType().equals(Type.LONG)) return;

        setValue(Long.toString(Long.parseLong(getValue()) - amount));
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void register() {
        if (settings.containsKey(getName().toUpperCase())) return;
        if (settings.containsValue(this)) return;

        settings.put(getName().toUpperCase(), this);
    }

    public boolean isTrue() {
        return getValue().equals("True");
    }

    public long getLong() {
        return Long.parseLong(getValue());
    }

    public int getInt() {
        return Integer.parseInt(getValue());
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public enum Type {
        BOOLEAN,
        LONG,
        STRING;

        @Override
        public String toString() {
            return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
        }
    }

    public enum Name {
        INTERVAL,
        CONNECTION_WAIT,
        NEW_READ_TIMEOUT,
        NEW_WRITE_TIMEOUT,
        CHANNELS_PER_SEND,
        CHANNELS_PER_TIME,
        BUTTON_PANEL_WIDTH,

        SHOW_DMX_SEND_DEBUG,
        CONTINUOUS,
        LIMIT_LED_STRIPS,
        DRAW_DMX,
        REMEMBER_CAMERA_POSITION,
        HAZE;

        public String getName() {
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : this.toString().split("_")) {
                stringBuilder.append(" ");
                stringBuilder.append(string.substring(0, 1).toUpperCase());
                stringBuilder.append(string.substring(1).toLowerCase());
            }
            return stringBuilder.toString().substring(1);
        }
    }

    public static void createSettings() {
        new Setting(Name.INTERVAL.getName(), 100).register();
        new Setting(Name.CONNECTION_WAIT.getName(), 2000).register();
        new Setting(Name.NEW_READ_TIMEOUT.getName(), 0).register();
        new Setting(Name.NEW_WRITE_TIMEOUT.getName(), 0).register();
        new Setting(Name.CHANNELS_PER_SEND.getName(), 512).register();
        new Setting(Name.CHANNELS_PER_TIME.getName(), 512).register();
        new Setting(Name.BUTTON_PANEL_WIDTH.getName(), 800).register();

        new Setting(Name.SHOW_DMX_SEND_DEBUG.getName(), false).register();
        new Setting(Name.CONTINUOUS.getName(), false).register();
        new Setting(Name.LIMIT_LED_STRIPS.getName(), false).register();
        new Setting(Name.DRAW_DMX.getName(), false).register();
        new Setting(Name.REMEMBER_CAMERA_POSITION.getName(), false).register();
        new Setting(Name.HAZE.getName(), false).register();
    }

    public static void loadFromFile() {
        FileHandle fileHandle = Gdx.files.local("Lights/Settings/Settings.txt");
        if (!fileHandle.exists()) return;

        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (!line.contains(": ")) continue;

            Setting setting = byName(line.split(": ")[0]);
            if (setting == null) continue;
            setting.setValue(line.split(": ")[1]);
        }
    }

    public static void saveToFile() {
        if (count() == 0) return;

        FileHandle fileHandle = Gdx.files.local("Lights/Settings/Settings.txt");
        fileHandle.writeString("", false);

        for (Setting setting : settings())
            fileHandle.writeString(setting.getName().toUpperCase() + ": " + setting.getValue() + "\r\n", true);
    }

    public static int count() {
        return settings.size();
    }

    public static Setting byName(Name name) {
        return byName(name.getName());
    }

    public static Setting byName(String name) {
        return settings.getOrDefault(name.toUpperCase(), null);
    }

    public static List<Setting> settings() {
        return new ArrayList<>(settings.values());
    }
}