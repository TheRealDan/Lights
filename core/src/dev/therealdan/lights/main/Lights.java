package dev.therealdan.lights.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import dev.therealdan.lights.dmx.Output;
import dev.therealdan.lights.fixtures.fixture.Profile;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.settings.Control;
import dev.therealdan.lights.settings.Setting;
import dev.therealdan.lights.ui.*;

public class Lights extends ApplicationAdapter {

    private static Lights lights;

    public static Colour color;
    public static Keyboard keyboard;
    public static Mouse mouse;
    public static Output output;

    private Renderer renderer;
    private TheInputProcessor theInputProcessor;

    private Visualiser3D visualiser3D;
    private UIHandler uiHandler;
    private ProfileEditor profileEditor;
    private FixtureEditor fixtureEditor;

    @Override
    public void create() {
        lights = this;

        color = new Colour();
        keyboard = new Keyboard();
        mouse = new Mouse();
        output = new Output();

        Setting.createSettings();
        Setting.loadFromFile();

        Control.createControls();
        Control.loadFromFile();

        renderer = new Renderer();
        theInputProcessor = new TheInputProcessor();

        uiHandler = new UIHandler();
        visualiser3D = new Visualiser3D();
        profileEditor = new ProfileEditor();
        fixtureEditor = new FixtureEditor();

        Gdx.graphics.setVSync(true);
        Gdx.input.setInputProcessor(theInputProcessor);

        output.start();

        openMainView();
    }

    @Override
    public void render() {
        Color background = Lights.color.BACKGROUND;
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        mouse.update();
        uiHandler.update();

        theInputProcessor.draw(renderer);

        renderer.draw();
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize();

        theInputProcessor.resize(width, height);
    }

    @Override
    public void dispose() {
        Setting.saveToFile();
        Control.saveToFile();

        uiHandler.save();
        visualiser3D.save();

        renderer.dispose();
    }

    public static void openMainView() {
        lights.theInputProcessor.clear();
        lights.theInputProcessor.add(lights.visualiser3D);
        lights.theInputProcessor.add(lights.uiHandler);
    }

    public static void openProfileEditor(Profile profile) {
        lights.profileEditor.edit(profile);
        lights.theInputProcessor.clear();
        lights.theInputProcessor.add(lights.profileEditor);
    }

    public static void openFixtureEditor() {
        lights.fixtureEditor.clear();
        lights.theInputProcessor.clear();
        lights.theInputProcessor.add(lights.fixtureEditor);
    }
}