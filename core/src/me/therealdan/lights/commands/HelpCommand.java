package me.therealdan.lights.commands;

import me.therealdan.lights.ui.ui.ConsoleUI;

public class HelpCommand implements Command {

    @Override
    public boolean onCommand(ConsoleUI console, String command, String[] args) {
        if (!isCommand(command)) return false;

        for (Command each : console.getCommands()) {
            ConsoleUI.print(ConsoleUI.ConsoleColor.WHITE, each.getCommand() + " - " + each.getDescription());
        }

        return true;
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "List all commands";
    }

    @Override
    public String getSyntax() {
        return "help";
    }

    private boolean isCommand(String command) {
        if (command.equalsIgnoreCase("help")) return true;
        if (command.equalsIgnoreCase("?")) return true;

        return false;
    }
}
