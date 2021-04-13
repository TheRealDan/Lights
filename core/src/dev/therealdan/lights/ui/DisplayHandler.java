package dev.therealdan.lights.ui;

import com.badlogic.gdx.InputProcessor;
import dev.therealdan.lights.dmx.Output;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.main.Theme;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.store.ControlsStore;
import dev.therealdan.lights.store.SettingsStore;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class DisplayHandler implements InputProcessor {

    private Visualiser3D _visualiser3D;
    private PanelHandler _panelHandler;
    private ProfileEditor _profileEditor;
    private FixtureEditor _fixtureEditor;

    private Focus _focus = Focus.MAIN_VIEW;

    public DisplayHandler(SettingsStore settingsStore, ControlsStore controlsStore, Mouse mouse, Theme theme, Output output) {
        _visualiser3D = new Visualiser3D(settingsStore, controlsStore, output);
        _panelHandler = new PanelHandler(settingsStore, controlsStore, mouse, theme, output, this);
        _profileEditor = new ProfileEditor(this);
        _fixtureEditor = new FixtureEditor(this);
    }

    public void save() {
        _panelHandler.save();
        _visualiser3D.save();
    }

    public void update() {
        _panelHandler.update();
    }

    public void draw(Mouse mouse, Renderer renderer) {
        Iterator<Visual> iterator = getActive(false);
        while (iterator.hasNext())
            if (!iterator.next().draw(mouse, renderer))
                return;
    }

    public void resize(int width, int height) {
        Iterator<Visual> iterator = getAll(false);
        while (iterator.hasNext())
            iterator.next().resize(width, height);
    }

    @Override
    public boolean keyDown(int keycode) {
        Iterator<Visual> iterator = getActive(true);
        while (iterator.hasNext())
            if (!iterator.next().keyDown(keycode)) return false;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        Iterator<Visual> iterator = getActive(true);
        while (iterator.hasNext())
            if (!iterator.next().keyUp(keycode)) return false;
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        Iterator<Visual> iterator = getActive(true);
        while (iterator.hasNext())
            if (!iterator.next().keyTyped(character)) return false;
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Iterator<Visual> iterator = getActive(true);
        while (iterator.hasNext())
            if (!iterator.next().touchDown(screenX, screenY, pointer, button)) return false;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Iterator<Visual> iterator = getActive(true);
        while (iterator.hasNext())
            if (!iterator.next().touchUp(screenX, screenY, pointer, button)) return false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Iterator<Visual> iterator = getActive(true);
        while (iterator.hasNext())
            if (!iterator.next().touchDragged(screenX, screenY, pointer)) return false;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Iterator<Visual> iterator = getActive(true);
        while (iterator.hasNext())
            if (!iterator.next().mouseMoved(screenX, screenY)) return false;
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        Iterator<Visual> iterator = getActive(true);
        while (iterator.hasNext())
            if (!iterator.next().scrolled(amount)) return false;
        return true;
    }

    public Iterator<Visual> getActive(boolean reverse) {
        Deque<Visual> visuals = new ArrayDeque<>();
        switch (getFocus()) {
            case MAIN_VIEW:
                visuals.add(_visualiser3D);
                visuals.add(_panelHandler);
                break;
            case PROFILE_EDITOR:
                visuals.add(_profileEditor);
                break;
            case FIXTURE_EDITOR:
                visuals.add(_fixtureEditor);
                break;
        }
        return reverse ? visuals.descendingIterator() : visuals.iterator();
    }

    public Iterator<Visual> getAll(boolean reverse) {
        Deque<Visual> visuals = new ArrayDeque<>();
        visuals.add(_visualiser3D);
        visuals.add(_panelHandler);
        visuals.add(_profileEditor);
        visuals.add(_fixtureEditor);
        return reverse ? visuals.descendingIterator() : visuals.iterator();
    }

    public void setFocus(Focus focus) {
        _focus = focus;
    }

    public Focus getFocus() {
        return _focus;
    }

    public enum Focus {
        MAIN_VIEW,
        PROFILE_EDITOR,
        FIXTURE_EDITOR
    }
}