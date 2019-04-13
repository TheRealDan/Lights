package me.therealdan.lights.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import me.therealdan.lights.dmx.Output;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.settings.Control;
import me.therealdan.lights.settings.Setting;
import me.therealdan.lights.ui.DefaultInputProcessor;
import me.therealdan.lights.ui.UIHandler;
import me.therealdan.lights.ui.Visualiser3D;

public class Lights extends ApplicationAdapter implements DefaultInputProcessor {

    public static Colour color;
    public static Mouse mouse;

    private Renderer renderer;
    private Visualiser3D visualiser3D;
    private UIHandler uiHandler;

    @Override
    public void create() {
        color = new Colour();
        mouse = new Mouse();

        Setting.createSettings();
        Setting.loadFromFile();

        Control.createControls();
        Control.loadFromFile();

        renderer = new Renderer();
        uiHandler = new UIHandler();
        visualiser3D = new Visualiser3D();

        new Output();

        Gdx.graphics.setVSync(true);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        mouse.update();

        uiHandler.update();

        visualiser3D.draw(Gdx.graphics.getDeltaTime());
        uiHandler.draw(renderer, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        renderer.draw();
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

        uiHandler.save();
        visualiser3D.save();
    }

    @Override
    public boolean keyUp(int keycode) {
        visualiser3D.keyUp(keycode);
        uiHandler.keyUp(keycode);

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        visualiser3D.keyDown(keycode);
        uiHandler.keyDown(keycode);

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        visualiser3D.touchDragged(screenX, screenY, pointer);

        return true;
    }
}