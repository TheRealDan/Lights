package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.controllers.Button;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Hotkeys implements Tab {

    private static Hotkeys hotkeys;

    private LinkedHashMap<String, Integer> controls = new LinkedHashMap<>();
    private String edit = "";

    public Hotkeys() {
        hotkeys = this;

        load();
    }

    private void load() {
        FileHandle fileHandle = Gdx.files.local("Lights/Settings/Controls.txt");

        for (String line : fileHandle.readString().split("\\r?\\n")) {
            String[] args = line.split(": ");
            set(
                    args[0],
                    Integer.parseInt(args[1])
            );
        }
    }

    @Override
    public void save() {
        FileHandle fileHandle = Gdx.files.local("Lights/Settings/Controls.txt");
        fileHandle.writeString("", false);

        for (String control : controls.keySet())
            fileHandle.writeString(control + ": " + get(control) + "\r\n", true);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE:
                clear(getEdit());
                break;
            case Input.Keys.SPACE:
            case Input.Keys.COMMA:
            case Input.Keys.PERIOD:
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:
            case Input.Keys.UP:
            case Input.Keys.DOWN:
                set(getEdit(), keycode);
                break;
            default:
                String string = Input.Keys.toString(keycode);
                if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                    set(getEdit(), keycode);
                }
        }

        return true;
    }

    @Override
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        float cellHeight = 30;

        float x = LightsCore.edge();
        float y = Gdx.graphics.getHeight() - Y;

        float width = (WIDTH - (LightsCore.edge() * 3f)) / 2f;

        buttons(renderer, x, y, width, cellHeight);
        visualiser(renderer, x + width + LightsCore.edge(), y, width, cellHeight);
    }

    private void buttons(Renderer renderer, float x, float y, float width, float cellHeight) {
        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Buttons");
        y -= cellHeight;

        for (Button button : Buttons.buttons()) {
            String key = "Button_" + button.getPosition();
            Util.box(renderer, x, y, width, cellHeight, getEdit().equalsIgnoreCase(key) ? LightsCore.DARK_RED : LightsCore.medium(), contains(key) ? button.getName() + " - " + format(key) : button.getName());
            if (Util.containsMouse(x, y, width, cellHeight))
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                    setEdit(key);
            y -= cellHeight;
        }
    }

    private void visualiser(Renderer renderer, float x, float y, float width, float cellHeight) {
        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Buttons");
        y -= cellHeight;

        for (Control control : Category.VISUALISER.getControls()) {
            String key = control.toString();
            Util.box(renderer, x, y, width, cellHeight, getEdit().equalsIgnoreCase(key) ? LightsCore.DARK_RED : LightsCore.medium(), contains(key) ? control.getName() + " - " + format(key) : control.getName());
            if (Util.containsMouse(x, y, width, cellHeight))
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                    setEdit(key);
            y -= cellHeight;
        }
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public String getEdit() {
        return edit;
    }

    public enum Control {
        CAMERA_STRAFE_LEFT,
        CAMERA_STRAFE_RIGHT,
        CAMERA_FORWARD,
        CAMERA_BACKWARD,
        CAMERA_UP,
        CAMERA_DOWN;

        public Category getCategory() {
            switch (this) {
                case CAMERA_STRAFE_LEFT:
                case CAMERA_STRAFE_RIGHT:
                case CAMERA_FORWARD:
                case CAMERA_BACKWARD:
                case CAMERA_UP:
                case CAMERA_DOWN:
                    return Category.VISUALISER;
                default:
                    return Category.BUTTONS;
            }
        }

        public String getName() {
            return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
        }
    }

    public enum Category {
        VISUALISER,
        BUTTONS;

        public List<Control> getControls() {
            List<Control> controls = new ArrayList<>();
            for (Control control : Control.values())
                if (control.getCategory().equals(this))
                    controls.add(control);
            return controls;
        }

        public String getName() {
            return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
        }
    }

    public static void clear(String control) {
        hotkeys.controls.remove(control);
    }

    public static void set(Control control, int key) {
        set(control.toString(), key);
    }

    public static void set(String control, int key) {
        hotkeys.controls.put(control, key);
    }

    public static int get(Control control) {
        return get(control.toString());
    }

    public static int get(String control) {
        return hotkeys.controls.get(control);
    }

    public static boolean contains(Control control) {
        return contains(control.toString());
    }

    public static boolean contains(String control) {
        return hotkeys.controls.containsKey(control);
    }

    public static String format(Control control) {
        return format(control.toString());
    }

    public static String format(String string) {
        return Input.Keys.toString(get(string));
    }

    public static Button getButton(int keycode) {
        for (Button button : Buttons.buttons()) {
            if (contains("Button_" + button.getPosition())) {
                if (get("Button_" + button.getPosition()) == keycode) {
                    return button;
                }
            }
        }

        return null;
    }


}