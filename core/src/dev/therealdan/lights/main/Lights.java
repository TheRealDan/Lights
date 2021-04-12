package dev.therealdan.lights.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import dev.therealdan.lights.dmx.Output;
import dev.therealdan.lights.fixtures.fixture.Profile;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.settings.ControlsStore;
import dev.therealdan.lights.settings.SettingsStore;
import dev.therealdan.lights.ui.*;

public class Lights extends ApplicationAdapter {

    private static Lights lights;

    private SettingsStore _settingsStore;
    private ControlsStore _controlsStore;
    private Mouse _mouse;
    private Renderer _renderer;
    private Output _output;
    private TheInputProcessor _theInputProcessor;

    private Visualiser3D visualiser3D;
    private PanelHandler panelHandler;
    private ProfileEditor profileEditor;
    private FixtureEditor fixtureEditor;

    @Override
    public void create() {
        lights = this;

        _settingsStore = new SettingsStore();
        _controlsStore = new ControlsStore();
        _mouse = new Mouse();
        _renderer = new Renderer();
        _output = new Output(_settingsStore);
        _theInputProcessor = new TheInputProcessor();

        panelHandler = new PanelHandler(_settingsStore, _controlsStore, _mouse, _renderer.getTheme(), _output);
        visualiser3D = new Visualiser3D(_settingsStore, _controlsStore, _output);
        profileEditor = new ProfileEditor();
        fixtureEditor = new FixtureEditor();

        Gdx.graphics.setVSync(true);
        Gdx.input.setInputProcessor(_theInputProcessor);

        openMainView();
    }

    @Override
    public void render() {
        Color background = _renderer.getTheme().BACKGROUND;
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        _mouse.update();
        panelHandler.update();

        _theInputProcessor.draw(_mouse, _renderer);

        _renderer.draw();
    }

    @Override
    public void resize(int width, int height) {
        _renderer.resize();

        _theInputProcessor.resize(width, height);
    }

    @Override
    public void dispose() {
        _settingsStore.saveToFile();
        _controlsStore.saveToFile();

        panelHandler.save();
        visualiser3D.save();

        _renderer.dispose();
    }

    public static void openMainView() {
        lights._theInputProcessor.clear();
        lights._theInputProcessor.add(lights.visualiser3D);
        lights._theInputProcessor.add(lights.panelHandler);
    }

    public static void openProfileEditor(Profile profile) {
        lights.profileEditor.edit(profile);
        lights._theInputProcessor.clear();
        lights._theInputProcessor.add(lights.profileEditor);
    }

    public static void openFixtureEditor() {
        lights.fixtureEditor.clear();
        lights._theInputProcessor.clear();
        lights._theInputProcessor.add(lights.fixtureEditor);
    }
}