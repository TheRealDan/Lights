package me.therealdan.lights.commands;

import me.therealdan.lights.dmx.Output;
import me.therealdan.lights.ui.views.Console;

public class FreezeCommand implements Command {

    @Override
    public boolean onCommand(Console console, String command, String[] args) {
        if (!command.equalsIgnoreCase(getCommand())) return false;

        Output.toggleFreeze();

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