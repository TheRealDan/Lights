package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import me.therealdan.lights.Lights;
import me.therealdan.lights.commands.*;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.UIHandler;
import me.therealdan.lights.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ConsoleUI implements UI {

    private static ConsoleUI console;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

    private List<Command> commands = new LinkedList<>();

    private LinkedList<String> log = new LinkedList<>();
    private LinkedList<String> commandHistory = new LinkedList<>();
    private int commandHistoryCurrentIndex = 0;
    private boolean commandHistoryActive = false;
    private String input = "";

    public ConsoleUI() {
        console = this;

        register(new HelpCommand());
        register(new ClearCommand());
        register(new ChannelCommand());
        register(new FreezeCommand());
        register(new SettingsCommand());
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.CONSOLE);
        boolean interacted = false;

        float x = getX();
        float y = getY();
        float width = getWidth();
        float cellHeight = 30;

        Util.box(renderer, x, y, width, cellHeight, Lights.DARK_BLUE, setWidth(renderer, "Console"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (String line : getLog()) {
            String colorCode = "";
            if (line.startsWith("%")) {
                colorCode = line.substring(1, 2);
                line = line.substring(2);
            }
            Util.box(renderer, x, y, width, cellHeight, Lights.medium(), getColor(colorCode), setWidth(renderer, line));
            drag(x, y, width, cellHeight);
            y -= cellHeight;
        }

        Util.box(renderer, x, y, width, cellHeight, canInteract() ? Lights.DARK_RED : Lights.medium(), setWidth(renderer, input));
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        setHeightBasedOnY(y);
        return interacted;
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

        String command = input.contains(" ") ? input.split(" ")[0] : input;
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
                return Lights.RED;
            case "G":
                return Lights.GREEN;
            case "B":
                return Lights.BLUE;
            case "M":
                return Lights.MAGENTA;
            case "Y":
                return Lights.YELLOW;
            case "C":
                return Lights.CYAN;

            case "W":
            default:
                return Lights.text();
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
        for (String line : message)
            console.log.addLast(color.getCode() + line);
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