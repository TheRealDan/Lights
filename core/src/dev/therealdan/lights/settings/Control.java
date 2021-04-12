package dev.therealdan.lights.settings;

import com.badlogic.gdx.Input;
import dev.therealdan.lights.util.Util;

public class Control {

    private String _key;
    private Category _category;
    private int keycode = -1;

    public Control(String key, Category category, int keycode) {
        this(key, category);
        setKeycode(keycode);
    }

    public Control(String key, Category category) {
        _key = key;
        _category = category;
    }

    public void setKeycode(int keycode) {
        this.keycode = keycode;
    }

    public int getKeycode() {
        return keycode;
    }

    public String formatKeycode() {
        if (getKeycode() == -1) return "NONE";

        return Input.Keys.toString(getKeycode());
    }

    public String getKey() {
        return _key;
    }

    public String getName() {
        return Util.format(getKey());
    }

    public Category getCategory() {
        return _category;
    }

    public enum Key {
        CAMERA_STRAFE_LEFT,
        CAMERA_STRAFE_RIGHT,
        CAMERA_FORWARD,
        CAMERA_BACKWARD,
        CAMERA_UP,
        CAMERA_DOWN;
    }

    public enum Category {
        GLOBAL,
        VISUALISER3D,
        BUTTONS,
        UI;

        public String format() {
            switch (this) {
                case VISUALISER3D:
                    return "Visualiser 3D";
                case UI:
                    return UI.toString();
                default:
                    return Util.format(this.toString());
            }
        }
    }
}