package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.controllers.Button;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Buttons implements Tab {

    private static Buttons buttons;
    public static final int PER_ROW = 10;

    private LinkedHashMap<Integer, Button> buttonMap = new LinkedHashMap<>();

    private Section section = Section.NONE;
    private Button button = null;
    private Sequence sequence = null;

    public Buttons() {
        buttons = this;

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
                        Sequences.byName(args[0]),
                        Integer.parseInt(args[1])
                );
            }
        }
    }

    @Override
    public void save() {
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
    public boolean keyDown(int keycode) {
        Button button = getButton();
        if (button == null) return true;

        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        switch (getSection()) {
            case NAME:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        if (button.getName().length() > 0)
                            button.rename(button.getName().substring(0, button.getName().length() - 1));
                        if (shift) button.rename("");
                        break;
                    case Input.Keys.SPACE:
                        button.rename(button.getName() + " ");
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                            if (!shift) string = string.toLowerCase();
                            button.rename(button.getName() + string);
                        }
                }
                break;

            case POSITION:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        Buttons.set(button, 0);
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("1234567890".contains(string)) {
                            String position = button.getPosition() + string;
                            Buttons.set(button, Integer.parseInt(position));
                        }
                }
                break;

            case SEQUENCES:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        button.set(getSequence(), 0);
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("1234567890".contains(string)) {
                            String priority = button.getPriority(getSequence()) + string;
                            button.set(getSequence(), Integer.parseInt(priority));
                        }
                }
                break;
        }

        return true;
    }

    @Override
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        float cellHeight = 30;

        float x = X + LightsCore.edge();
        float y = HEIGHT - Y - LightsCore.edge();

        float width = (WIDTH - (LightsCore.edge() * 4f)) / 3f;

        buttons(renderer, x, y, width, cellHeight);
        button(renderer, x + width + LightsCore.edge(), y, width, cellHeight);
        sequences(renderer, x + width * 2f + LightsCore.edge() * 2f, y, width, cellHeight);
    }

    private void buttons(Renderer renderer, float x, float y, float width, float cellHeight) {
        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Buttons");
        y -= cellHeight;

        for (Button button : buttons()) {
            Util.box(renderer, x, y, width, cellHeight, button.equals(getButton()) ? LightsCore.DARK_RED : LightsCore.medium(), button.getName());
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                    select(button);
            }
            y -= cellHeight;
        }

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Add Button");
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                set(new Button());
        }
    }

    private void button(Renderer renderer, float x, float y, float width, float cellHeight) {
        Button button = getButton();
        if (button == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Button");
        y -= cellHeight;

        for (Section section : Section.values()) {
            if (!section.isButton()) continue;
            Util.box(renderer, x, y, width, cellHeight, section.equals(getSection()) ? LightsCore.DARK_RED : LightsCore.medium(), section.getName() + ": " + section.getValue(button));
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight))
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                    setSection(section);
            y -= cellHeight;
        }
    }

    private void sequences(Renderer renderer, float x, float y, float width, float cellHeight) {
        Button button = getButton();
        if (button == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Sequences");
        y -= cellHeight;

        boolean alternate = true;
        for (Sequence sequence : Sequences.sequences()) {
            Util.box(renderer, x, y, width, cellHeight, button.contains(sequence) ? LightsCore.DARK_RED : alternate ? LightsCore.medium() : LightsCore.dark(), sequence.getName() + " " + (button.contains(sequence) ? button.getPriority(sequence) : ""))
            ;
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                setSection(Section.SEQUENCES);
                select(sequence);
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                    if (button.contains(sequence)) {
                        button.remove(sequence);
                    } else {
                        button.set(sequence, 1);
                    }
                }
            }
            y -= cellHeight;
            alternate = !alternate;
        }
    }

    public void select(Button button) {
        this.button = button;
    }

    public void select(Sequence sequence) {
        this.sequence = sequence;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public Button getButton() {
        return button;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Section getSection() {
        return section;
    }

    public enum Section {
        NONE,

        NAME,
        POSITION,

        SEQUENCES;

        public boolean isButton() {
            switch (this) {
                case NAME:
                case POSITION:
                    return true;
                default:
                    return false;
            }
        }

        public String getValue(Button button) {
            switch (this) {
                default:
                    return "N/A";
                case NAME:
                    return button.getName();
                case POSITION:
                    return Integer.toString(button.getPosition());
            }
        }

        public String getName() {
            return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
        }
    }

    public static void set(Button button) {
        set(button, getFreePosition());
    }

    public static void set(Button button, int position) {
        remove(button);
        if (!contains(position)) {
            buttons.buttonMap.put(position, button);
        } else {
            set(button);
        }
    }

    public static void remove(Button button) {
        if (contains(button.getPosition()))
            buttons.buttonMap.remove(button.getPosition());
    }

    public static boolean contains(int position) {
        return buttons.buttonMap.containsKey(position);
    }

    public static int getPosition(Button button) {
        for (int position : buttons.buttonMap.keySet())
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
        for (int position : buttons.buttonMap.keySet())
            if (position > top) top = position;
        return top;
    }

    public static Button getButton(int position) {
        return buttons.buttonMap.getOrDefault(position, null);
    }

    public static List<Button> buttons() {
        return new ArrayList<>(buttons.buttonMap.values());
    }
}