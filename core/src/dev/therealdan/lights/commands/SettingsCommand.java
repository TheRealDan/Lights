package dev.therealdan.lights.commands;

import dev.therealdan.lights.settings.Setting;
import dev.therealdan.lights.panels.panels.ConsolePanel;

public class SettingsCommand implements Command {

    @Override
    public boolean onCommand(ConsolePanel console, String command, String[] args) {
        if (!command.equalsIgnoreCase(getCommand())) return false;

        if (args.length > 1) {
            Setting setting = Setting.byName(args[0]);
            if (setting != null) {
                setting.setValue(args[1]);

                ConsolePanel.print(setting.getName() + " set to: " + setting.getValue());
                return true;
            }
        }

        for (Setting setting : Setting.settings())
            ConsolePanel.print("settings " + setting.getName() + " [" + setting.getType().toString() + "]");

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