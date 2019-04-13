package me.therealdan.lights.commands;

import me.therealdan.lights.main.Lights;
import me.therealdan.lights.ui.ui.ConsoleUI;

public class FreezeCommand implements Command {

    @Override
    public boolean onCommand(ConsoleUI console, String command, String[] args) {
        if (!command.equalsIgnoreCase(getCommand())) return false;

        Lights.output.toggleFreeze();

        return true;
    }

    @Override
    public String getCommand() {
        return "freeze";
    }

    @Override
    public String getDescription() {
        return "Toggle freeze state";
    }

    @Override
    public String getSyntax() {
        return "freeze";
    }
}