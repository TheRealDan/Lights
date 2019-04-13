package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.Lights;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.Live;
import me.therealdan.lights.util.Util;

public class FixturesUI implements UI {

    public static float WIDTH = 800;
    public static int FIXTURES_PER_ROW = 10;

    public FixturesUI() {
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.FIXTURES);
        boolean interacted = false;

        setWidth(FixturesUI.WIDTH);

        float cellHeight = 30;
        float cellSize = FixturesUI.WIDTH / FixturesUI.FIXTURES_PER_ROW;

        float x = getX();
        float y = getY();
        float width = getWidth();

        Util.box(renderer, x, y, width, cellHeight, Lights.DARK_BLUE, setWidth(renderer, "Fixtures"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (Fixture fixture : PatchUI.fixtures()) {
            Util.box(renderer, x, y, cellSize, cellSize, Programmer.isSelected(fixture) ? Lights.DARK_RED : Lights.medium(), fixture.getName(), Task.TextPosition.CENTER);
            if (Util.containsMouse(x, y, cellSize, cellSize) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(500)) {
                    if (Programmer.isSelected(fixture)) {
                        Programmer.deselect(fixture);
                    } else {
                        Programmer.select(fixture);
                    }
                }
            }
            x += cellSize;

            if (x + cellSize > getX() + getWidth()) {
                x = getX();
                y -= cellSize;
            }
        }
        y -= cellSize;

        setHeightBasedOnY(y);
        return interacted;
    }
}