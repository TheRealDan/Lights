package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.AddFixtureIcon;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.programmer.Programmer;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.store.FixturesStore;
import dev.therealdan.lights.ui.DisplayHandler;

import static dev.therealdan.lights.util.sorting.Sortable.Sort.ID;

public class FixturesPanel implements Panel {

    private FixturesStore _fixturesStore;

    public int fixturesPerRow = 8;

    public FixturesPanel(FixturesStore fixturesStore, DisplayHandler displayHandler) {
        _fixturesStore = fixturesStore;

        register(new CloseIcon());
        register(new AddFixtureIcon(displayHandler));

        setWidth(800);
        setHeight(200);
    }

    @Override
    public boolean drawContent(Mouse mouse, Renderer renderer, float x, float y, float width, float height, boolean interacted) {
        int fixtures = _fixturesStore.count();
        while (fixtures % getFixturesPerRow() != 0) fixtures++;

        float fixtureWidth = width / getFixturesPerRow();
        float fixtureHeight = height / (fixtures / getFixturesPerRow());

        for (Fixture fixture : _fixturesStore.getFixtures(ID)) {
            renderer.box(x, y, fixtureWidth, fixtureHeight, Programmer.isSelected(fixture) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, fixture.getName(), Task.TextPosition.CENTER);
            if (mouse.within(x, y, fixtureWidth, fixtureHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(500)) {
                    if (Programmer.isSelected(fixture)) {
                        Programmer.deselect(fixture);
                    } else {
                        Programmer.select(fixture);
                    }
                }
            }
            x += fixtureWidth;

            if (x + fixtureWidth > getX() + getWidth()) {
                x = getX();
                y -= fixtureHeight;
            }
        }
        return interacted;
    }

    @Override
    public boolean isResizeable() {
        return true;
    }

    public void setFixturesPerRow(int fixtures) {
        this.fixturesPerRow = fixtures;
    }

    public int getFixturesPerRow() {
        return fixturesPerRow;
    }
}