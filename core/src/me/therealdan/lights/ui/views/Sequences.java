package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.programmer.Frame;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.programmer.Task;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.ui.views.live.ui.PatchUI;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Sequences implements Tab {

    private static Sequences instance;

    private List<Sequence> sequences = new ArrayList<>();
    private Sequence sequence = null;
    private Frame frame = null;

    private Section section = Section.NONE;
    private Edit edit = Edit.NONE;
    private int sequenceStart = 0, frameStart = 0, taskStart = 0;
    private int maxRows = 100;

    private final long delay = 1000;

    private String frameMilliseconds = "";
    private long frameTimestamp = System.currentTimeMillis();

    private String fadeMilliseconds = "";
    private long fadeTimestamp = System.currentTimeMillis();

    public Sequences() {
        instance = this;

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
    public boolean scrolled(int amount) {
        if (getSection() == null) return true;

        switch (getSection()) {
            case SEQUENCES:
                sequenceStart += amount;
                if (sequenceStart < 0) sequenceStart = 0;
                if (sequenceStart > sequences().size() - maxRows) sequenceStart = sequences().size() - maxRows;
                break;

            case FRAMES:
                frameStart += amount;
                if (frameStart < 0) frameStart = 0;
                if (frameStart > getSequence().frames().size() - maxRows) frameStart = getSequence().frames().size() - maxRows;
                break;

        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        switch (getEdit()) {
            case SEQUENCE_NAME:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        if (getSequence().getName().length() > 0)
                            getSequence().rename(getSequence().getName().substring(0, getSequence().getName().length() - 1));
                        if (shift) getSequence().rename("");
                        break;
                    case Input.Keys.SPACE:
                        getSequence().rename(getSequence().getName() + " ");
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                            if (!shift) string = string.toLowerCase();
                            getSequence().rename(getSequence().getName() + string);
                        }
                }
                break;

            case FRAME_TIME:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        if (getFrame() != null) getFrame().setFrameTime(0);
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("1234567890".contains(string)) {
                            frameMilliseconds += string;
                            frameTimestamp = System.currentTimeMillis();
                        }
                }
                break;

            case FADE_TIME:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        if (getFrame() != null) getFrame().setFadeTime(0);
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("1234567890".contains(string)) {
                            fadeMilliseconds += string;
                            fadeTimestamp = System.currentTimeMillis();
                        }
                }
                break;
        }

        return true;
    }

    @Override
    public void update() {
        Sequence sequence = getSequence();
        Frame frame = getFrame();

        if (frame != null) {
            if (System.currentTimeMillis() - frameTimestamp > delay && frameMilliseconds.length() > 0) {
                frame.setFrameTime(Long.parseLong(frameMilliseconds));
                frameMilliseconds = "";
            }

            if (System.currentTimeMillis() - fadeTimestamp > delay && fadeMilliseconds.length() > 0) {
                frame.setFadeTime(Long.parseLong(fadeMilliseconds));
                fadeMilliseconds = "";
            }
        }


        if (sequence != null && frame != null) {
            if (sequence.globalFrameTime()) {
                long frameTime = frame.getFrameTime();
                for (Frame each : sequence.frames())
                    if (each.getFrameTime() != frameTime)
                        each.setFrameTime(frameTime);
            }

            if (sequence.globalFadeTime()) {
                long fadeTime = frame.getFadeTime();
                for (Frame each : sequence.frames())
                    if (each.getFadeTime() != fadeTime)
                        each.setFadeTime(fadeTime);
            }
        }
    }

    @Override
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        float cellHeight = 30;

        float x = X + LightsCore.edge();
        float y = Y + HEIGHT - LightsCore.edge();

        float width = (WIDTH - LightsCore.edge() * 6f) / 5f;

        sequences(renderer, x, y, width, cellHeight);
        sequence(renderer, x + width + LightsCore.edge(), y, width, cellHeight);
        frames(renderer, x + width * 2f + LightsCore.edge() * 2f, y, width, cellHeight);
        frame(renderer, x + width * 3f + LightsCore.edge() * 3f, y, width, cellHeight);
        tasks(renderer, x + width * 4f + LightsCore.edge() * 4f, y, width, cellHeight);
    }

    private void sequences(Renderer renderer, float x, float y, float width, float cellHeight) {
        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Sequences");
        y -= cellHeight;

        int current = 0;
        boolean alternate = true;
        for (Sequence sequence : sequences(true)) {
            if (current - sequenceStart >= maxRows) break;
            if (current >= sequenceStart) {
                Util.box(renderer, x, y, width, cellHeight, sequence.equals(getSequence()) ? LightsCore.DARK_RED : alternate ? LightsCore.medium() : LightsCore.dark(), sequence.getName());
                if (Util.containsMouse(x, y, width, cellHeight)) {
                    setSection(Section.SEQUENCES);
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                        select(sequence);
                }
                y -= cellHeight;
                alternate = !alternate;
            }
            current++;
        }
    }

    private void sequence(Renderer renderer, float x, float y, float width, float cellHeight) {
        Sequence sequence = getSequence();
        if (sequence == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Sequence");
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, getEdit().equals(Edit.SEQUENCE_NAME) ? LightsCore.DARK_RED : LightsCore.medium(), "Name: " + sequence.getName());
        if (Util.containsMouse(x, y, width, cellHeight)) {
            setSection(Section.SEQUENCE);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                setEdit(Edit.SEQUENCE_NAME);
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, sequence.doesLoop() ? LightsCore.DARK_RED : LightsCore.medium(), "Loop: " + (sequence.doesLoop() ? "Enabled" : "Disabled"));
        if (Util.containsMouse(x, y, width, cellHeight)) {
            setSection(Section.SEQUENCE);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                sequence.toggleLoop();
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, sequence.doesClear() ? LightsCore.DARK_RED : LightsCore.medium(), "Clear: " + (sequence.doesClear() ? "Enabled" : "Disabled"));
        if (Util.containsMouse(x, y, width, cellHeight)) {
            setSection(Section.SEQUENCE);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                sequence.toggleClear();
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, sequence.useTempo() ? LightsCore.DARK_RED : LightsCore.medium(), "Use Global Tempo: " + (sequence.useTempo() ? "Enabled" : "Disabled"));
        if (Util.containsMouse(x, y, width, cellHeight)) {
            setSection(Section.SEQUENCE);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                sequence.toggleUseTempo();
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, sequence.globalFrameTime() ? LightsCore.DARK_RED : LightsCore.medium(), "Global Frame Time: " + (sequence.globalFrameTime() ? "Enabled" : "Disabled"));
        if (Util.containsMouse(x, y, width, cellHeight)) {
            setSection(Section.SEQUENCE);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                sequence.toggleGlobalFrameTime();
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, sequence.globalFadeTime() ? LightsCore.DARK_RED : LightsCore.medium(), "Global Fade Time: " + (sequence.globalFadeTime() ? "Enabled" : "Disabled"));
        if (Util.containsMouse(x, y, width, cellHeight)) {
            setSection(Section.SEQUENCE);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                sequence.toggleGlobalFadeTime();
        }
        y -= cellHeight;
    }

    private void frames(Renderer renderer, float x, float y, float width, float cellHeight) {
        Sequence sequence = getSequence();
        if (sequence == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, sequence.frames().size() == 1 ? "1 Frame" : sequence.frames().size() + " Frames");
        y -= cellHeight;

        int current = 0;
        boolean alternate = true;
        for (Frame frame : sequence.frames()) {
            if (current - frameStart >= maxRows) break;
            if (current >= frameStart) {
                Util.box(renderer, x, y, width, cellHeight, frame.equals(getFrame()) ? LightsCore.DARK_RED : alternate ? LightsCore.medium() : LightsCore.dark(), frame.getInfo());
                if (Util.containsMouse(x, y, width, cellHeight)) {
                    setSection(Section.FRAMES);
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                        select(frame);
                }
                y -= cellHeight;
                alternate = !alternate;
            }
            current++;
        }
    }

    private void frame(Renderer renderer, float x, float y, float width, float cellHeight) {
        Sequence sequence = getSequence();
        if (sequence == null) return;
        Frame frame = getFrame();
        if (frame == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Frame");
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, getEdit().equals(Edit.FRAME_TIME) ? LightsCore.DARK_RED : LightsCore.medium(), "Frame Time: " + Frame.format(frame.getFrameTime()));
        if (Util.containsMouse(x, y, width, cellHeight)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                setEdit(Edit.FRAME_TIME);
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, getEdit().equals(Edit.FADE_TIME) ? LightsCore.DARK_RED : LightsCore.medium(), "Fade Time: " + Frame.format(frame.getFadeTime()));
        if (Util.containsMouse(x, y, width, cellHeight)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                setEdit(Edit.FADE_TIME);
        }
        y -= cellHeight;
    }

    private void tasks(Renderer renderer, float x, float y, float width, float cellHeight) {
        Sequence sequence = getSequence();
        if (sequence == null) return;
        Frame frame = getFrame();
        if (frame == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, frame.tasks().size() == 1 ? "1 Task" : frame.tasks().size() + " Tasks");
        y -= cellHeight;

        int current = 0;
        boolean alternate = true;
        for (Task task : frame.tasks()) {
            if (current - taskStart >= maxRows) break;
            if (current >= taskStart) {
                Util.box(renderer, x, y, width, cellHeight, alternate ? LightsCore.medium() : LightsCore.dark(), task.getInfo());
                if (Util.containsMouse(x, y, width, cellHeight)) {
                    setSection(Section.TASKS);
                }
                y -= cellHeight;
                alternate = !alternate;
            }
            current++;
        }
    }

    private void select(Frame frame) {
        this.frame = frame;
    }

    private Frame getFrame() {
        return frame;
    }

    private void select(Sequence sequence) {
        this.sequence = sequence;
        this.frame = null;
    }

    private Sequence getSequence() {
        return sequence;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    private Section getSection() {
        return section;
    }

    private void setEdit(Edit edit) {
        this.edit = edit;
    }

    private Edit getEdit() {
        return edit;
    }

    public enum Section {
        NONE,

        SEQUENCES,

        SEQUENCE,

        FRAMES,

        TASKS;
    }

    public enum Edit {
        NONE,

        SEQUENCE_NAME,

        FRAME_TIME,
        FADE_TIME,
    }

    public static void remove(String name) {
        for (Sequence sequence : sequences()) {
            if (sequence.getName().equalsIgnoreCase(name)) {
                remove(sequence);
            }
        }
    }

    public static void remove(Sequence sequence) {
        instance.sequences.remove(sequence);
    }

    public static void add(Sequence sequence) {
        instance.sequences.add(sequence);
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
        if (!alphabeticalOrder) return new ArrayList<>(instance.sequences);

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