package dev.therealdan.lights.commands;

import dev.therealdan.lights.dmx.Output;
import dev.therealdan.lights.panels.panels.ConsolePanel;

public class FreezeCommand implements Command {

    private Output _output;

    public FreezeCommand(Output output) {
        _output = output;
    }

    @Override
    public boolean onCommand(ConsolePanel console, String command, String[] args) {
        if (!command.equalsIgnoreCase(getCommand())) return false;

        _output.toggleFreeze();

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