package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.dmx.Output;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

public class FrozenUI implements UI {

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.FROZEN);
        boolean interacted = false;

        if (!Output.isFrozen()) return interacted;

        setLocation(WIDTH / 2 - getWidth() / 2, HEIGHT / 2 - getHeight() - 2);

        float cellHeight = 30;

        float x = getX();
        float y = Gdx.graphics.getHeight() - getY();

        float width = getWidth();

        Util.box(renderer, x, y, width, cellHeight, LightsCore.RED, setWidth(renderer, "OUTPUT FROZEN"));
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), setWidth(renderer, "Press Escape to unfreeze"));
        y -= cellHeight;

        setHeight((Gdx.graphics.getHeight() - getY()) - y);
        return interacted;
    }
}