package dev.therealdan.lights.controllers;

import com.badlogic.gdx.graphics.Color;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.programmer.Sequence;
import dev.therealdan.lights.ui.ui.FadersUI;

public class Fader {

    private float value = 0f;
    private int id;
    private String fileName;
    private String name;
    private Color color;
    private Type type = Type.MASTER;
    private Sequence sequence = new Sequence("No Sequence");

    public Fader(int id, String fileName) {
        this(id);
        this.fileName = fileName;
    }

    public Fader(int id) {
        this(id, "New fader", Lights.color.MEDIUM);
    }

    public Fader(int id, String fileName, String name, Color color) {
        this(id, name, color);
        this.fileName = fileName;
    }

    public Fader(int id, String name, Color color) {
        this.id = id;
        this.name = name;
        this.color = new Color(color);
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
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

    public void toggleType() {
        setType(getType().next());
    }

    public void rename(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setValue(float value) {
        this.value = Math.min(Math.max(value, 0), 1);
    }

    public float getValue() {
        return value;
    }

    public int getID() {
        return id;
    }

    public String getFileName() {
        if (fileName == null) fileName = getID() + "_" + getName();
        return fileName;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public enum Type {
        MASTER,
        INHIBITOR,
        AMBIENT,
        OVERRIDE;

        public Type next() {
            switch (this) {
                case MASTER:
                    return INHIBITOR;
                case INHIBITOR:
                    return AMBIENT;
                case AMBIENT:
                    return OVERRIDE;
                case OVERRIDE:
                    return MASTER;
            }
            return null;
        }

        public String getDescription() {
            switch (this) {
                case MASTER:
                    return "Multiplies against value in specified channel(s)";
                case INHIBITOR:
                    return "Sets upper limit for channel(s)";
                case AMBIENT:
                    return "Sets lower limit for channel(s)";
                case OVERRIDE:
                    return "Completely overrides channel(s)";

            }
            return getName() + "?";
        }

        public String getName() {
            String name = "";
            boolean capitalize = true;
            for (String letter : this.toString().replace("_", " ").toLowerCase().split("")) {
                if (capitalize) {
                    capitalize = false;
                    name += letter.toUpperCase();
                } else {
                    name += letter;
                }
                if (letter.equalsIgnoreCase(" ")) capitalize = true;
            }
            return name;
        }
    }

    public static int getFreeID() {
        int id = 1;

        while (true) {
            if (byID(id) == null) return id;
            id++;
        }
    }

    public static Fader byID(int id) {
        for (Fader fader : FadersUI.faders()) {
            if (fader.getID() == id)
                return fader;
        }

        return null;
    }
}