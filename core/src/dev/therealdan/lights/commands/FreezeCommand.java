package dev.therealdan.lights.commands;

import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.panels.ConsolePanel;

public class FreezeCommand implements Command {

    @Override
    public boolean onCommand(ConsolePanel console, String command, String[] args) {
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