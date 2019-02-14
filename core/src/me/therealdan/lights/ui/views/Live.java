package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.controllers.Button;
import me.therealdan.lights.controllers.Fader;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.dmx.Output;
import me.therealdan.lights.programmer.CondensedFrame;
import me.therealdan.lights.programmer.Frame;
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.ui.views.live.Visualiser3D;
import me.therealdan.lights.ui.views.live.ui.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Live implements Tab {

    private static Live live;

    private Visualiser3D visualiser3D;

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

    public Live() {
        live = this;

        // Menu
        uis.add(new PanelVisibilityUI());

        // Settings
        uis.add(new SettingsUI());
        uis.add(new DMXInterfaceUI());

        // Setup
        uis.add(new ProfilesUI());
        uis.add(new PatchUI());
        uis.add(new SequencesUI());
        uis.add(new FaderEditUI());
        uis.add(new ButtonEditUI());

        // Util
        uis.add(new ConsoleUI());
        uis.add(new DMXOutputUI());

        // Programmer
        uis.add(new SequenceProgrammerUI());
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

        for (UI ui : UIs())
            ui.load();
    }

    @Override
    public void save() {
        for (UI ui : UIs())
            ui.save();

        getVisualiser3D().save();
    }

    @Override
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

            if (sequence.doesClear() && sequence.onLastFrame()) {
                if (!clear.containsKey(sequence)) {
                    clear.put(sequence, System.currentTimeMillis());
                } else if (System.currentTimeMillis() - clear.get(sequence) > sequence.getActiveFrame().getFrameTime()) {
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
            if (Programmer.hasValue(address)) {
                visualiser.set(address, (int) Programmer.getValue(address));
            } else {
                visualiser.set(address, currentCondensedFrame.getValue(address));
            }
        }

        if (!Output.isFrozen()) output.copy(visualiser);

        if (!getSection().equals(Section.SEQUENCE_PROGRAMMER)) SequenceProgrammerUI.setSelected(SequenceProgrammerUI.Selected.NONE);
    }

    @Override
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        getVisualiser3D().draw(Gdx.graphics.getDeltaTime());

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
                ui.draw(renderer, X, Y, WIDTH, HEIGHT);
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
    }

    @Override
    public void resize(int width, int height) {
        getVisualiser3D().resize(width, height);
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
    public boolean keyUp(int keycode) {
        getVisualiser3D().keyUp(keycode);

        for (UI ui : UIs())
            ui.keyUp(keycode);

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        getVisualiser3D().keyDown(keycode);

        if (keycode == Input.Keys.SPACE) {
            tempo = System.currentTimeMillis() - lastTempo;
            lastTempo = System.currentTimeMillis();
        }

        if (Input.Keys.ESCAPE == keycode) Output.toggleFreeze();

        for (UI ui : UIs()) {
            if (ui.isVisible() && ui.containsMouse() && ui.canInteract()) {
                if (ui.keyDown(keycode)) return true;
            }
        }

        Button button = Hotkeys.getButton(keycode);
        if (button != null) button.press();

        Fader fader = Fader.byID(shift ? 2 : 1);
        if (fader != null) {
            switch (keycode) {
                case Input.Keys.MINUS:
                    MasterUI.fadeToZero();
                    break;
                case Input.Keys.EQUALS:
                    MasterUI.fadeToMax();
                    break;
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
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        getVisualiser3D().touchDragged(screenX, screenY, pointer);
        return true;
    }

    private Visualiser3D getVisualiser3D() {
        if (visualiser3D == null) visualiser3D = new Visualiser3D();
        return visualiser3D;
    }

    public enum Section {
        DRAGGING,

        VISUALISER3D,

        PANEL_VISIBILITY,

        SETTINGS, DMX_INTERFACE,

        PROFILES, PATCH,
        SEQUENCES, FADER_EDIT, BUTTON_EDIT,

        CONSOLE, DMX_OUTPUT,

        SEQUENCE_PROGRAMMER,
        FIXTURES, GROUPS, COLOR_WHEEL,
        PARAMETERS,

        FROZEN,
        ACTIVE_SEQUENCES,
        TIMINGS,

        MASTER, FADERS, BUTTONS,
    }

    public static void drag(UI ui) {
        if (live.dragging == null && ui != null) {
            live.xDifference = Math.abs(ui.getX() - Gdx.input.getX());
            live.yDifference = Math.abs(ui.getYString() - Gdx.input.getY());
            moveToTop(ui);
        }

        live.dragging = ui;
    }

    public static boolean isDragging() {
        return getDragging() != null;
    }

    public static UI getDragging() {
        return live.dragging;
    }

    public static void clearSequence(int priority) {
        live.sequenceStack.remove(priority);
    }

    public static void setSequence(int priority, Sequence sequence) {
        live.sequenceStack.put(priority, sequence);
    }

    public static boolean contains(int priority) {
        return live.sequenceStack.containsKey(priority);
    }

    public static Sequence getSequence(int priority) {
        return live.sequenceStack.getOrDefault(priority, null);
    }

    public static int getPriority(Sequence sequence) {
        for (int priority : live.sequenceStack.keySet())
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
        live.uis.remove(ui);
        live.uis.add(ui);
    }

    public static List<UI> UIs() {
        return new ArrayList<>(live.uis);
    }

    public static long getTempo() {
        return live.tempo;
    }

    public static int getTopPriority() {
        int highest = 0;
        for (int priority : live.sequenceStack.keySet())
            if (priority > highest) highest = priority;
        return highest;
    }

    public static void setMaster(float master) {
        live.master = Math.min(Math.max(master, 0), 1);
    }

    public static float getMaster() {
        return live.master;
    }

    public static void setSection(Section section) {
        live.lastSet = System.currentTimeMillis();
        live.section = section;
    }

    public static Section getSection() {
        if (isDragging()) return Section.DRAGGING;
        if (System.currentTimeMillis() - live.lastSet > 500)
            live.section = Section.VISUALISER3D;
        return live.section;
    }
}