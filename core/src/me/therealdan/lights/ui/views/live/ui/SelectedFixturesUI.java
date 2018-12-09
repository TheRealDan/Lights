package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.util.Util;

public class SelectedFixturesUI implements UI {

    public SelectedFixturesUI() {
        setLocation(230, 20);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.SELECTED_FIXTURES);
        boolean interacted = false;

        float cellHeight = 30;

        float x = getX();
        float y = Gdx.graphics.getHeight() - getY();
        float width = getWidth();

        int selected = Programmer.getSelectedFixtures().size();
        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, selected + (selected == 1 ? " fixture selected" : " fixtures selected")));
        y -= cellHeight;

        for (Fixture fixture : Programmer.getSelectedFixtures()) {
            Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), setWidth(renderer, fixture.getName()));
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                    Programmer.deselect(fixture);
            }
            y -= cellHeight;
        }

        setHeight((Gdx.graphics.getHeight() - getY()) - y);
        return interacted;
    }
}