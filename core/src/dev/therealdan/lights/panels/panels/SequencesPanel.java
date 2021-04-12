package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.fixtures.fixture.profile.Channel;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.programmer.Frame;
import dev.therealdan.lights.programmer.Programmer;
import dev.therealdan.lights.programmer.Sequence;
import dev.therealdan.lights.programmer.Task;
import dev.therealdan.lights.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public class SequencesPanel implements Panel {

    private static SequencesPanel sequencesUI;

    private final int MAX_ROWS = 13;

    private List<Sequence> sequences = new ArrayList<>();

    private Sequence selectedSequence;
    private Section section;

    private List<Frame> selectedFrames = new ArrayList<>();

    private Sequence sequencesScroll = null;
    private boolean canScrollSequences = false;
    private int framesScroll = 0;
    private boolean canScrollFrames = false;
    private int tasksScroll = 0;
    private boolean canScrollTasks = false;

    public SequencesPanel() {
        sequencesUI = this;

        register(new CloseIcon());

        FileHandle fileHandle = Gdx.files.local("Lights/Sequences/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle child : fileHandle.list())
                load(child);
    }

    private void load(FileHandle fileHandle) {
        String name = fileHandle.name().replace(".txt", "");
        if (name.equals(".DS_Store")) return;
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
                            Fixture.fixtureByID(Integer.parseInt(args[1])),
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
        Panel.super.save();

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
        boolean interacted = false;
        boolean shift = Lights.keyboard.isShift();

        float x = getX();
        float y = getY();
        float sequencesWidth = renderer.getWidth("Sequences: " + countSequences()) + 10;
        float cellHeight = 30;

        for (Sequence sequence : sequences())
            sequencesWidth = Math.max(sequencesWidth, renderer.getWidth(sequence.getName()) + 25);

        renderer.box(x, y, getWidth(), getHeight(), renderer.getTheme().DARK);
        renderer.box(x, y, sequencesWidth, cellHeight, renderer.getTheme().DARK_BLUE, "Sequences: " + countSequences(), dev.therealdan.lights.renderer.Task.TextPosition.CENTER);
        drag(x, y, sequencesWidth, cellHeight);
        y -= cellHeight;
        canScrollSequences = Lights.mouse.contains(x, y, sequencesWidth, getHeight());

        int i = 0;
        boolean display = false;
        for (Sequence sequence : sequences(true)) {
            if (sequence.equals(getSequencesScroll())) display = true;
            if (display) {
                renderer.box(x, y, sequencesWidth, cellHeight, sequence.equals(getSelectedSequence()) ? renderer.getTheme().DARK_GREEN : renderer.getTheme().MEDIUM, setWidth(renderer, sequence.getName()));
                if (Lights.mouse.contains(x, y, sequencesWidth, cellHeight) && canInteract()) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                        select(sequence);
                    }
                }
                y -= cellHeight;
                if (++i == MAX_ROWS) break;
            }
        }

        setHeightBasedOnY(y);
        if (!hasSelectedSequence()) {
            setWidth(sequencesWidth);
            return interacted;
        }

        float optionsWidth = 300;

        x += sequencesWidth;
        y = getY();
        renderer.box(x, y, optionsWidth, cellHeight, renderer.getTheme().DARK_BLUE, "Sequence Options", dev.therealdan.lights.renderer.Task.TextPosition.CENTER);
        drag(x, y, optionsWidth, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, canEdit(Section.NAME) ? renderer.getTheme().DARK_GREEN : renderer.getTheme().MEDIUM, "Name: " + getSelectedSequence().getName());
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                if (canEdit(Section.NAME)) {
                    edit(null);
                } else {
                    edit(Section.NAME);
                }
            }
        }
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, hasSelectedSequence() && getSelectedSequence().doesLoop() ? renderer.getTheme().DARK_GREEN : renderer.getTheme().MEDIUM, "Loop");
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                if (hasSelectedSequence()) getSelectedSequence().toggleLoop();
            }
        }
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, hasSelectedSequence() && getSelectedSequence().doesClear() ? renderer.getTheme().DARK_GREEN : renderer.getTheme().MEDIUM, "Clear after play through");
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                if (hasSelectedSequence()) getSelectedSequence().toggleClear();
            }
        }
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, hasSelectedSequence() && getSelectedSequence().globalFrameTime() ? renderer.getTheme().DARK_GREEN : renderer.getTheme().MEDIUM, "Global Frame Time");
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                if (hasSelectedSequence()) getSelectedSequence().toggleGlobalFrameTime();
            }
        }
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, hasSelectedSequence() && getSelectedSequence().globalFadeTime() ? renderer.getTheme().DARK_GREEN : renderer.getTheme().MEDIUM, "Global Fade Time");
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                if (hasSelectedSequence()) getSelectedSequence().toggleGlobalFadeTime();
            }
        }
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, hasSelectedSequence() && getSelectedSequence().useTempo() ? renderer.getTheme().DARK_GREEN : renderer.getTheme().MEDIUM, "Global Tempo");
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                if (hasSelectedSequence()) getSelectedSequence().toggleUseTempo();
            }
        }
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, renderer.getTheme().MEDIUM, !hasAllSelected() ? "Select All" : "Deselect All");
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                if (!hasAllSelected()) {
                    for (Frame frame : getSelectedSequence().frames())
                        select(frame);
                } else {
                    deselectAllFrames();
                }
            }
        }
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, renderer.getTheme().MEDIUM, "Edit Sequence");
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                Programmer.edit(getSelectedSequence());
            }
        }
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, renderer.getTheme().MEDIUM, renderer.getTheme().RED, "Delete Sequence");
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shift && Lights.mouse.leftReady(500)) {
                if (hasSelectedSequence()) {
                    remove(getSelectedSequence());
                    return interacted;
                }
            }
        }
        y -= cellHeight;

        if (countSelectedFrames() > 0) {
            renderer.box(x, y, optionsWidth, cellHeight, renderer.getTheme().DARK_BLUE, "Frame Options", dev.therealdan.lights.renderer.Task.TextPosition.CENTER);
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

            renderer.box(x, y, optionsWidth, cellHeight, renderer.getTheme().MEDIUM, "Frame Time: " + (frameTime == -2 ? "Various" : Frame.format(frameTime)));
            if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                    if (Lights.mouse.contains(x, y, optionsWidth / 2, cellHeight)) {
                        for (Frame frame : selectedFrames())
                            frame.setFrameTime(frame.getFrameTime() + (shift ? 100 : 10));
                    } else {
                        for (Frame frame : selectedFrames())
                            frame.setFrameTime(frame.getFrameTime() - (shift ? 100 : 10));
                    }
                }
            }
            y -= cellHeight;

            renderer.box(x, y, optionsWidth, cellHeight, renderer.getTheme().MEDIUM, "Fade Time: " + (fadeTime == -2 ? "Various" : Frame.format(fadeTime)));
            if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                    if (Lights.mouse.contains(x, y, optionsWidth / 2, cellHeight)) {
                        for (Frame frame : selectedFrames())
                            frame.setFadeTime(frame.getFadeTime() + (shift ? 100 : 10));
                    } else {
                        for (Frame frame : selectedFrames())
                            frame.setFadeTime(frame.getFadeTime() - (shift ? 100 : 10));
                    }
                }
            }
            y -= cellHeight;

            renderer.box(x, y, optionsWidth, cellHeight, renderer.getTheme().MEDIUM, renderer.getTheme().RED, "Delete Frame" + (countSelectedFrames() > 1 ? "s" : ""));
            if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shift && Lights.mouse.leftReady(500)) {
                    for (Frame frame : selectedFrames())
                        getSelectedSequence().delete(frame);
                }
            }
            y -= cellHeight;
        }

        if (countSequences() < MAX_ROWS) setHeightBasedOnY(y);

        float framesWidth = 360;

        x += optionsWidth;
        y = getY();
        renderer.box(x, y, framesWidth, cellHeight, renderer.getTheme().DARK_BLUE, "Frames: " + countSelectedFrames() + " / " + getSelectedSequence().countFrames(), dev.therealdan.lights.renderer.Task.TextPosition.CENTER);
        drag(x, y, framesWidth, cellHeight);
        y -= cellHeight;
        canScrollFrames = Lights.mouse.contains(x, y, framesWidth, getHeight());

        i = 0;
        display = false;
        int current = 0;
        for (Frame frame : getSelectedSequence().frames()) {
            if (current == getFramesScroll()) display = true;
            current++;
            if (display) {
                renderer.box(x, y, framesWidth, cellHeight, isSelected(frame) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, frame.getInfo());
                if (Lights.mouse.contains(x, y, framesWidth, cellHeight) && canInteract()) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                        if (isSelected(frame)) {
                            deselect(frame);
                        } else {
                            select(frame);
                        }
                    }
                }
                y -= cellHeight;
                if (++i == MAX_ROWS) break;
                if (++i == MAX_ROWS) break;
            }
        }

        if (countSelectedFrames() != 1) {
            setWidth(sequencesWidth + optionsWidth + framesWidth);
            return interacted;
        }
        Frame frame = selectedFrames().get(0);

        float tasksWidth = 250;

        x += framesWidth;
        y = getY();
        renderer.box(x, y, tasksWidth, cellHeight, renderer.getTheme().DARK_BLUE, "Tasks: " + frame.tasks().size(), dev.therealdan.lights.renderer.Task.TextPosition.CENTER);
        drag(x, y, tasksWidth, cellHeight);
        y -= cellHeight;
        canScrollTasks = Lights.mouse.contains(x, y, tasksWidth, getHeight());

        i = 0;
        display = false;
        current = 0;
        for (Task task : frame.tasks()) {
            if (current == getTasksScroll()) display = true;
            current++;
            if (display) {
                renderer.box(x, y, tasksWidth, cellHeight, renderer.getTheme().MEDIUM, task.getInfo());
                drag(x, y, tasksWidth, cellHeight);
                y -= cellHeight;
                if (++i == MAX_ROWS) break;
            }
        }

        setWidth(sequencesWidth + optionsWidth + framesWidth + tasksWidth);
        return interacted;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Lights.keyboard.isShift();

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

    @Override
    public void scrolled(int amount) {
        if (canScrollSequences()) {
            if (amount > 0) {
                boolean next = false;
                int i = 0;
                for (Sequence sequence : sequences(true)) {
                    if (i++ > countSequences() - MAX_ROWS) return;
                    if (next) {
                        setSequencesScroll(sequence);
                        return;
                    }
                    if (sequence.equals(getSequencesScroll())) next = true;
                }
            } else {
                Sequence previous = null;
                for (Sequence sequence : sequences(true)) {
                    if (sequence.equals(getSequencesScroll()) && previous != null) {
                        setSequencesScroll(previous);
                        return;
                    }
                    previous = sequence;
                }
            }
        }

        if (canScrollFrames()) {
            if (amount > 0) {
                if (getFramesScroll() < getSelectedSequence().frames().size() - MAX_ROWS)
                    setFramesScroll(getFramesScroll() + 1);
            } else {
                setFramesScroll(getFramesScroll() - 1);
                if (getFramesScroll() < 0) setFramesScroll(0);
            }
        }

        if (canScrollTasks()) {
            if (amount > 0) {
                if (getTasksScroll() < selectedFrames().get(0).tasks().size() - MAX_ROWS)
                    setTasksScroll(getTasksScroll() + 1);
            } else {
                setTasksScroll(getTasksScroll() - 1);
                if (getTasksScroll() < 0) setTasksScroll(0);
            }
        }
    }

    private void select(Frame frame) {
        if (isSelected(frame)) return;
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

    private int countSelectedFrames() {
        return selectedFrames.size();
    }

    private List<Frame> selectedFrames() {
        return new ArrayList<>(selectedFrames);
    }

    private void select(Sequence sequence) {
        this.selectedSequence = sequence;
        deselectAllFrames();

        setFramesScroll(0);
        setTasksScroll(0);
    }

    private boolean hasSelectedSequence() {
        return getSelectedSequence() != null;
    }

    private boolean hasAllSelected() {
        for (Frame frame : getSelectedSequence().frames())
            if (!isSelected(frame))
                return false;

        return true;
    }

    private void edit(Section section) {
        this.section = section;
    }

    private Sequence getSelectedSequence() {
        return selectedSequence;
    }

    public void setSequencesScroll(Sequence sequence) {
        this.sequencesScroll = sequence;
    }

    private Sequence getSequencesScroll() {
        if (sequencesScroll == null) setSequencesScroll(sequences(true).get(0));
        if (!sequences().contains(sequencesScroll)) setSequencesScroll(sequences(true).get(0));
        return sequencesScroll;
    }

    public boolean canScrollSequences() {
        return canScrollSequences;
    }

    public void setFramesScroll(int frame) {
        this.framesScroll = frame;
    }

    public int getFramesScroll() {
        return framesScroll;
    }

    public boolean canScrollFrames() {
        return canScrollFrames;
    }

    public void setTasksScroll(int task) {
        this.tasksScroll = task;
    }

    public int getTasksScroll() {
        return tasksScroll;
    }

    public boolean canScrollTasks() {
        return canScrollTasks;
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
        sequencesUI.select((Sequence) null);
    }

    public static void add(Sequence sequence) {
        for (Sequence each : sequences())
            if (each.getName().equals(sequence.getName()))
                remove(each);

        sequencesUI.sequences.add(sequence);
    }

    public static int countSequences() {
        return sequencesUI.sequences.size();
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