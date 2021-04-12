package dev.therealdan.lights.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SettingsStore {

    private final String _path = "Lights/Settings/Settings.txt";

    private LinkedHashSet<Setting> _settings = new LinkedHashSet<>();

    public SettingsStore() {
        loadDefaults();
        loadFromFile();
    }

    public void loadDefaults() {
        register(new Setting(Setting.Key.INTERVAL, 100));
        register(new Setting(Setting.Key.CONNECTION_WAIT, 2000));
        register(new Setting(Setting.Key.NEW_READ_TIMEOUT, 0));
        register(new Setting(Setting.Key.NEW_WRITE_TIMEOUT, 0));
        register(new Setting(Setting.Key.CHANNELS_PER_SEND, 512));
        register(new Setting(Setting.Key.CHANNELS_PER_TIME, 512));

        register(new Setting(Setting.Key.SHOW_DMX_SEND_DEBUG, false));
        register(new Setting(Setting.Key.CONTINUOUS, false));
        register(new Setting(Setting.Key.DRAW_DMX, false));
        register(new Setting(Setting.Key.REMEMBER_CAMERA_POSITION, false));
    }

    public void loadFromFile() {
        FileHandle fileHandle = Gdx.files.local(_path);
        if (!fileHandle.exists()) return;

        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (!line.contains(": ")) continue;

            Setting setting = getByName(line.split(": ")[0]);
            if (setting == null) continue;
            setting.setValue(line.split(": ")[1]);
        }
    }

    public void saveToFile() {
        if (count() == 0) return;

        FileHandle fileHandle = Gdx.files.local(_path);
        fileHandle.writeString("", false);

        for (Setting setting : getSettings())
            fileHandle.writeString(setting.getKey().toString() + ": " + setting.getValue() + "\r\n", true);
    }

    public void register(Setting setting) {
        _settings.add(setting);
    }

    public int count() {
        return _settings.size();
    }

    public Setting getByName(String name) {
        return getByKey(Setting.Key.valueOf(name.replace(" ", "_")));
    }

    public Setting getByKey(Setting.Key key) {
        for (Setting setting : getSettings())
            if (setting.getKey().equals(key))
                return setting;

        return null;
    }

    public List<Setting> getSettings() {
        return new ArrayList<>(_settings);
    }
}