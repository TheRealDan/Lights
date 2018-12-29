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

    private String name;
    private Color color;
    private LinkedHashMap<Sequence, Integer> sequences = new LinkedHashMap<>();

    public Button() {
        this("New Button", LightsCore.medium());
    }

    public Button(String name, Color color) {
        this.name = name;
        this.color = new Color(color);
    }

    public void press() {
        for (Sequence sequence : sequences()) {
            sequence.resetLastFrame();
            Live.setSequence(getPriority(sequence), sequence);
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

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public List<Sequence> sequences() {
        return new ArrayList<>(sequences.keySet());
    }

}