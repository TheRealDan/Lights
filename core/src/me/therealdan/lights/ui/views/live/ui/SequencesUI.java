package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
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

        float x = getX();
        float y = getY();
        float width = getWidth();
        float cellHeight = 30;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Sequences"));
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (Sequence sequence : sequences(true)) {
            Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), setWidth(renderer, sequence.getName()));
            drag(x, y, width, cellHeight);
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        return interacted;
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