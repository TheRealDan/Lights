package dev.therealdan.lights.commands;

import dev.therealdan.lights.panels.panels.ConsolePanel;

public interface Command {

    boolean onCommand(ConsolePanel console, String command, String[] args);

    String getCommand();

    String getDescription();

    String getSyntax();
}