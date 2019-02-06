package me.therealdan.lights.commands;

import me.therealdan.lights.ui.views.live.ui.ConsoleUI;

public class ClearCommand implements Command {

    @Override
    public boolean onCommand(ConsoleUI console, String command, String[] args) {
        if (!command.equalsIgnoreCase(getCommand())) return false;

        ConsoleUI.clearLog();

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