package dev.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.programmer.Programmer;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.UIHandler;

import static dev.therealdan.lights.util.sorting.Sortable.Sort.ID;

public class FixturesUI implements UI {

    public static float WIDTH = 800;
    public static int FIXTURES_PER_ROW = 10;

    public FixturesUI() {
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.FIXTURES);
        boolean interacted = false;

        setWidth(FixturesUI.WIDTH);

        float cellHeight = 30;
        float cellSize = FixturesUI.WIDTH / FixturesUI.FIXTURES_PER_ROW;

        float x = getX();
        float y = getY();
        float width = getWidth();

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, setWidth(renderer, "Fixtures"), Task.TextPosition.CENTER);
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.rightClicked()) {
            interacted = true;
            Lights.openFixtureEditor();
        }
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (Fixture fixture : Fixture.fixtures(ID)) {
            renderer.box(x, y, cellSize, cellSize, Programmer.isSelected(fixture) ? Lights.color.DARK_RED : Lights.color.MEDIUM, fixture.getName(), Task.TextPosition.CENTER);
            if (Lights.mouse.contains(x, y, cellSize, cellSize) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
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