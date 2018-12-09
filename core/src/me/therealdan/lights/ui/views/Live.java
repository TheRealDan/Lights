package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.controllers.Button;
import me.therealdan.lights.controllers.Fader;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.dmx.Output;
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

    private long lastTempo = System.currentTimeMillis();
    private long tempo = 1000;

    public Live() {
        live = this;

        // Programmer
        uis.add(new FixturesUI());
        uis.add(new GroupsUI());
        uis.add(new SelectedFixturesUI());
        uis.add(new AvailableParametersUI());
        uis.add(new SelectedChannelsUI());
//        uis.add(new ColorWheelUI());
        uis.add(new SequenceProgrammerUI());

        // Info
        uis.add(new ActiveSequencesUI());
        uis.add(new FrozenUI());

        // Panels
        uis.add(new MasterUI());
        uis.add(new FadersUI());
        uis.add(new ButtonsUI());
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
        }

        Frame frame = new Frame();

        for (int priority = 0; priority <= getTopPriority(); priority++)
            if (contains(priority))
                frame.override(getSequence(priority));

        for (Fader fader : Faders.faders())
            frame.override(fader);

        for (int address = 1; address <= DMX.MAX_CHANNELS; address++) {
            if (Programmer.hasValue(address)) {
                visualiser.set(address, (int) Programmer.getValue(address));
            } else if (frame.hasValue(address)) {
                visualiser.set(address, (int) frame.getValue(address));
            } else {
                visualiser.set(address, 0);
            }
        }

        if (!Output.isFrozen()) output.copy(visualiser);

        if (!getSection().equals(Section.SEQUENCE_PROGRAMMER)) SequenceProgrammerUI.setSelected(SequenceProgrammerUI.Selected.NONE);
    }

    @Override
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        getVisualiser3D().draw(Gdx.graphics.getDeltaTime());

        for (UI ui : uis)
            if (ui.isVisible())
                if (ui.draw(renderer, X, Y, WIDTH, HEIGHT))
                    dragging = null;

        if (dragging != null) {
            dragging.setLocation(Gdx.input.getX() - xDifference, Gdx.input.getY() - yDifference);
            if (!dragging.containsMouse()) dragging = null;
        }
    }

    @Override
    public void resize(int width, int height) {
        getVisualiser3D().resize(width, height);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        dragging = null;

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (UI ui : uis) {
            if (ui.isVisible() && ui.containsMouse()) {
                xDifference = Math.abs(ui.getX() - screenX);
                yDifference = Math.abs(ui.getY() - screenY);
                dragging = ui;
                break;
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        getVisualiser3D().keyUp(keycode);

        for (UI ui : uis)
            ui.keyUp(keycode);


        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        getVisualiser3D().keyDown(keycode);

        if (keycode == Input.Keys.SPACE) {
            tempo = System.currentTimeMillis() - lastTempo;
            lastTempo = System.currentTimeMillis();
        }

        boolean containsMouse = false;
        for (UI ui : uis) {
            ui.keyDown(keycode);
            if (ui.containsMouse())
                containsMouse = true;
        }
        if (containsMouse) return true;

        if (Input.Keys.ESCAPE == keycode) Output.toggleFreeze();

        Button button = Hotkeys.getButton(keycode);
        if (button != null) button.press();

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

        MASTER, BUTTONS, FADERS,

        SEQUENCE_PROGRAMMER, FROZEN,

        ACTIVE_SEQUENCES,
        FIXTURES, GROUPS, SELECTED_FIXTURES, AVAILABLE_PARAMETERS, SELECTED_CHANNELS, COLOR_WHEEL,
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

    public static List<Sequence> getSequences() {
        List<Sequence> sequences = new ArrayList<>();
        for (int priority = 0; priority <= getTopPriority(); priority++)
            if (contains(priority))
                sequences.add(getSequence(priority));
        return sequences;
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
        if (live.dragging != null) return Section.DRAGGING;
        if (System.currentTimeMillis() - live.lastSet > 500)
            live.section = Section.VISUALISER3D;
        return live.section;
    }
}