package me.therealdan.lights.controllers;

import com.badlogic.gdx.graphics.Color;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.ui.views.Buttons;
import me.therealdan.lights.ui.views.Live;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Button {

    private int id;
    private String name;
    private Color color;
    private LinkedHashMap<Sequence, Integer> sequences = new LinkedHashMap<>();

    public Button() {
        this("New Button", LightsCore.medium());
    }

    public Button(String name, Color color) {
        this.id = getFreeID();
        this.name = name;
        this.color = new Color(color);
    }

    public void press() {
        for (Sequence sequence : sequences()) {
            sequence.resetLastFrame();
            Live.setSequence(getPriority(sequence), sequence);
        }
    }

    public void rename(String name) {
        this.name = name;
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

    public int getPosition() {
        for (int position = 0; position <= Buttons.getTopPosition(); position++)
            if (this.equals(Buttons.getButton(position)))
                return position;
        return -1;
    }

    public int getRow() {
        for (int row = 0; row <= Buttons.ROWS; row++)
            for (int column = 0; column <= Buttons.COLUMNS; column++)
                if (this.equals(Buttons.getButton(row, column)))
                    return row;
        return -1;
    }

    public int getColumn() {
        for (int row = 0; row <= Buttons.ROWS; row++)
            for (int column = 0; column <= Buttons.COLUMNS; column++)
                if (this.equals(Buttons.getButton(row, column)))
                    return column;
        return -1;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public List<Sequence> sequences() {
        return new ArrayList<>(sequences.keySet());
    }

    private static int getFreeID() {
        int id = 1;

        while (true) {
            if (byID(id) == null) return id;
            id++;
        }
    }

    public static Button byID(int id) {
        for (Button button : Buttons.buttons()) {
            if (button.getID() == id)
                return button;
        }

        return null;
    }
}