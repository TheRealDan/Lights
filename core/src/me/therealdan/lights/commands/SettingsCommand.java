package me.therealdan.lights.commands;

import me.therealdan.lights.settings.Setting;
import me.therealdan.lights.ui.ui.ConsoleUI;

public class SettingsCommand implements Command {

    @Override
    public boolean onCommand(ConsoleUI console, String command, String[] args) {
        if (!command.equalsIgnoreCase(getCommand())) return false;

        if (args.length > 1) {
            Setting setting = Setting.byName(args[0]);
            if (setting != null) {
                setting.setValue(args[1]);

                ConsoleUI.print(setting.getName() + " set to: " + setting.getValue());
                return true;
            }
        }

        for (Setting setting : Setting.settings())
            ConsoleUI.print("settings " + setting.getName() + " [" + setting.getType().toString() + "]");

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