package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.Live;
import me.therealdan.lights.util.Util;

public class MasterUI implements UI {

    private static MasterUI masterUI;

    public static float HEIGHT = 250;

    private long fadeTime = 1200;
    private long timestamp = System.currentTimeMillis();
    private float startingValue = 1.0f;
    private boolean fadeToMax = false;
    private boolean fadeToZero = false;

    public MasterUI() {
        masterUI = this;
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.MASTER);
        boolean interacted = false;

        setHeight(MasterUI.HEIGHT);

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Master"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        String currentAction = Live.getMaster() > 0.0 ? "Fade to Zero" : "Fade to Max";
        if (isFadeToMax()) currentAction = "Fading to Max..";
        if (isFadeToZero()) currentAction = "Fading to Zero..";
        Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), setWidth(renderer, currentAction), Task.TextPosition.CENTER);
        if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.leftMouseReady(1000)) {
                interacted = true;
                toggle();
            }
        }
        y -= cellHeight;

        float height = getHeight() - cellHeight - cellHeight;
        Util.box(renderer, x, y, width, height, LightsCore.medium(), setWidth(renderer, Util.getPercentage(Live.getMaster())), Task.TextPosition.CENTER);
        float fill = Live.getMaster() * height;
        Util.box(renderer, x, y - height + fill, width, fill, LightsCore.BLACK);

        if (Util.containsMouse(x, y, width, height) && canInteract()) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                interacted = true;
                float bottom = y - height + 20;
                Live.setMaster(Math.min(Math.max((Gdx.graphics.getHeight() - Gdx.input.getY() - bottom) / (y - 20 - bottom), 0), 1));
            }
        }

        if (isFadeToZero() || isFadeToMax()) {
            double timepassed = System.currentTimeMillis() - timestamp;
            double percentage = 0;
            if (timepassed > 0 && getFadeTime() > 0) percentage = timepassed / getFadeTime();
            if (percentage > 1) percentage = 1;

            if (isFadeToMax()) {
                Live.setMaster(startingValue + (float) percentage);
                if (Live.getMaster() == 1.0) fadeToMax = false;
            } else {
                Live.setMaster(startingValue - (float) percentage);
                if (Live.getMaster() == 0.0) fadeToZero = false;
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

        if (Live.getMaster() == 0.0) {
            fadeToMax();
        } else {
            fadeToZero();
        }
    }

    public static void fadeToZero() {
        masterUI.timestamp = System.currentTimeMillis();
        masterUI.startingValue = Live.getMaster();
        masterUI.fadeToMax = false;
        masterUI.fadeToZero = true;
    }

    public static void fadeToMax() {
        masterUI.timestamp = System.currentTimeMillis();
        masterUI.startingValue = Live.getMaster();
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