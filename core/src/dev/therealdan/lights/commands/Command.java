package dev.therealdan.lights.commands;

import dev.therealdan.lights.ui.ui.ConsoleUI;

public interface Command {

    boolean onCommand(ConsoleUI console, String command, String[] args);

    String getCommand();

    String getDescription();

    String getSyntax();
}