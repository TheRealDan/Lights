package dev.therealdan.lights.main;

import com.badlogic.gdx.graphics.Color;

public class Theme {

    public Color TEXT;
    public Color LIGHT;
    public Color MEDIUM;
    public Color DARK;

    public Color BACKGROUND = new Color(0.05f, 0.05f, 0.05f, 1);

    public Color BLACK = new Color(0f, 0f, 0f, 1);
    public Color WHITE = new Color(1f, 1f, 1f, 1);

    public Color RED = new Color(1f, 0.25f, 0.25f, 1);
    public Color GREEN = new Color(0.25f, 1f, 0.25f, 1);
    public Color BLUE = new Color(0.25f, 0.25f, 1, 1);
    public Color MAGENTA = new Color(1f, 0.25f, 1f, 1);
    public Color YELLOW = new Color(1f, 1f, 0.25f, 1);
    public Color CYAN = new Color(0.25f, 1f, 1f, 1);

    public Color PURPLE = new Color(0.5f, 0.25f, 1f, 1);
    public Color ORANGE = new Color(1f, 0.5f, 0.25f, 1);

    public Color DARK_RED = new Color(0.5f, 0.1f, 0.1f, 1);
    public Color DARK_GREEN = new Color(0.1f, 0.5f, 0.1f, 1);
    public Color DARK_BLUE = new Color(0.1f, 0.1f, 0.5f, 1);
    public Color DARK_MAGENTA = new Color(0.5f, 0.1f, 0.5f, 1);
    public Color DARK_YELLOW = new Color(0.5f, 0.5f, 0.1f, 1);
    public Color DARK_CYAN = new Color(0.1f, 0.5f, 0.5f, 1);

    protected Theme() {
        TEXT = new Color(1, 1, 1, 1);
        LIGHT = new Color(0.6f, 0.6f, 0.6f, 1);
        MEDIUM = new Color(0.2f, 0.2f, 0.2f, 1);
        DARK = new Color(0.1f, 0.1f, 0.1f, 1);

        // TODO - Make these configurable
    }
}