package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.programmer.Frame;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.programmer.Task;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SequencesUI implements UI {

    private static SequencesUI sequencesUI;

    private List<Sequence> sequences = new ArrayList<>();

    private Sequence selectedSequence;
    private Section section;

    private List<Frame> selectedFrames = new ArrayList<>();

    public SequencesUI() {
        sequencesUI = this;

        FileHandle fileHandle = Gdx.files.local("Lights/Sequences/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle child : fileHandle.list())
                load(child);
    }

    private void load(FileHandle fileHandle) {
        String name = fileHandle.name().replace(".txt", "");
        Sequence sequence = new Sequence(name);

        Frame frame = null;

        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("Loop: ")) {
                sequence.loop(Boolean.parseBoolean(line.split(": ")[1]));
            } else if (line.startsWith("Clear: ")) {
                sequence.clear(Boolean.parseBoolean(line.split(": ")[1]));
            } else if (line.startsWith("Global Frame Time: ")) {
                sequence.globalFrameTime(Boolean.parseBoolean(line.split(": ")[1]));
            } else if (line.startsWith("Global Fade Time: ")) {
                sequence.globalFadeTime(Boolean.parseBoolean(line.split(": ")[1]));
            } else if (line.startsWith("Use Tempo: ")) {
                sequence.useTempo(Boolean.parseBoolean(line.split(": ")[1]));

            } else if (line.startsWith("  Frame ")) {
                if (frame != null) sequence.add(frame);
                frame = new Frame();

            } else if (line.startsWith("    Frame Time: ")) {
                if (frame != null) frame.setFrameTime(Long.parseLong(line.split(": ")[1]));
            } else if (line.startsWith("    Fade Time: ")) {
                if (frame != null) frame.setFadeTime(Long.parseLong(line.split(": ")[1]));

            } else if (line.startsWith("      |")) {
                if (frame != null) {
                    String[] args = line.split(" \\| ");
                    frame.set(
                            PatchUI.fixtureByID(Integer.parseInt(args[1])),
                            Channel.Type.valueOf(args[2]),
                            Float.parseFloat(args[4]),
                            Integer.parseInt(args[3])
                    );
                }
            }
        }
        if (frame != null) sequence.add(frame);
        add(sequence);
    }

    @Override
    public void save() {
        UI.super.save();

        for (Sequence sequence : sequences()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Sequences/" + sequence.getName() + ".txt");
            fileHandle.writeString("Loop: " + sequence.doesLoop() + "\r\n", false);
            fileHandle.writeString("Clear: " + sequence.doesClear() + "\r\n", true);
            fileHandle.writeString("Global Frame Time: " + sequence.globalFrameTime() + "\r\n", true);
            fileHandle.writeString("Global Fade Time: " + sequence.globalFadeTime() + "\r\n", true);
            fileHandle.writeString("Use Tempo: " + sequence.useTempo() + "\r\n", true);
            fileHandle.writeString("Frames:\r\n", true);
            int frameIndex = 0;
            for (Frame frame : sequence.frames()) {
                fileHandle.writeString("  Frame " + frameIndex + ":\r\n", true);
                fileHandle.writeString("    Frame Time: " + frame.getFrameTime() + "\r\n", true);
                fileHandle.writeString("    Fade Time: " + frame.getFadeTime() + "\r\n", true);
                fileHandle.writeString("    Tasks:\r\n", true);
                for (Task task : frame.tasks()) {
                    fileHandle.writeString("      | " + task.getFixture().getID() + " | " + task.getChannelType().toString() + " | " + task.getParameter() + " | " + task.getValue() + "\r\n", true);
                }
                frameIndex++;
            }
        }
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.SEQUENCES);
        boolean interacted = false;
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        float x = getX();
        float y = getY();
        float sequencesWidth = 0;
        float cellHeight = 30;

        for (Sequence sequence : sequences())
            sequencesWidth = Math.max(sequencesWidth, renderer.getWidth(sequence.getName()) + 25);

        Util.box(renderer, x, y, sequencesWidth, cellHeight, LightsCore.DARK_BLUE, "Sequences");
        drag(x, y, sequencesWidth, cellHeight);
        y -= cellHeight;

        for (Sequence sequence : sequences(true)) {
            Util.box(renderer, x, y, sequencesWidth, cellHeight, sequence.equals(getSelectedSequence()) ? LightsCore.DARK_GREEN : LightsCore.medium(), setWidth(renderer, sequence.getName()));
            if (Util.containsMouse(x, y, sequencesWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    select(sequence);
                }
            }
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        if (!hasSelectedSequence()) {
            setWidth(sequencesWidth);
            return interacted;
        }

        float optionsWidth = 300;

        x += sequencesWidth;
        y = getY();
        Util.box(renderer, x, y, optionsWidth, cellHeight, LightsCore.DARK_BLUE, "Sequence Options");
        drag(x, y, optionsWidth, cellHeight);
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, canEdit(Section.NAME) ? LightsCore.DARK_GREEN : LightsCore.medium(), "Name: " + getSelectedSequence().getName());
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                if (canEdit(Section.NAME)) {
                    edit(null);
                } else {
                    edit(Section.NAME);
                }
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, hasSelectedSequence() && getSelectedSequence().doesLoop() ? LightsCore.DARK_GREEN : LightsCore.medium(), "Loop");
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                if (hasSelectedSequence()) getSelectedSequence().toggleLoop();
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, hasSelectedSequence() && getSelectedSequence().doesClear() ? LightsCore.DARK_GREEN : LightsCore.medium(), "Clear after play through");
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                if (hasSelectedSequence()) getSelectedSequence().toggleClear();
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, hasSelectedSequence() && getSelectedSequence().globalFrameTime() ? LightsCore.DARK_GREEN : LightsCore.medium(), "Global Frame Time");
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                if (hasSelectedSequence()) getSelectedSequence().toggleGlobalFrameTime();
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, hasSelectedSequence() && getSelectedSequence().globalFadeTime() ? LightsCore.DARK_GREEN : LightsCore.medium(), "Global Fade Time");
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                if (hasSelectedSequence()) getSelectedSequence().toggleGlobalFadeTime();
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, hasSelectedSequence() && getSelectedSequence().useTempo() ? LightsCore.DARK_GREEN : LightsCore.medium(), "Global Tempo");
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                if (hasSelectedSequence()) getSelectedSequence().toggleUseTempo();
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, LightsCore.medium(), LightsCore.RED, "Delete Sequence");
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shift && LightsCore.actionReady(500)) {
                if (hasSelectedSequence()) remove(getSelectedSequence());
            }
        }
        y -= cellHeight;

        if (selectedFrames().size() > 0) {
            Util.box(renderer, x, y, optionsWidth, cellHeight, LightsCore.DARK_BLUE, "Frame Options");
            drag(x, y, optionsWidth, cellHeight);
            y -= cellHeight;

            long frameTime = -1;
            long fadeTime = -1;
            for (Frame frame : selectedFrames()) {
                if (frameTime == -1) frameTime = frame.getFrameTime();
                if (frameTime != frame.getFrameTime()) frameTime = -2;
                if (fadeTime == -1) fadeTime = frame.getFadeTime();
                if (fadeTime != frame.getFadeTime()) fadeTime = -2;
            }

            Util.box(renderer, x, y, optionsWidth, cellHeight, LightsCore.medium(), "Frame Time: " + (frameTime == -2 ? "Various" : Frame.format(frameTime)));
            if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                    if (Util.containsMouse(x, y, optionsWidth / 2, cellHeight)) {
                        for (Frame frame : selectedFrames())
                            frame.setFrameTime(frame.getFrameTime() + (shift ? 100 : 10));
                    } else {
                        for (Frame frame : selectedFrames())
                            frame.setFrameTime(frame.getFrameTime() - (shift ? 100 : 10));
                    }
                }
            }
            y -= cellHeight;

            Util.box(renderer, x, y, optionsWidth, cellHeight, LightsCore.medium(), "Fade Time: " + (fadeTime == -2 ? "Various" : Frame.format(fadeTime)));
            if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                    if (Util.containsMouse(x, y, optionsWidth / 2, cellHeight)) {
                        for (Frame frame : selectedFrames())
                            frame.setFadeTime(frame.getFadeTime() + (shift ? 100 : 10));
                    } else {
                        for (Frame frame : selectedFrames())
                            frame.setFadeTime(frame.getFadeTime() - (shift ? 100 : 10));
                    }
                }
            }
            y -= cellHeight;

            Util.box(renderer, x, y, optionsWidth, cellHeight, LightsCore.medium(), LightsCore.RED, "Delete Frame" + (selectedFrames().size() > 1 ? "s" : ""));
            if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shift && LightsCore.actionReady(500)) {
                    for (Frame frame : selectedFrames())
                        getSelectedSequence().delete(frame);
                }
            }
            y -= cellHeight;
        }

        float framesWidth = 400;

        x += optionsWidth;
        y = getY();
        Util.box(renderer, x, y, framesWidth, cellHeight, LightsCore.DARK_BLUE, "Frames: " + getSelectedSequence().frames().size());
        drag(x, y, framesWidth, cellHeight);
        y -= cellHeight;

        for (Frame frame : getSelectedSequence().frames()) {
            Util.box(renderer, x, y, framesWidth, cellHeight, isSelected(frame) ? LightsCore.DARK_RED : LightsCore.medium(), frame.getInfo());
            if (Util.containsMouse(x, y, framesWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                    if (isSelected(frame)) {
                        deselect(frame);
                    } else {
                        select(frame);
                    }
                }
            }
            y -= cellHeight;
        }

        if (selectedFrames().size() != 1) {
            setWidth(sequencesWidth + optionsWidth + framesWidth);
            return interacted;
        }
        Frame frame = selectedFrames().get(0);

        float tasksWidth = 250;

        x += framesWidth;
        y = getY();
        Util.box(renderer, x, y, tasksWidth, cellHeight, LightsCore.DARK_BLUE, "Tasks: " + frame.tasks().size());
        drag(x, y, tasksWidth, cellHeight);
        y -= cellHeight;

        for (Task task : frame.tasks()) {
            Util.box(renderer, x, y, tasksWidth, cellHeight, LightsCore.medium(), task.getInfo());
            drag(x, y, tasksWidth, cellHeight);
            y -= cellHeight;
        }

        setWidth(sequencesWidth + optionsWidth + framesWidth + tasksWidth);
        return interacted;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        if (canEdit(Section.NAME)) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getSelectedSequence().getName().length() > 0)
                        getSelectedSequence().rename(getSelectedSequence().getName().substring(0, getSelectedSequence().getName().length() - 1));
                    if (shift) getSelectedSequence().rename("");
                    break;
                case Input.Keys.SPACE:
                    getSelectedSequence().rename(getSelectedSequence().getName() + " ");
                    break;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                        if (!shift) string = string.toLowerCase();
                        getSelectedSequence().rename(getSelectedSequence().getName() + string);
                    }
            }
        }

        return true;
    }

    private void select(Frame frame) {
        this.selectedFrames.add(frame);
    }

    private void deselect(Frame frame) {
        this.selectedFrames.remove(frame);
    }

    private void deselectAllFrames() {
        this.selectedFrames.clear();
    }

    private boolean isSelected(Frame frame) {
        return selectedFrames().contains(frame);
    }

    private List<Frame> selectedFrames() {
        return new ArrayList<>(selectedFrames);
    }

    private void select(Sequence sequence) {
        this.selectedSequence = sequence;
        deselectAllFrames();
    }

    private boolean hasSelectedSequence() {
        return getSelectedSequence() != null;
    }

    private void edit(Section section) {
        this.section = section;
    }

    private Sequence getSelectedSequence() {
        return selectedSequence;
    }

    private boolean canEdit(Section section) {
        return section.equals(this.section);
    }

    public enum Section {
        NAME;
    }

    public static void remove(String name) {
        for (Sequence sequence : sequences()) {
            if (sequence.getName().equalsIgnoreCase(name)) {
                remove(sequence);
            }
        }
    }

    public static void remove(Sequence sequence) {
        sequencesUI.sequences.remove(sequence);
    }

    public static void add(Sequence sequence) {
        sequencesUI.sequences.add(sequence);
    }

    public static Sequence byName(String name) {
        for (Sequence sequence : sequences())
            if (sequence.getName().equalsIgnoreCase(name))
                return sequence;

        return null;
    }

    public static List<Sequence> sequences() {
        return sequences(false);
    }

    public static List<Sequence> sequences(boolean alphabeticalOrder) {
        if (!alphabeticalOrder) return new ArrayList<>(sequencesUI.sequences);

        List<Sequence> sequences = sequences(false);
        List<Sequence> alphabetical = new ArrayList<>();
        while (sequences.size() > 0) {
            Sequence first = null;
            for (Sequence sequence : sequences)
                if (first == null || first.getName().compareTo(sequence.getName()) > 0)
                    first = sequence;
            sequences.remove(first);
            alphabetical.add(first);
        }
        return alphabetical;
    }
}