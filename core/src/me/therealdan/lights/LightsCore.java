package me.therealdan.lights;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import me.therealdan.lights.dmx.Output;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.ViewBar;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.ui.views.*;

public class LightsCore extends ApplicationAdapter {

    private static LightsCore lightsCore;
    private static Color text, light, medium, dark;

    private Renderer renderer;
    private MenuBar menuBar;
    private ViewBar viewBar;

    public static Color BLACK = new Color(0f, 0f, 0f, 1);
    public static Color WHITE = new Color(1f, 1f, 1f, 1);

    public static Color RED = new Color(1f, 0.25f, 0.25f, 1);
    public static Color GREEN = new Color(0.25f, 1f, 0.25f, 1);
    public static Color BLUE = new Color(0.25f, 0.25f, 1, 1);
    public static Color MAGENTA = new Color(1f, 0.25f, 1f, 1);
    public static Color YELLOW = new Color(1f, 1f, 0.25f, 1);
    public static Color CYAN = new Color(0.25f, 1f, 1f, 1);

    public static Color PURPLE = new Color(0.5f, 0.25f, 1f, 1);
    public static Color ORANGE = new Color(1f, 0.5f, 0.25f, 1);

    public static Color DARK_RED = new Color(0.5f, 0.1f, 0.1f, 1);
    public static Color DARK_GREEN = new Color(0.1f, 0.5f, 0.1f, 1);
    public static Color DARK_BLUE = new Color(0.1f, 0.1f, 0.5f, 1);
    public static Color DARK_MAGENTA = new Color(0.5f, 0.1f, 0.5f, 1);
    public static Color DARK_YELLOW = new Color(0.5f, 0.5f, 0.1f, 1);
    public static Color DARK_CYAN = new Color(0.1f, 0.5f, 0.5f, 1);

    private static boolean mouseUp = true;
    private static long lastAction = System.currentTimeMillis();

    @Override
    public void create() {
        lightsCore = this;

        renderer = new Renderer();
        text = new Color(1, 1, 1, 1);
        light = new Color(0.6f, 0.6f, 0.6f, 1);
        medium = new Color(0.2f, 0.2f, 0.2f, 1);
        dark = new Color(0.1f, 0.1f, 0.1f, 1);

        Tab.register(new Live());
        Tab.register(new Sequences());
        Tab.register(new Faders());
        Tab.register(new Hotkeys());

        viewBar = new ViewBar();
        for (Tab tab : Tab.values()) getViewBar().register(tab);
        getViewBar().setActiveTab(getViewBar().getFirstTab());

        menuBar = new MenuBar();

        new Output();

        Gdx.graphics.setVSync(true);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        controls();

        for (Tab tab : Tab.values())
            tab.update();

        getViewBar().getActiveTab().draw(renderer, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.draw();
    }

    private void controls() {
        // Cycle Tabs
        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE))
            getViewBar().nextTab(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT));

        // Handle current Tab
        Gdx.graphics.setTitle(getViewBar().getActiveTab().getName());
        Gdx.input.setInputProcessor(getViewBar().getActiveTab());

        // Mouse up
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) mouseUp = true;
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize();
        for (Tab tab : Tab.values()) tab.resize(width, height);
    }

    @Override
    public void dispose() {
        menuBar.dispose();
        renderer.dispose();

        for (Tab tab : Tab.values())
            tab.save();
    }

    public ViewBar getViewBar() {
        return viewBar;
    }

    public static float edge() {
        return 15;
    }

    public static boolean actionReady(long milliseconds) {
        if (milliseconds == -1) {
            if (mouseUp) return true;
            mouseUp = false;
            return false;
        }

        if (mouseUp || System.currentTimeMillis() - lastAction > milliseconds) {
            mouseUp = false;
            lastAction = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public static Color text() {
        return text;
    }

    public static Color light() {
        return light;
    }

    public static Color medium() {
        return medium;
    }

    public static Color dark() {
        return dark;
    }

    public static LightsCore getInstance() {
        return lightsCore;
    }

}