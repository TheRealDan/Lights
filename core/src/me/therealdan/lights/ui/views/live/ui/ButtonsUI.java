package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.controllers.Button;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ButtonsUI implements UI {

    private static ButtonsUI buttonsUI;
    public static float WIDTH = 800;
    public static final int PER_ROW = 10;

    private LinkedHashMap<Integer, Button> buttonMap = new LinkedHashMap<>();

    private Button buttonToMove = null;

    public ButtonsUI() {
        buttonsUI = this;

        FileHandle fileHandle = Gdx.files.local("Lights/Buttons/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle child : fileHandle.list())
                load(child);
    }

    private void load(FileHandle fileHandle) {
        Button button = new Button();
        boolean sequences = false;
        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("Name: ")) {
                button.rename(line.split(": ")[1]);
            } else if (line.startsWith("Position: ")) {
                int position = Integer.parseInt(line.split(": ")[1]);
                if (position >= 0) set(button, position);
            } else if (line.startsWith("Color:")) {
                sequences = false;
            } else if (line.startsWith("  Red: ") && !sequences) {
                button.setRed(Float.parseFloat(line.split(": ")[1]));
            } else if (line.startsWith("  Green: ") && !sequences) {
                button.setGreen(Float.parseFloat(line.split(": ")[1]));
            } else if (line.startsWith("  Blue: ") && !sequences) {
                button.setBlue(Float.parseFloat(line.split(": ")[1]));
            } else if (line.startsWith("Sequences:")) {
                sequences = true;
            } else if (sequences) {
                String[] args = line.replaceFirst("  ", "").split(": ");
                button.set(
                        SequencesUI.byName(args[0]),
                        Integer.parseInt(args[1])
                );
            }
        }
    }

    @Override
    public void save() {
        UI.super.save();

        for (Button button : buttons()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Buttons/" + button.getName() + ".txt");
            fileHandle.writeString("", false);

            fileHandle.writeString("Name: " + button.getName() + "\r\n", true);
            fileHandle.writeString("Position: " + getPosition(button) + "\r\n", true);
            fileHandle.writeString("Color:\r\n", true);
            fileHandle.writeString("  Red: " + button.getColor().r + "\r\n", true);
            fileHandle.writeString("  Green: " + button.getColor().g + "\r\n", true);
            fileHandle.writeString("  Blue: " + button.getColor().b + "\r\n", true);
            fileHandle.writeString("Sequences:\r\n", true);
            for (Sequence sequence : button.sequences()) {
                int priority = button.getPriority(sequence);
                fileHandle.writeString("  " + sequence.getName() + ": " + priority + "\r\n", true);
            }
        }
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.BUTTONS);
        boolean interacted = false;

        setWidth(ButtonsUI.WIDTH);

        float cellHeight = 30;

        float size = getWidth() / ButtonsUI.PER_ROW;

        float x = getX();
        float y = getY();

        Util.box(renderer, x, y, getWidth(), cellHeight, LightsCore.DARK_BLUE, "Buttons");
        drag(x, y, getWidth(), cellHeight);
        y -= cellHeight;

        for (int position = 1; position <= ButtonsUI.getTopPosition(); position++) {
            Button button = ButtonsUI.getButton(position);
            if (button != null) {
                Util.box(renderer, x, y, size, size, button.getColor());
                renderer.queue(new Task(x, y - size / 2).text(button.getName(), Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));
                if (Util.containsMouse(x, y, size, size) && canInteract()) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(1000)) {
                        button.press();
                        Util.box(renderer, x, y, size, size, new Color(button.getColor()).mul(1.5f));
                    } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                        ButtonEditUI.edit(button);
                    }
                }
            } else {
                Util.box(renderer, x, y, size, size, LightsCore.dark());
                if (Util.containsMouse(x, y, size, size) && canInteract()) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(1000)) {
                        if (getButtonToMove() != null) {
                            set(getButtonToMove(), position);
                            move(null);
                        }
                    }
                }
            }
            x += size;

            if (x + size > getX() + getWidth()) {
                x = getX();
                y -= size;
            }
        }
        y -= size;

        setHeightBasedOnY(y);
        return interacted;
    }

    public static Button getButtonToMove() {
        return buttonsUI.buttonToMove;
    }

    public static void move(Button button) {
        buttonsUI.buttonToMove = button;
    }

    public static void set(Button button) {
        set(button, getFreePosition());
    }

    public static void set(Button button, int position) {
        remove(button);
        if (!contains(position)) {
            buttonsUI.buttonMap.put(position, button);
        } else {
            set(button);
        }
    }

    public static void remove(Button button) {
        if (contains(button.getPosition()))
            buttonsUI.buttonMap.remove(button.getPosition());
    }

    public static boolean contains(int position) {
        return buttonsUI.buttonMap.containsKey(position);
    }

    public static int getPosition(Button button) {
        for (int position : buttonsUI.buttonMap.keySet())
            if (getButton(position).equals(button))
                return position;

        return -1;
    }

    public static int getFreePosition() {
        int position = 1;
        while (contains(position))
            position++;
        return position;
    }

    public static int getTopPosition() {
        int top = -1;
        for (int position : buttonsUI.buttonMap.keySet())
            if (position > top) top = position;
        return top;
    }

    public static Button getButton(int position) {
        return buttonsUI.buttonMap.getOrDefault(position, null);
    }

    public static List<Button> buttons() {
        return new ArrayList<>(buttonsUI.buttonMap.values());
    }
}