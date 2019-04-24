package me.therealdan.lights.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Control {

    private static LinkedHashMap<String, Control> controls = new LinkedHashMap<>();

    private String name;
    private Category category;

    private int keycode = -1;

    public Control(Name name, Category category, int keycode) {
        this(name.toString(), category, keycode);
    }

    public Control(String name, Category category, int keycode) {
        this(name, category);
        setKeycode(keycode);
    }

    public Control(Name name, Category category) {
        this(name.toString(), category);
    }

    public Control(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public void setKeycode(int keycode) {
        this.keycode = keycode;
    }

    public void register() {
        if (controls.containsKey(getName())) return;
        if (controls.containsValue(this)) return;

        controls.put(getName(), this);
    }

    public int getKeycode() {
        return keycode;
    }

    public String formatKeycode() {
        if (getKeycode() == -1) return "NONE";

        return Input.Keys.toString(getKeycode());
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public enum Name {
        CAMERA_STRAFE_LEFT,
        CAMERA_STRAFE_RIGHT,
        CAMERA_FORWARD,
        CAMERA_BACKWARD,
        CAMERA_UP,
        CAMERA_DOWN;

        public String formatString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : this.toString().split("_")) {
                stringBuilder.append(" ");
                stringBuilder.append(string.substring(0, 1).toUpperCase());
                stringBuilder.append(string.substring(1).toLowerCase());
            }
            return stringBuilder.toString().substring(1);
        }
    }

    public enum Category {
        GLOBAL,
        VISUALISER3D,
        BUTTONS,
        UI;

        public String formatString() {
            if (this.equals(UI)) return this.toString();

            StringBuilder stringBuilder = new StringBuilder();
            for (String string : this.toString().split("_")) {
                stringBuilder.append(" ");
                stringBuilder.append(string.substring(0, 1).toUpperCase());
                stringBuilder.append(string.substring(1).toLowerCase());
            }

            if (this.equals(VISUALISER3D)) return "Visualiser3D";

            return stringBuilder.toString().substring(1);
        }

        public List<Control> getControls() {
            List<Control> controls = new ArrayList<>();
            for (Control control : controls())
                if (control.getCategory().equals(this))
                    controls.add(control);

            return controls;
        }
    }

    public static void createControls() {
        new Control(Name.CAMERA_STRAFE_LEFT, Category.VISUALISER3D).register();
        new Control(Name.CAMERA_STRAFE_RIGHT, Category.VISUALISER3D).register();
        new Control(Name.CAMERA_FORWARD, Category.VISUALISER3D).register();
        new Control(Name.CAMERA_BACKWARD, Category.VISUALISER3D).register();
        new Control(Name.CAMERA_UP, Category.VISUALISER3D).register();
        new Control(Name.CAMERA_DOWN, Category.VISUALISER3D).register();
    }

    public static void loadFromFile() {
        FileHandle fileHandle = Gdx.files.local("Lights/Settings/Controls.txt");
        if (!fileHandle.exists()) return;

        Category category = null;
        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (!line.contains(":")) continue;

            if (line.startsWith("  ")) {
                if (category == null) continue;

                String[] split = line.replace("  ", "").split(": ");
                String name = split[0];
                int keycode = Integer.parseInt(split[1]);

                Control control = Control.byName(name);
                if (control == null) {
                    control = new Control(name, category);
                    control.register();
                }
                control.setKeycode(keycode);

            } else {
                category = Category.valueOf(line.split(":")[0]);
            }
        }
    }

    public static void saveToFile() {
        if (count() == 0) return;

        FileHandle fileHandle = Gdx.files.local("Lights/Settings/Controls.txt");
        fileHandle.writeString("", false);

        for (Category category : categories()) {
            fileHandle.writeString(category.toString() + ":\r\n", true);
            for (Control control : category.getControls()) {
                fileHandle.writeString("  " + control.getName().toString() + ": " + control.getKeycode() + "\r\n", true);
            }
        }
    }

    public static int count() {
        return controls.size();
    }

    public static Control byName(Name name) {
        return byName(name.toString());
    }

    public static Control byName(String name) {
        return controls.getOrDefault(name, null);
    }

    public static List<Control> controls() {
        return new ArrayList<>(controls.values());
    }

    public static List<Name> names() {
        return Arrays.asList(Name.values());
    }

    public static List<Category> categories() {
        return Arrays.asList(Category.values());
    }
}