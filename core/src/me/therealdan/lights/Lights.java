package me.therealdan.lights;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import me.therealdan.lights.dmx.Output;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.settings.Control;
import me.therealdan.lights.settings.Setting;
import me.therealdan.lights.ui.DefaultInputProcessor;
import me.therealdan.lights.ui.Live;
import me.therealdan.lights.ui.Visualiser3D;

public class Lights extends ApplicationAdapter implements DefaultInputProcessor {

    private Renderer renderer;
    private Visualiser3D visualiser3D;
    private Live live;

    // COLORS

    private static Color text, light, medium, dark;

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

    // CLICK DETECTION

    private static boolean LEFT_MOUSE_UP = true;
    private static boolean RIGHT_MOUSE_UP = true;
    private static long LAST_ACTION = System.currentTimeMillis();

    @Override
    public void create() {
        text = new Color(1, 1, 1, 1);
        light = new Color(0.6f, 0.6f, 0.6f, 1);
        medium = new Color(0.2f, 0.2f, 0.2f, 1);
        dark = new Color(0.1f, 0.1f, 0.1f, 1);

        Setting.createSettings();
        Setting.loadFromFile();

        Control.createControls();
        Control.loadFromFile();

        renderer = new Renderer();
        live = new Live();
        visualiser3D = new Visualiser3D();

        new Output();

        Gdx.graphics.setVSync(true);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        controls();

        live.update();

        visualiser3D.draw(Gdx.graphics.getDeltaTime());
        live.draw(renderer, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        renderer.draw();
    }

    private void controls() {
        // Mouse up
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) LEFT_MOUSE_UP = true;
        if (!Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) RIGHT_MOUSE_UP = true;
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize();
        visualiser3D.resize(width, height);
    }

    @Override
    public void dispose() {
        renderer.dispose();

        Setting.saveToFile();
        Control.saveToFile();

        live.save();
        visualiser3D.save();
    }

    @Override
    public boolean keyUp(int keycode) {
        visualiser3D.keyUp(keycode);
        live.keyUp(keycode);

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        visualiser3D.keyDown(keycode);
        live.keyDown(keycode);

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        visualiser3D.touchDragged(screenX, screenY, pointer);

        return true;
    }

    public static boolean leftMouseClicked(long milliseconds, boolean override) {
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) return false;
        if (override) milliseconds = 0;
        return leftMouseReady(milliseconds);
    }

    public static boolean leftMouseReady(long milliseconds) {
        if (milliseconds == -1) return LEFT_MOUSE_UP;

        if (LEFT_MOUSE_UP || System.currentTimeMillis() - LAST_ACTION > milliseconds) {
            LEFT_MOUSE_UP = false;
            LAST_ACTION = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public static boolean rightMouseClicked(long milliseconds, boolean override) {
        if (!Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) return false;
        if (override) milliseconds = 0;
        return rightMouseReady(milliseconds);
    }

    public static boolean rightMouseReady(long milliseconds) {
        if (milliseconds == -1) return RIGHT_MOUSE_UP;

        if (RIGHT_MOUSE_UP || System.currentTimeMillis() - LAST_ACTION > milliseconds) {
            RIGHT_MOUSE_UP = false;
            LAST_ACTION = System.currentTimeMillis();
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
}