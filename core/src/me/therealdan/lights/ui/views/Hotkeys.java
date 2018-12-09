package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.controllers.Button;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hotkeys implements Tab {

    private static Hotkeys hotkeys;

    private HashMap<String, Integer> controls = new HashMap<>();
    private String edit = "";

    public Hotkeys() {
        hotkeys = this;

        set(Control.CAMERA_STRAFE_LEFT.toString(), Input.Keys.LEFT);
        set(Control.CAMERA_STRAFE_RIGHT.toString(), Input.Keys.RIGHT);
        set(Control.CAMERA_FORWARD.toString(), Input.Keys.UP);
        set(Control.CAMERA_BACKWARD.toString(), Input.Keys.DOWN);
        set(Control.CAMERA_UP.toString(), Input.Keys.COMMA);
        set(Control.CAMERA_DOWN.toString(), Input.Keys.PERIOD);

        set("Button_1", Input.Keys.Q);
        set("Button_2", Input.Keys.W);
        set("Button_3", Input.Keys.E);
        set("Button_4", Input.Keys.R);
        set("Button_5", Input.Keys.T);
        set("Button_6", Input.Keys.Y);
        set("Button_7", Input.Keys.U);
        set("Button_8", Input.Keys.I);
        set("Button_9", Input.Keys.O);
        set("Button_10", Input.Keys.P);

        set("Button_11", Input.Keys.A);
        set("Button_12", Input.Keys.S);
        set("Button_13", Input.Keys.D);
        set("Button_14", Input.Keys.F);
        set("Button_15", Input.Keys.G);
        set("Button_16", Input.Keys.H);
        set("Button_17", Input.Keys.J);
        set("Button_18", Input.Keys.K);
        set("Button_19", Input.Keys.L);

        set("Button_20", Input.Keys.Z);
        set("Button_21", Input.Keys.X);
        set("Button_22", Input.Keys.C);
        set("Button_23", Input.Keys.V);
        set("Button_24", Input.Keys.B);
        set("Button_25", Input.Keys.N);
        set("Button_26", Input.Keys.M);
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
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight))
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
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight))
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