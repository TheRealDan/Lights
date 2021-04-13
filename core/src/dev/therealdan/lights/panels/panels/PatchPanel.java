package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import dev.therealdan.lights.dmx.DMX;
import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.store.FixturesStore;

import static dev.therealdan.lights.util.sorting.Sortable.Sort.ID;

public class PatchPanel implements Panel {

    private FixturesStore _fixturesStore;

    private Fixture selectedFixture = null;

    public PatchPanel(FixturesStore fixturesStore) {
        _fixturesStore = fixturesStore;

        register(new CloseIcon());
    }

    @Override
    public boolean draw(Mouse mouse, Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        boolean interacted = false;

        float x = getX();
        float y = getY();
        float cellHeight = 30;
        float uiWidth = getWidth();

        float idWidth = renderer.getWidth("ID") + 10;
        float nameWidth = renderer.getWidth("Name") + 10;
        float profileWidth = renderer.getWidth("Profile") + 10;
        float addressWidth = renderer.getWidth("Address") + 10;
        float width = idWidth + nameWidth + profileWidth + addressWidth;
        for (Fixture fixture : _fixturesStore.getFixtures()) {
            idWidth = Math.max(idWidth, renderer.getWidth(Integer.toString(fixture.getID())) + 10);
            nameWidth = Math.max(nameWidth, renderer.getWidth(fixture.getName()) + 10);
            profileWidth = Math.max(profileWidth, renderer.getWidth(fixture.getProfile()) + 10);
            addressWidth = Math.max(addressWidth, renderer.getWidth(Integer.toString(fixture.getAddress())) + 10);
            width = Math.max(width, idWidth + nameWidth + profileWidth + addressWidth);
        }

        renderer.box(x, y, uiWidth, getHeight(), renderer.getTheme().DARK);
        renderer.box(x, y, uiWidth, cellHeight, renderer.getTheme().DARK_BLUE, getFriendlyName(), Task.TextPosition.CENTER);
        drag(mouse, x, y, uiWidth, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, idWidth, cellHeight, renderer.getTheme().DARK_BLUE, "ID", Task.TextPosition.CENTER);
        renderer.box(x + idWidth, y, nameWidth, cellHeight, renderer.getTheme().DARK_BLUE, "Name", Task.TextPosition.CENTER);
        renderer.box(x + idWidth + nameWidth, y, profileWidth, cellHeight, renderer.getTheme().DARK_BLUE, "Profile", Task.TextPosition.CENTER);
        renderer.box(x + idWidth + nameWidth + profileWidth, y, addressWidth, cellHeight, renderer.getTheme().DARK_BLUE, "Address", Task.TextPosition.CENTER);
        drag(mouse, x, y, width, cellHeight);
        y -= cellHeight;

        for (Fixture fixture : _fixturesStore.getFixtures(ID)) {
            if (mouse.within(x, y, width, cellHeight) && canInteract()) {
                interacted = true;
                if (mouse.leftClicked()) {
                    select(fixture);
                }
            }
            Color color = fixture.equals(getSelectedFixture()) ? renderer.getTheme().DARK_GREEN : renderer.getTheme().MEDIUM;
            renderer.box(x, y, idWidth, cellHeight, color, setWidth(renderer, Integer.toString(fixture.getID())), Task.TextPosition.CENTER);
            renderer.box(x + idWidth, y, nameWidth, cellHeight, color, setWidth(renderer, fixture.getName()));
            renderer.box(x + idWidth + nameWidth, y, profileWidth, cellHeight, color, setWidth(renderer, fixture.getProfile()));
            renderer.box(x + idWidth + nameWidth + profileWidth, y, addressWidth, cellHeight, color, setWidth(renderer, Integer.toString(fixture.getAddress())));
            y -= cellHeight;
        }

        setHeightBasedOnY(y);

        if (hasFixtureSelected()) {
            int perRow = (int) DMX.MAX_CHANNELS / 16;
            float addressesWidth = perRow * cellHeight;
            x = getX() + width;
            y = getY() - cellHeight;

            for (int address = 1; address <= DMX.MAX_CHANNELS; address++) {
                boolean occupied = false;
                for (Fixture fixture : _fixturesStore.getFixtures()) {
                    if (!fixture.equals(getSelectedFixture()) && fixture.getAddress() <= address && address <= fixture.getAddress() + fixture.getPhysicalChannels() - 1) {
                        occupied = true;
                        break;
                    }
                }
                boolean selected = getSelectedFixture().getAddress() <= address && address <= getSelectedFixture().getAddress() + getSelectedFixture().getPhysicalChannels() - 1;
                Color color = renderer.getTheme().MEDIUM;
                if (selected) color = renderer.getTheme().DARK_GREEN;
                if (occupied) color = renderer.getTheme().DARK_BLUE;
                if (selected && occupied) color = renderer.getTheme().DARK_CYAN;
                renderer.box(x, y, cellHeight, cellHeight, color, Integer.toString(address));
                if (mouse.within(x, y, cellHeight, cellHeight) && canInteract()) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                        getSelectedFixture().setAddress(address);
                    }
                }

                x += cellHeight;

                if (address % perRow == 0) {
                    x = getX() + width;
                    y -= cellHeight;
                }
            }
            setWidth(width + addressesWidth);
        } else {
            setWidth(width);
        }

        setHeightBasedOnY(y);
        return interacted;
    }

    private void select(Fixture fixture) {
        this.selectedFixture = fixture;
    }

    private Fixture getSelectedFixture() {
        return selectedFixture;
    }

    private boolean hasFixtureSelected() {
        return getSelectedFixture() != null;
    }
}