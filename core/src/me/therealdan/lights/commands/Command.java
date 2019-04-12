package me.therealdan.lights.commands;

import me.therealdan.lights.ui.ui.ConsoleUI;

public interface Command {

    boolean onCommand(ConsoleUI console, String command, String[] args);

    String getCommand();

    String getDescription();

    String getSyntax();
}