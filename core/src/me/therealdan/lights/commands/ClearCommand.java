package me.therealdan.lights.commands;

import me.therealdan.lights.ui.views.Console;

public class ClearCommand implements Command {

    @Override
    public boolean onCommand(Console console, String command, String[] args) {
        if (!command.equalsIgnoreCase(getCommand())) return false;

        Console.clearLog();

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