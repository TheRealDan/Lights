package dev.therealdan.lights.store;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import dev.therealdan.lights.controllers.Button;
import dev.therealdan.lights.settings.Control;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ControlsStore implements Store {

    private LinkedHashSet<Control> _controls = new LinkedHashSet<>();

    public ControlsStore() {
        loadDefaults();
        loadFromFile();
    }

    @Override
    public void loadDefaults() {
        // TODO - These need some default key binds
        register(new Control(Control.Key.CAMERA_STRAFE_LEFT.toString(), Control.Category.VISUALISER3D, -1));
        register(new Control(Control.Key.CAMERA_STRAFE_RIGHT.toString(), Control.Category.VISUALISER3D, -1));
        register(new Control(Control.Key.CAMERA_FORWARD.toString(), Control.Category.VISUALISER3D, -1));
        register(new Control(Control.Key.CAMERA_BACKWARD.toString(), Control.Category.VISUALISER3D, -1));
        register(new Control(Control.Key.CAMERA_UP.toString(), Control.Category.VISUALISER3D, -1));
        register(new Control(Control.Key.CAMERA_DOWN.toString(), Control.Category.VISUALISER3D, -1));
    }

    @Override
    public void loadFromFile() {
        FileHandle fileHandle = Gdx.files.local(getPath());
        if (!fileHandle.exists()) return;

        Control.Category category = null;
        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (!line.contains(":")) continue;

            if (line.startsWith("  ")) {
                if (category == null) continue;

                String[] split = line.replace("  ", "").split(": ");
                String name = split[0];
                int keycode = Integer.parseInt(split[1]);

                Control control = getByKey(name);
                if (control == null) {
                    control = new Control(name, category);
                    register(control);
                }
                control.setKeycode(keycode);

            } else {
                category = Control.Category.valueOf(line.split(":")[0]);
            }
        }
    }

    @Override
    public void saveToFile() {
        if (count() == 0) return;

        FileHandle fileHandle = Gdx.files.local(getPath());
        fileHandle.writeString("", false);

        for (Control.Category category : Control.Category.values()) {
            fileHandle.writeString(category.toString() + ":\r\n", true);
            for (Control control : getControls(category)) {
                fileHandle.writeString("  " + control.getKey() + ": " + control.getKeycode() + "\r\n", true);
            }
        }
    }

    public void register(Control control) {
        _controls.add(control);
    }

    @Override
    public int count() {
        return _controls.size();
    }

    public Control getByButton(Button button) {
        Control control = getByKey("Button_" + button.getID());
        if (control == null) {
            control = new Control("Button_" + button.getID(), Control.Category.BUTTONS, -1);
            register(control);
        }
        return control;
    }

    public Control getByKey(Control.Key key) {
        return getByKey(key.toString());
    }

    public Control getByKey(String key) {
        for (Control control : getControls())
            if (control.getKey().equals(key))
                return control;

        return null;
    }

    public List<Control> getControls() {
        return new ArrayList<>(_controls);
    }

    public List<Control> getControls(Control.Category category) {
        List<Control> controls = new ArrayList<>();
        for (Control control : getControls())
            if (control.getCategory().equals(category))
                controls.add(control);

        return controls;
    }

    @Override
    public String getPath() {
        return "Lights/Settings/Controls.txt";
    }
}
