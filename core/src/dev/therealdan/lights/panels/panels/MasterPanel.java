package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.PanelHandler;
import dev.therealdan.lights.util.Util;

public class MasterPanel implements Panel {

    private static MasterPanel masterUI;

    public static float HEIGHT = 250;

    private long fadeTime = 1200;
    private long timestamp = System.currentTimeMillis();
    private float startingValue = 1.0f;
    private boolean fadeToMax = false;
    private boolean fadeToZero = false;

    public MasterPanel() {
        masterUI = this;

        register(new CloseIcon());
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        boolean interacted = false;

        setHeight(MasterPanel.HEIGHT);

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        renderer.box(x, y, width, cellHeight, renderer.getTheme().DARK_BLUE, setWidth(renderer, getFriendlyName()), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        String currentAction = PanelHandler.getMaster() > 0.0 ? "Fade to Zero" : "Fade to Max";
        if (isFadeToMax()) currentAction = "Fading to Max..";
        if (isFadeToZero()) currentAction = "Fading to Zero..";
        renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, setWidth(renderer, currentAction), Task.TextPosition.CENTER);
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(1000)) {
                interacted = true;
                toggle();
            }
        }
        y -= cellHeight;

        float height = getHeight() - cellHeight - cellHeight;
        renderer.box(x, y, width, height, renderer.getTheme().MEDIUM, setWidth(renderer, Util.getPercentage(PanelHandler.getMaster())), Task.TextPosition.CENTER);
        float fill = PanelHandler.getMaster() * height;
        renderer.box(x, y - height + fill, width, fill, renderer.getTheme().BLACK);

        if (Lights.mouse.contains(x, y, width, height) && canInteract()) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                interacted = true;
                float bottom = y - height + 20;
                PanelHandler.setMaster(Math.min(Math.max((Gdx.graphics.getHeight() - Gdx.input.getY() - bottom) / (y - 20 - bottom), 0), 1));
            }
        }

        if (isFadeToZero() || isFadeToMax()) {
            double timepassed = System.currentTimeMillis() - timestamp;
            double percentage = 0;
            if (timepassed > 0 && getFadeTime() > 0) percentage = timepassed / getFadeTime();
            if (percentage > 1) percentage = 1;

            if (isFadeToMax()) {
                PanelHandler.setMaster(startingValue + (float) percentage);
                if (PanelHandler.getMaster() == 1.0) fadeToMax = false;
            } else {
                PanelHandler.setMaster(startingValue - (float) percentage);
                if (PanelHandler.getMaster() == 0.0) fadeToZero = false;
            }
        }

        return interacted;
    }

    public void toggle() {
        if (isFadeToMax() || isFadeToZero()) {
            fadeToMax = false;
            fadeToZero = false;
            return;
        }

        if (PanelHandler.getMaster() == 0.0) {
            fadeToMax();
        } else {
            fadeToZero();
        }
    }

    public static void fadeToZero() {
        masterUI.timestamp = System.currentTimeMillis();
        masterUI.startingValue = PanelHandler.getMaster();
        masterUI.fadeToMax = false;
        masterUI.fadeToZero = true;
    }

    public static void fadeToMax() {
        masterUI.timestamp = System.currentTimeMillis();
        masterUI.startingValue = PanelHandler.getMaster();
        masterUI.fadeToMax = true;
        masterUI.fadeToZero = false;
    }

    public long getFadeTime() {
        return fadeTime;
    }

    public boolean isFadeToMax() {
        return fadeToMax;
    }

    public boolean isFadeToZero() {
        return fadeToZero;
    }
}