package me.therealdan.lights.commands;

import me.therealdan.lights.dmx.Output;
import me.therealdan.lights.ui.views.live.ui.ConsoleUI;

public class SettingsCommand implements Command {

    @Override
    public boolean onCommand(ConsoleUI console, String command, String[] args) {
        if (!command.equalsIgnoreCase(getCommand())) return false;

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("interval")) {
                long value = Long.parseLong(args[1]);
                Output.INTERVAL = value;
                ConsoleUI.print("Set interval to: " + value);
                return true;
            } else if (args[0].equalsIgnoreCase("connection_wait")) {
                long value = Long.parseLong(args[1]);
                Output.CONNECTION_WAIT = value;
                ConsoleUI.print("Set connection wait to: " + value);
                return true;
            } else if (args[0].equalsIgnoreCase("new_read_timeout")) {
                int value = Integer.parseInt(args[1]);
                Output.NEW_READ_TIMEOUT = value;
                ConsoleUI.print("Set new read timeout to: " + value);
                return true;
            } else if (args[0].equalsIgnoreCase("new_write_timeout")) {
                int value = Integer.parseInt(args[1]);
                Output.NEW_WRITE_TIMEOUT = value;
                ConsoleUI.print("Set new write timeout to: " + value);
                return true;
            } else if (args[0].equalsIgnoreCase("channels_per_send")) {
                int value = Integer.parseInt(args[1]);
                Output.CHANNELS_PER_SEND = value;
                ConsoleUI.print("Set channels per send to: " + value);
                return true;
            }
        }

        ConsoleUI.print(
                "settings interval [long]",
                "settings connection_wait [long]",
                "settings new_read_timeout [int]",
                "settings new_write_timeout [int]",
                "settings channels_per_send [int]"
        );

        return true;
    }

    @Override
    public String getCommand() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return "Edit Settings";
    }

    @Override
    public String getSyntax() {
        return "settings";
    }
}