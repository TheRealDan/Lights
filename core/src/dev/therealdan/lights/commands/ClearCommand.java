package dev.therealdan.lights.commands;

import dev.therealdan.lights.panels.panels.ConsolePanel;

public class ClearCommand implements Command {

    @Override
    public boolean onCommand(ConsolePanel console, String command, String[] args) {
        if (!command.equalsIgnoreCase(getCommand())) return false;

        ConsolePanel.clearLog();

        return true;
    }

    @Override
    public String getCommand() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clear console";
    }

    @Override
    public String getSyntax() {
        return "clear";
    }
}