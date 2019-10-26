package dev.therealdan.lights.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import dev.therealdan.lights.programmer.Sequence;
import dev.therealdan.lights.ui.UIHandler;
import dev.therealdan.lights.panels.panels.SequencesPanel;
import dev.therealdan.lights.util.sorting.Sortable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public class Button implements Sortable {

    private static HashSet<Button> buttons = new HashSet<>();

    private String fileName;
    private String name;
    private int id;
    private int position;
    private Color color;
    private LinkedHashMap<Sequence, Integer> sequences = new LinkedHashMap<>();

    protected Button() {
        this.color = new Color();
    }

    public Button(String name, Color color) {
        this.name = name;
        this.id = getFreeID();
        this.position = getFreePosition();
        this.color = new Color(color);
    }

    public void press() {
        for (Sequence sequence : sequences()) {
            sequence.resetLastFrame();
            UIHandler.setSequence(getPriority(sequence), sequence);
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setRed(float red) {
        color.set(
                red,
                color.g,
                color.b,
                color.a
        );
    }

    public void setGreen(float green) {
        color.set(
                color.r,
                green,
                color.b,
                color.a
        );
    }

    public void setBlue(float blue) {
        color.set(
                color.r,
                color.g,
                blue,
                color.a
        );
    }

    public void rename(String name) {
        this.name = name;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void set(Sequence sequence, int priority) {
        sequences.put(sequence, priority);
    }

    public void remove(Sequence sequence) {
        sequences.remove(sequence);
    }

    public boolean contains(Sequence sequence) {
        return sequences.containsKey(sequence);
    }

    public int getPriority(Sequence sequence) {
        return sequences.get(sequence);
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int getPosition() {
        return position;
    }

    public String getFileName() {
        if (fileName == null) fileName = getID() + "_" + getName();
        return fileName;
    }

    @Override
    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public List<Sequence> sequences() {
        return sequences(false);
    }

    public List<Sequence> sequences(boolean alphabeticalOrder) {
        if (!alphabeticalOrder) return new ArrayList<>(sequences.keySet());

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

    public static void add(Button button) {
        buttons.add(button);
    }

    public static void remove(Button button) {
        buttons.remove(button);
    }

    public static void loadButtonsFromFile() {
        FileHandle fileHandle = Gdx.files.local("Lights/Buttons/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle file : fileHandle.list())
                loadButtonFromFile(file);
    }

    private static void loadButtonFromFile(FileHandle fileHandle) {
        if (fileHandle.isDirectory()) return;

        Button button = new Button();
        button.fileName = fileHandle.toString().replaceFirst("Lights/Buttons/", "").replace(".txt", "");

        boolean sequences = false;
        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("Name: ")) {
                button.rename(line.split(": ")[1]);
            } else if (line.startsWith("ID: ")) {
                button.id = Integer.parseInt(line.split(": ")[1]);
            } else if (line.startsWith("Position: ")) {
                button.setPosition(Integer.parseInt(line.split(": ")[1]));
            } else if (line.startsWith("Color:")) {
                sequences = false;
            } else if (line.startsWith("  Red: ") && !sequences) {
                button.setRed(Float.parseFloat(line.split(": ")[1]));
            } else if (line.startsWith("  Green: ") && !sequences) {
                button.setGreen(Float.parseFloat(line.split(": ")[1]));
            } else if (line.startsWith("  Blue: ") && !sequences) {
                button.setBlue(Float.parseFloat(line.split(": ")[1]));
            } else if (line.startsWith("Sequences:")) {
                sequences = true;
            } else if (sequences) {
                String[] args = line.replaceFirst("  ", "").split(": ");
                button.set(
                        SequencesPanel.byName(args[0]),
                        Integer.parseInt(args[1])
                );
            }
        }

        add(button);
    }

    public static void saveButtonsToFile() {
        for (Button button : buttons()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Buttons/" + button.getFileName() + ".txt");
            fileHandle.writeString("", false);

            fileHandle.writeString("Name: " + button.getName() + "\r\n", true);
            fileHandle.writeString("ID: " + button.getID() + "\r\n", true);
            fileHandle.writeString("Position: " + button.getPosition() + "\r\n", true);
            fileHandle.writeString("Color:\r\n", true);
            fileHandle.writeString("  Red: " + button.getColor().r + "\r\n", true);
            fileHandle.writeString("  Green: " + button.getColor().g + "\r\n", true);
            fileHandle.writeString("  Blue: " + button.getColor().b + "\r\n", true);
            fileHandle.writeString("Sequences:\r\n", true);
            for (Sequence sequence : button.sequences()) {
                int priority = button.getPriority(sequence);
                fileHandle.writeString("  " + sequence.getName() + ": " + priority + "\r\n", true);
            }
        }
    }

    public static int getFreeID() {
        int id = 0;
        while (byID(id) != null) id++;
        return id;
    }

    public static int getFreePosition() {
        int position = 1;
        while (byPosition(position) != null) position++;
        return position;
    }

    public static int getTopPosition() {
        int topPosition = 0;
        for (Button button : buttons())
            if (button.getPosition() > topPosition)
                topPosition = button.getPosition();

        return topPosition;
    }

    public static int count() {
        return buttons.size();
    }

    public static Button byName(String name) {
        for (Button button : buttons())
            if (button.getName().equals(name))
                return button;

        return null;
    }

    public static Button byID(int id) {
        for (Button button : buttons())
            if (button.getID() == id)
                return button;

        return null;
    }

    public static Button byPosition(int position) {
        for (Button button : buttons())
            if (button.getPosition() == position)
                return button;

        return null;
    }

    public static List<Button> buttons(Sort... sort) {
        if (sort.length == 0) return new ArrayList<>(Button.buttons);

        List<Button> buttons = new ArrayList<>();
        for (Sortable sortable : Sortable.sort(new ArrayList<>(Button.buttons), sort))
            buttons.add((Button) sortable);
        return buttons;
    }
}