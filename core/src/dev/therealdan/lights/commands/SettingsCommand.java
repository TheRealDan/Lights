package dev.therealdan.lights.commands;

import dev.therealdan.lights.panels.panels.ConsolePanel;
import dev.therealdan.lights.settings.Setting;
import dev.therealdan.lights.settings.SettingsStore;

public class SettingsCommand implements Command {

    private SettingsStore _settingsStore;

    public SettingsCommand(SettingsStore settingsStore) {
        _settingsStore = settingsStore;
    }

    @Override
    public boolean onCommand(ConsolePanel console, String command, String[] args) {
        if (!command.equalsIgnoreCase(getCommand())) return false;

        if (args.length > 1) {
            Setting setting = _settingsStore.getByName(args[0]);
            if (setting != null) {
                setting.setValue(args[1]);

                ConsolePanel.print(setting.getKey() + " set to: " + setting.getValue());
                return true;
            }
        }

        for (Setting setting : _settingsStore.getSettings())
            ConsolePanel.print("settings " + setting.getKey() + " [" + setting.getKey().getType().toString() + "]");

        return true;
    }

    @Override
    public String getCommand() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return "Edit Settings";
    }

    @Override
    public String getSyntax() {
        return "settings";
    }
}