package me.therealdan.lights.commands;

import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.ui.views.live.ui.ConsoleUI;
import me.therealdan.lights.programmer.Programmer;

import java.util.ArrayList;
import java.util.List;

public class ChannelCommand implements Command {

    @Override
    public boolean onCommand(ConsoleUI console, String command, String[] args) {
        if (!command.equalsIgnoreCase(getCommand())) return false;

        try {
            int value = Math.min(Math.max(Integer.parseInt(args[1]), 0), 255);

            List<Integer> channels = new ArrayList<>();
            for (String string : args[0].split(",")) {
                if (string.contains("~")) {
                    for (int channel = Integer.parseInt(string.split("~")[0]); channel <= Integer.parseInt(string.split("~")[1]); channel++)
                        channels.add(channel);
                } else {
                    channels.add(Integer.parseInt(string));
                }
            }

            int maxChannels = 16;
            int i = channels.size() > maxChannels ? 0 : -1;
            StringBuilder channelsString = new StringBuilder();
            for (int channel : channels) {
                if (1 <= channel && channel <= DMX.MAX_CHANNELS) {
                    Programmer.set(channel, value);
                    if (i < maxChannels) channelsString.append(", ").append(channel);
                    if (i > -1) i++;
                }
            }

            if (i > maxChannels)
                channelsString.append(" and ").append(channels.size() - maxChannels).append(" others");

            String s = " ";
            if (i > 1) s = "s ";
            ConsoleUI.log("channel" + s + channelsString.toString().replaceFirst(", ", "") + " set to: " + value);
        } catch (Exception e) {
            ConsoleUI.print(ConsoleUI.ConsoleColor.RED, "Command Syntax: " + getSyntax());
        }
        return true;
    }

    @Override
    public String getCommand() {
        return "channel";
    }

    @Override
    public String getDescription() {
        return "Directly set channel values";
    }

    @Override
    public String getSyntax() {
        return "channel [channel] [value]";
    }
}