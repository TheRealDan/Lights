package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    public static int ROWS = 10;
    public static int COLUMNS = 10;

    private LinkedHashMap<Integer, Button> buttonMap = new LinkedHashMap<>();

    private Section section = Section.NONE;
    private Button button = null;
    private Sequence sequence = null;

    public Buttons() {
        buttons = this;

        Button red = new Button("Red", LightsCore.RED);
        red.set(Sequences.byName("Red"), 1);
        set(red);

        Button green = new Button("Green", LightsCore.GREEN);
        green.set(Sequences.byName("Green"), 1);
        set(green);

        Button blue = new Button("Blue", LightsCore.BLUE);
        blue.set(Sequences.byName("Blue"), 1);
        set(blue);

        Button magenta = new Button("Magenta", LightsCore.MAGENTA);
        magenta.set(Sequences.byName("Magenta"), 1);
        set(magenta);

        Button yellow = new Button("Yellow", LightsCore.YELLOW);
        yellow.set(Sequences.byName("Yellow"), 1);
        set(yellow);

        Button cyan = new Button("Cyan", LightsCore.CYAN);
        cyan.set(Sequences.byName("Cyan"), 1);
        set(cyan);

        Button purple = new Button("Purple", LightsCore.PURPLE);
        purple.set(Sequences.byName("Purple"), 1);
        set(purple);

        Button orange = new Button("Orange", LightsCore.ORANGE);
        orange.set(Sequences.byName("Orange"), 1);
        set(orange);

        Button white = new Button("White", LightsCore.WHITE);
        white.set(Sequences.byName("White"), 1);
        set(white);

        Button black = new Button("Black", LightsCore.BLACK);
        black.set(Sequences.byName("Black"), 1);
        set(black);

        Button solid = new Button("Solid", LightsCore.BLACK);
        solid.set(Sequences.byName("Solid"), 2);
        set(solid);

        Button flashySublte = new Button("Flashy Subtle", LightsCore.BLACK);
        flashySublte.set(Sequences.byName("Flashy Subtle"), 2);
        set(flashySublte);

        Button flashyx3 = new Button("Flashy x3", LightsCore.BLACK);
        flashyx3.set(Sequences.byName("Flashy x3"), 2);
        set(flashyx3);

        Button flashyx1 = new Button("Flashy x1", LightsCore.BLACK);
        flashyx1.set(Sequences.byName("Flashy x1"), 2);
        set(flashyx1);

        Button ledsFlashy = new Button("LEDs Flashy", LightsCore.BLACK);
        ledsFlashy.set(Sequences.byName("LEDs Flashy"), 3);
        set(ledsFlashy);

        Button strobe = new Button("Strobe", LightsCore.BLACK);
        strobe.set(Sequences.byName("Strobe"), 2);
        set(strobe);

        Button mOverlay = new Button("M Overlay", LightsCore.MAGENTA);
        mOverlay.set(Sequences.byName("M Overlay"), 4);
        set(mOverlay);
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

            case ROW:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        Buttons.set(button, 0, button.getColumn());
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("1234567890".contains(string)) {
                            String row = button.getRow() + string;
                            Buttons.set(button, Integer.parseInt(row), button.getColumn());
                        }
                }
                break;

            case COLUMN:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        Buttons.set(button, button.getRow(), 0);
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("1234567890".contains(string)) {
                            String column = button.getColumn() + string;
                            Buttons.set(button, button.getRow(), Integer.parseInt(column));
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

        NAME, ID,
        POSITION, ROW, COLUMN,

        SEQUENCES;

        public boolean isButton() {
            switch (this) {
                case NAME:
                case ID:
                case POSITION:
                case ROW:
                case COLUMN:
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
                case ID:
                    return Integer.toString(button.getID());
                case POSITION:
                    return Integer.toString(button.getPosition());
                case ROW:
                    return Integer.toString(button.getRow());
                case COLUMN:
                    return Integer.toString(button.getColumn());
            }
        }

        public String getName() {
            if (this.equals(ID)) return "ID";
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

    public static void set(Button button, int row, int column) {
        set(button, getPosition(row, column));
    }

    public static void remove(Button button) {
        if (contains(button.getPosition()))
            buttons.buttonMap.remove(button.getPosition());
    }

    public static boolean contains(int position) {
        return buttons.buttonMap.containsKey(position);
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

    public static Button getButton(int row, int column) {
        return getButton(getPosition(row, column));
    }

    public static Button getButton(int position) {
        return buttons.buttonMap.getOrDefault(position, null);
    }

    private static int getPosition(int row, int column) {
        return (row - 1) * buttons.ROWS + column;
    }

    public static List<Button> buttons() {
        return new ArrayList<>(buttons.buttonMap.values());
    }
}