package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.UIHandler;

public class PatchUI implements UI {

    private Fixture selectedFixture = null;

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.PATCH);
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
        for (Fixture fixture : Fixture.fixtures()) {
            idWidth = Math.max(idWidth, renderer.getWidth(Integer.toString(fixture.getID())) + 10);
            nameWidth = Math.max(nameWidth, renderer.getWidth(fixture.getName()) + 10);
            profileWidth = Math.max(profileWidth, renderer.getWidth(fixture.getProfile()) + 10);
            addressWidth = Math.max(addressWidth, renderer.getWidth(Integer.toString(fixture.getAddress())) + 10);
            width = Math.max(width, idWidth + nameWidth + profileWidth + addressWidth);
        }

        renderer.box(x, y, uiWidth, getHeight(), Lights.color.DARK);
        renderer.box(x, y, uiWidth, cellHeight, Lights.color.DARK_BLUE, "Patch", Task.TextPosition.CENTER);
        drag(x, y, uiWidth, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, idWidth, cellHeight, Lights.color.DARK_BLUE, "ID", Task.TextPosition.CENTER);
        renderer.box(x + idWidth, y, nameWidth, cellHeight, Lights.color.DARK_BLUE, "Name", Task.TextPosition.CENTER);
        renderer.box(x + idWidth + nameWidth, y, profileWidth, cellHeight, Lights.color.DARK_BLUE, "Profile", Task.TextPosition.CENTER);
        renderer.box(x + idWidth + nameWidth + profileWidth, y, addressWidth, cellHeight, Lights.color.DARK_BLUE, "Address", Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (Fixture fixture : Fixture.fixtures(Fixture.SortBy.ID)) {
            if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
                interacted = true;
                if (Lights.mouse.leftClicked()) {
                    select(fixture);
                }
            }
            Color color = fixture.equals(getSelectedFixture()) ? Lights.color.DARK_GREEN : Lights.color.MEDIUM;
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
                for (Fixture fixture : Fixture.fixtures()) {
                    if (!fixture.equals(getSelectedFixture()) && fixture.getAddress() <= address && address <= fixture.getAddress() + fixture.getPhysicalChannels() - 1) {
                        occupied = true;
                        break;
                    }
                }
                boolean selected = getSelectedFixture().getAddress() <= address && address <= getSelectedFixture().getAddress() + getSelectedFixture().getPhysicalChannels() - 1;
                Color color = Lights.color.MEDIUM;
                if (selected) color = Lights.color.DARK_GREEN;
                if (occupied) color = Lights.color.DARK_BLUE;
                if (selected && occupied) color = Lights.color.DARK_CYAN;
                renderer.box(x, y, cellHeight, cellHeight, color, Integer.toString(address));
                if (Lights.mouse.contains(x, y, cellHeight, cellHeight) && canInteract()) {
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