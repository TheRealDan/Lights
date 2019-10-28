package dev.therealdan.lights.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.controllers.Button;
import dev.therealdan.lights.controllers.Fader;
import dev.therealdan.lights.dmx.DMX;
import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.fixtures.Group;
import dev.therealdan.lights.fixtures.fixture.Profile;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.panels.*;
import dev.therealdan.lights.programmer.CondensedFrame;
import dev.therealdan.lights.programmer.Frame;
import dev.therealdan.lights.programmer.Programmer;
import dev.therealdan.lights.programmer.Sequence;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.settings.Control;
import dev.therealdan.lights.settings.Setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UIHandler implements Visual {

    private static UIHandler uiHandler;

    private List<Panel> panels = new ArrayList<>();
    private Panel dragging = null;
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
        panels.add(new PanelVisibilityPanel());

        // Settings
        panels.add(new SettingsPanel());
        panels.add(new ControlsPanel());
        panels.add(new DMXInterfacePanel());

        // Setup
        panels.add(new ProfilesPanel());

        // TODO - Move elsewhere
        Profile.loadProfilesFromFile();

        panels.add(new PatchPanel());

        // TODO - Move elsewhere
        Fixture.loadFixturesFromFile();
        Group.loadGroupsFromFile();

        panels.add(new SequencesPanel());
        panels.add(new FaderEditorPanel());
        panels.add(new ButtonEditorPanel());

        // Util
        panels.add(new ConsolePanel());
        panels.add(new DMXOutputPanel());

        // Programmer
        panels.add(new SequenceProgrammerPanel());
        panels.add(new NewSequenceProgrammerPanel());
        panels.add(new FixturesPanel());
        panels.add(new GroupsPanel());
        panels.add(new ParametersPanel());

        // Info
        panels.add(new FrozenPanel());
        panels.add(new ActiveSequencesPanel());
        panels.add(new TimingsPanel());

        // Panels
        panels.add(new MasterPanel());
        panels.add(new FadersPanel());
        panels.add(new ButtonsPanel());

        // TODO - Move elsewhere
        Button.loadButtonsFromFile();

        for (Panel panel : UIs())
            panel.load();
    }

    public void save() {
        for (Panel panel : UIs())
            panel.save();

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
        for (Fader fader : FadersPanel.faders())
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
            SequenceProgrammerPanel.setSelected(SequenceProgrammerPanel.Selected.NONE);
    }

    @Override
    public boolean draw(Renderer renderer) {
        Panel allowInteract = null;
        for (Panel panel : UIs()) {
            panel.setAllowInteract(false);
            if (panel.isVisible() && panel.containsMouse() && !isDragging())
                allowInteract = panel;
        }
        if (allowInteract != null) allowInteract.setAllowInteract(true);

        for (Panel panel : UIs()) {
            if (panel.isVisible()) {
                long timestamp = System.currentTimeMillis();
                panel.draw(renderer, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                renderer.draw();
                TimingsPanel.set(panel.getName(), panel.getName() + " draw(): %mms %zms %ams", System.currentTimeMillis() - timestamp);
            } else {
                TimingsPanel.clear(panel.getName());
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
        for (Panel panel : UIs())
            panel.scrolled(amount);

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

        for (Panel panel : UIs()) {
            if (panel.isVisible() && panel.containsMouse() && panel.canInteract()) {
                if (!panel.keyDown(keycode)) return false;
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
                MasterPanel.fadeToZero();
                break;
            case Input.Keys.EQUALS:
                MasterPanel.fadeToMax();
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
        for (Panel panel : UIs())
            if (!panel.keyUp(keycode)) return false;

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

    public static void drag(Panel panel) {
        if (UIHandler.uiHandler.dragging == null && panel != null) {
            UIHandler.uiHandler.xDifference = Math.abs(panel.getX() - Gdx.input.getX());
            UIHandler.uiHandler.yDifference = Math.abs(panel.getYString() - Gdx.input.getY());
            moveToTop(panel);
        }

        UIHandler.uiHandler.dragging = panel;
    }

    public static boolean isDragging() {
        return getDragging() != null;
    }

    public static Panel getDragging() {
        return uiHandler.dragging;
    }

    public static Panel byName(String name) {
        for (Panel panel : UIs())
            if (panel.getName().equals(name) || panel.getFriendlyName().equalsIgnoreCase(name))
                return panel;

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

    public static void moveToTop(Panel panel) {
        UIHandler.uiHandler.panels.remove(panel);
        UIHandler.uiHandler.panels.add(panel);
    }

    public static List<Panel> UIs() {
        return new ArrayList<>(uiHandler.panels);
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