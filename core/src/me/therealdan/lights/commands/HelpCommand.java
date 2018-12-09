package me.therealdan.lights.commands;

import me.therealdan.lights.ui.views.Console;

public class HelpCommand implements Command {

    @Override
    public boolean onCommand(Console console, String command, String[] args) {
        if (!isCommand(command)) return false;

        for (Command each : console.getCommands()) {
            Console.print(Console.ConsoleColor.WHITE, each.getCommand() + " - " + each.getDescription());
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
