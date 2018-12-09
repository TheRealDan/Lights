package me.therealdan.lights.commands;

import me.therealdan.lights.ui.views.Console;

public interface Command {

    boolean onCommand(Console console, String command, String[] args);

    String getCommand();

    String getDescription();

    String getSyntax();
}