package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

public class MasterUI implements UI {

    public static float HEIGHT = 250;

    public MasterUI() {
        setLocation(20, 500);

        setWidth(80);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.MASTER);
        boolean interacted = false;

        setHeight(MasterUI.HEIGHT);

        float cellHeight = 30;

        float x = getX();
        float y = Gdx.graphics.getHeight() - getY();

        float width = getWidth();
        float height = getHeight() - cellHeight;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Master"));
        y -= cellHeight;

        Util.box(renderer, x, y, width, height, LightsCore.medium(), setWidth(renderer, Util.getPercentage(Live.getMaster())));
        float fill = Live.getMaster() * height;
        Util.box(renderer, x, y - height + fill, width, fill, LightsCore.DARK_RED);

        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, height) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            interacted = true;
            float bottom = y - height + 20;
            Live.setMaster(Math.min(Math.max((Gdx.graphics.getHeight() - Gdx.input.getY() - bottom) / (y - 20 - bottom), 0), 1));
        }

        return interacted;
    }
}