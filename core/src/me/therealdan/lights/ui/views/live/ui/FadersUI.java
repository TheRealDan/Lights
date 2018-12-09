package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.controllers.Fader;
import me.therealdan.lights.controllers.FaderBank;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Faders;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

public class FadersUI implements UI {

    private static float HEIGHT = 250;

    private int bank = 1;

    public FadersUI() {
        setLocation(500, 1100);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.FADERS);
        boolean interacted = false;

        setHeight(FadersUI.HEIGHT);

        float cellHeight = 30;

        float x = getX();
        float y = Gdx.graphics.getHeight() - getY();

        float faderWidth = 80;
        float height = getHeight() - cellHeight;

        Util.box(renderer, x, y, getWidth(), cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Faders - Bank " + getBank().getID()));
        y -= cellHeight;

        for (Fader fader : getBank().faders()) {
            Util.box(renderer, x, y, faderWidth, height, LightsCore.medium(), Util.getPercentage(fader.getValue()));
            float fill = fader.getValue() * height;
            Util.box(renderer, x, y - height + fill, faderWidth, fill, fader.getColor());
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, faderWidth, height) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                interacted = true;
                float bottom = y - height + 20;
                fader.setValue(Math.min(Math.max((Gdx.graphics.getHeight() - Gdx.input.getY() - bottom) / (y - 20 - bottom), 0), 1));
            }
            x += faderWidth;
        }

        setWidth(x - getX());
        return interacted;
    }

    public void setBank(int bank) {
        this.bank = bank;
    }

    public FaderBank getBank() {
        return Faders.getBank(bank);
    }
}