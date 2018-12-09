package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.commands.*;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Console implements Tab {

    private static Console console;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
    private static boolean FORGET = false;

    private List<Command> commands = new LinkedList<>();

    private LinkedList<String> log = new LinkedList<>();
    private LinkedList<String> commandHistory = new LinkedList<>();
    private int commandHistoryCurrentIndex = 0;
    private boolean commandHistoryActive = false;
    private String input = "";

    public Console() {
        console = this;

        register(new HelpCommand());
        register(new ClearCommand());
        register(new ChannelCommand());
        register(new FreezeCommand());
        register(new SettingsCommand());
    }

    @Override
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        // Input
        float x = X + LightsCore.edge();
        float y = Y + LightsCore.edge();
        float width = WIDTH - LightsCore.edge() * 2;
        float height = 30;
        Util.box(renderer, x, y + height, width, height, LightsCore.medium(), input);

        // Log
        y += height + LightsCore.edge();
        height = HEIGHT - height - (LightsCore.edge() * 3);
        Util.box(renderer, x, y + height, width, height, LightsCore.medium());
        for (String line : getLog()) {
            String colorCode = "";
            if (line.startsWith("%")) {
                colorCode = line.substring(1, 2);
                line = line.substring(2);
            }
            if (y > HEIGHT - (LightsCore.edge() * 3)) break;
            renderer.queue(new Task(x + 5, y + 3).text(line, Task.TextPosition.BOTTOM_LEFT).setColor(getColor(colorCode)));
            y += 20;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                up();
                break;
            case Input.Keys.DOWN:
                down();
                break;
            case Input.Keys.ENTER:
                if (input.length() > 0)
                    enter();
                break;
            case Input.Keys.BACKSPACE:
                if (input.length() > 0)
                    input = input.substring(0, input.length() - 1);
                break;
            case Input.Keys.SPACE:
                if (input.length() == 0 || !input.substring(input.length() - 1).equals(" "))
                    input = input.concat(" ");
                break;

            case Input.Keys.GRAVE:
                input = input.concat("~");
                break;
            case Input.Keys.MINUS:
                input = input.concat(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ? "_" : "-");
                break;
            case Input.Keys.COMMA:
                input = input.concat(",");
                break;

            default:
                String key = Input.Keys.toString(keycode);
                if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(key.toUpperCase()))
                    input = input.concat(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ? key.toUpperCase() : key.toLowerCase());
        }

        return true;
    }

    private void enter() {
        if (input.startsWith(" ")) {
            clearInput();
            return;
        }

        String command = input.split(" ")[0];
        String[] args = input.replaceFirst(command + " ", "").split(" ");

        boolean commandExists = false;
        for (Command each : commands)
            if (each.onCommand(this, command, args))
                commandExists = true;

        if (commandExists) {
            commandHistory.addFirst(input);
        } else {
            print(ConsoleColor.RED, command + ": command not found.");
        }

        clearInput();
        commandHistoryActive = false;
        commandHistoryCurrentIndex = 0;
    }

    private void up() {
        if (commandHistory.size() == 0) return;

        if (commandHistoryActive) commandHistoryCurrentIndex++;
        if (commandHistoryCurrentIndex > commandHistory.size() - 1) commandHistoryCurrentIndex--;

        input = commandHistory.get(commandHistoryCurrentIndex);
        commandHistoryActive = true;
    }

    private void down() {
        if (commandHistoryCurrentIndex == 0) return;

        commandHistoryCurrentIndex--;
        input = commandHistory.get(commandHistoryCurrentIndex);
    }

    public void register(Command command) {
        commands.add(command);
    }

    private Color getColor(String colorCode) {
        switch (colorCode.toUpperCase()) {
            case "R":
                return LightsCore.RED;
            case "G":
                return LightsCore.GREEN;
            case "B":
                return LightsCore.BLUE;
            case "M":
                return LightsCore.MAGENTA;
            case "Y":
                return LightsCore.YELLOW;
            case "C":
                return LightsCore.CYAN;

            case "W":
            default:
                return LightsCore.text();
        }
    }

    public List<String> getLog() {
        try {
            return new ArrayList<>(log);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Command> getCommands() {
        return new ArrayList<>(commands);
    }

    public static void clearInput() {
        console.input = "";
    }

    public static void log(String message) {
        log(ConsoleColor.WHITE, message);
    }

    public static void log(ConsoleColor color, String message) {
        print(color, "[" + dateFormat.format(new Date()) + "]: " + message);
    }

    public static void print(String... message) {
        print(ConsoleColor.WHITE, message);
    }

    public static void print(ConsoleColor color, String... message) {
        if (FORGET) clearLog();
        for (String line : message)
            console.log.addFirst(color.getCode() + line);
    }

    public static void clearLog() {
        console.log.clear();
    }

    public enum ConsoleColor {
        WHITE,
        RED,
        GREEN,
        BLUE,
        MAGENTA,
        YELLOW,
        CYAN;

        public String getCode() {
            return "%" + this.toString().substring(0, 1);
        }
    }
}