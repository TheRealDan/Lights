package me.therealdan.lights.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.controllers.Button;
import me.therealdan.lights.controllers.Fader;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.fixtures.Group;
import me.therealdan.lights.fixtures.fixture.Profile;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.programmer.CondensedFrame;
import me.therealdan.lights.programmer.Frame;
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.settings.Control;
import me.therealdan.lights.settings.Setting;
import me.therealdan.lights.ui.ui.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UIHandler implements Visual {

    private static UIHandler uiHandler;

    private List<UI> uis = new ArrayList<>();
    private UI dragging = null;
    private float xDifference, yDifference;

    private float master = 1.0f;

    private Section section = Section.VISUALISER3D;
    private long lastSet = System.currentTimeMillis();

    private HashMap<Integer, Sequence> sequenceStack = new HashMap<>();
    private HashMap<Sequence, Long> clear = new HashMap<>();

    private long lastTempo = System.currentTimeMillis();
    private long tempo = 1000;

    private CondensedFrame targetCondensedFrame, currentCondensedFrame, previousCondensedFrame;
    private long condensedFrameTimestamp = System.currentTimeMillis();

    public UIHandler() {
        uiHandler = this;

        // Menu
        uis.add(new PanelVisibilityUI());

        // Settings
        uis.add(new SettingsUI());
        uis.add(new ControlsUI());
        uis.add(new DMXInterfaceUI());

        // Setup
        uis.add(new ProfilesUI());

        // TODO - Move elsewhere
        Profile.loadProfilesFromFile();

        uis.add(new PatchUI());

        // TODO - Move elsewhere
        Fixture.loadFixturesFromFile();
        Group.loadGroupsFromFile();

        uis.add(new SequencesUI());
        uis.add(new FaderEditUI());
        uis.add(new ButtonEditUI());

        // Util
        uis.add(new ConsoleUI());
        uis.add(new DMXOutputUI());

        // Programmer
        uis.add(new SequenceProgrammerUI());
        uis.add(new NewSequenceProgrammerUI());
        uis.add(new FixturesUI());
        uis.add(new GroupsUI());
        uis.add(new ParametersUI());

        // Info
        uis.add(new FrozenUI());
        uis.add(new ActiveSequencesUI());
        uis.add(new TimingsUI());

        // Panels
        uis.add(new MasterUI());
        uis.add(new FadersUI());
        uis.add(new ButtonsUI());

        // TODO - Move elsewhere
        Button.loadButtonsFromFile();

        for (UI ui : UIs())
            ui.load();
    }

    public void save() {
        for (UI ui : UIs())
            ui.save();

        // TODO - Move elsewhere?
        Fixture.saveFixturesToFile();
        Group.saveGroupsToFile();

        Profile.saveProfilesToFile();

        Button.saveButtonsToFile();
    }

    public void update() {
        DMX output = DMX.get("OUTPUT");
        DMX visualiser = DMX.get("VISUALISER");

        for (Sequence sequence : getSequences()) {
            if (!sequence.isPlaying())
                sequence.play();

            if (sequence.useTempo()) {
                for (Frame frame : sequence.frames()) {
                    frame.setFrameTime(getTempo());
                    frame.setFadeTime(getTempo());
                }
            }

            boolean skip = false;
            if (sequence.doesClear() && sequence.onLastFrame()) {
                if (!clear.containsKey(sequence)) {
                    clear.put(sequence, System.currentTimeMillis());
                } else if (System.currentTimeMillis() - clear.get(sequence) > sequence.getActiveFrame().getFrameTime()) {
                    for (Button button : Button.buttons()) {
                        Control control = Control.byButton(button);
                        if (control != null && Gdx.input.isKeyPressed(control.getKeycode())) {
                            skip = true;
                            continue;
                        }
                    }
                    if (skip) continue;

                    clear.remove(sequence);
                    clearSequence(getPriority(sequence));
                    sequence.stop();
                    sequence.first();
                }
            }
        }

        CondensedFrame newCondensedFrame = new CondensedFrame();
        for (int priority = 0; priority <= getTopPriority(); priority++)
            if (contains(priority))
                newCondensedFrame.merge(getSequence(priority));
        for (Fader fader : FadersUI.faders())
            newCondensedFrame.merge(fader);

        if (targetCondensedFrame == null) {
            targetCondensedFrame = newCondensedFrame.clone();
            currentCondensedFrame = newCondensedFrame.clone();
            previousCondensedFrame = newCondensedFrame.clone();
        }

        if (!newCondensedFrame.equals(targetCondensedFrame)) {
            targetCondensedFrame = newCondensedFrame.clone();
            previousCondensedFrame = currentCondensedFrame.clone();
            condensedFrameTimestamp = System.currentTimeMillis();
        }

        if (!currentCondensedFrame.equals(targetCondensedFrame)) {
            currentCondensedFrame.calculate(targetCondensedFrame, previousCondensedFrame, condensedFrameTimestamp);
        }

        for (int address = 1; address <= DMX.MAX_CHANNELS; address++) {
            if (Programmer.activeFrameHasValueFor(address)) {
                visualiser.set(address, (int) Programmer.getActiveFrameValueFor(address));
            } else {
                visualiser.set(address, currentCondensedFrame.getValue(address));
            }
        }

        visualiser.set(13, Setting.byName(Setting.Name.HAZE).isTrue() ? 255 : 0);

        if (!Lights.output.isFrozen()) output.copy(visualiser);

        if (!getSection().equals(Section.SEQUENCE_PROGRAMMER))
            SequenceProgrammerUI.setSelected(SequenceProgrammerUI.Selected.NONE);
    }

    @Override
    public boolean draw(Renderer renderer) {
        UI allowInteract = null;
        for (UI ui : UIs()) {
            ui.setAllowInteract(false);
            if (ui.isVisible() && ui.containsMouse() && !isDragging())
                allowInteract = ui;
        }
        if (allowInteract != null) allowInteract.setAllowInteract(true);

        for (UI ui : UIs()) {
            if (ui.isVisible()) {
                long timestamp = System.currentTimeMillis();
                ui.draw(renderer, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                renderer.draw();
                TimingsUI.set(ui.getName(), ui.getName() + " draw(): %mms %zms %ams", System.currentTimeMillis() - timestamp);
            } else {
                TimingsUI.clear(ui.getName());
            }
        }

        if (isDragging()) {
            getDragging().setLocation(Gdx.input.getX() - xDifference, Gdx.input.getY() - yDifference);
            if (!getDragging().containsMouse()) drag(null);
        }

        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        for (UI ui : UIs())
            ui.scrolled(amount);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        drag(null);

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Lights.keyboard.isShift();

        if (keycode == Input.Keys.SPACE) {
            tempo = System.currentTimeMillis() - lastTempo;
            lastTempo = System.currentTimeMillis();
        }

        if (Input.Keys.ESCAPE == keycode) Lights.output.toggleFreeze();

        for (UI ui : UIs()) {
            if (ui.isVisible() && ui.containsMouse() && ui.canInteract()) {
                if (!ui.keyDown(keycode)) return false;
            }
        }

        for (Button button : Button.buttons()) {
            Control control = Control.byButton(button);
            if (control != null && control.getKeycode() == keycode) {
                button.press();
            }
        }

        switch (keycode) {
            case Input.Keys.MINUS:
                MasterUI.fadeToZero();
                break;
            case Input.Keys.EQUALS:
                MasterUI.fadeToMax();
                break;
        }

        Fader fader = Fader.byID(shift ? 2 : 1);
        if (fader != null) {
            switch (keycode) {
                case Input.Keys.NUM_1:
                    fader.setValue(0.1f);
                    break;
                case Input.Keys.NUM_2:
                    fader.setValue(0.2f);
                    break;
                case Input.Keys.NUM_3:
                    fader.setValue(0.3f);
                    break;
                case Input.Keys.NUM_4:
                    fader.setValue(0.4f);
                    break;
                case Input.Keys.NUM_5:
                    fader.setValue(0.5f);
                    break;
                case Input.Keys.NUM_6:
                    fader.setValue(0.6f);
                    break;
                case Input.Keys.NUM_7:
                    fader.setValue(0.7f);
                    break;
                case Input.Keys.NUM_8:
                    fader.setValue(0.8f);
                    break;
                case Input.Keys.NUM_9:
                    fader.setValue(1.0f);
                    break;
                case Input.Keys.NUM_0:
                    fader.setValue(0.0f);
                    break;
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (UI ui : UIs())
            if (!ui.keyUp(keycode)) return false;

        return true;
    }

    public enum Section {
        DRAGGING,

        VISUALISER3D,

        PANEL_VISIBILITY,

        SETTINGS, CONTROLS, DMX_INTERFACE,

        PROFILES, PATCH,
        SEQUENCES, FADER_EDIT, BUTTON_EDIT,

        CONSOLE, DMX_OUTPUT,

        SEQUENCE_PROGRAMMER, NEW_SEQUENCE_PROGRAMMER,
        FIXTURES, GROUPS, COLOR_WHEEL,
        PARAMETERS,

        FROZEN,
        ACTIVE_SEQUENCES,
        TIMINGS,

        MASTER, FADERS, BUTTONS,
    }

    public static void drag(UI ui) {
        if (UIHandler.uiHandler.dragging == null && ui != null) {
            UIHandler.uiHandler.xDifference = Math.abs(ui.getX() - Gdx.input.getX());
            UIHandler.uiHandler.yDifference = Math.abs(ui.getYString() - Gdx.input.getY());
            moveToTop(ui);
        }

        UIHandler.uiHandler.dragging = ui;
    }

    public static boolean isDragging() {
        return getDragging() != null;
    }

    public static UI getDragging() {
        return uiHandler.dragging;
    }

    public static UI byName(String name) {
        for (UI ui : UIs())
            if (ui.getName().equals(name))
                return ui;

        return null;
    }

    public static void clearSequence(int priority) {
        uiHandler.sequenceStack.remove(priority);
    }

    public static void setSequence(int priority, Sequence sequence) {
        uiHandler.sequenceStack.put(priority, sequence);
    }

    public static boolean contains(int priority) {
        return uiHandler.sequenceStack.containsKey(priority);
    }

    public static Sequence getSequence(int priority) {
        return uiHandler.sequenceStack.getOrDefault(priority, null);
    }

    public static int getPriority(Sequence sequence) {
        for (int priority : uiHandler.sequenceStack.keySet())
            if (sequence.equals(getSequence(priority)))
                return priority;

        return -1;
    }

    public static List<Sequence> getSequences() {
        List<Sequence> sequences = new ArrayList<>();
        for (int priority = 0; priority <= getTopPriority(); priority++)
            if (contains(priority))
                sequences.add(getSequence(priority));
        return sequences;
    }

    public static void moveToTop(UI ui) {
        UIHandler.uiHandler.uis.remove(ui);
        UIHandler.uiHandler.uis.add(ui);
    }

    public static List<UI> UIs() {
        return new ArrayList<>(uiHandler.uis);
    }

    public static long getTempo() {
        return uiHandler.tempo;
    }

    public static int getTopPriority() {
        int highest = 0;
        for (int priority : uiHandler.sequenceStack.keySet())
            if (priority > highest) highest = priority;
        return highest;
    }

    public static void setMaster(float master) {
        uiHandler.master = Math.min(Math.max(master, 0), 1);
    }

    public static float getMaster() {
        return uiHandler.master;
    }

    public static void setSection(Section section) {
        uiHandler.lastSet = System.currentTimeMillis();
        uiHandler.section = section;
    }

    public static Section getSection() {
        if (isDragging()) return Section.DRAGGING;
        if (System.currentTimeMillis() - uiHandler.lastSet > 500)
            uiHandler.section = Section.VISUALISER3D;
        return uiHandler.section;
    }
}