package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.fixtures.Group;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.UIHandler;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PatchUI implements UI {

    private static PatchUI patch;

    private List<Fixture> fixtures = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();

    private Fixture selectedFixture = null;

    public PatchUI() {
        patch = this;

        FileHandle fileHandle = Gdx.files.local("Lights/Fixtures/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle child : fileHandle.list())
                loadFixture(child);

        fileHandle = Gdx.files.local("Lights/Groups/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle child : fileHandle.list())
                loadGroup(child);
    }

    private void loadFixture(FileHandle fileHandle) {
        String name = fileHandle.name().replace(".txt", "");

        Profile profile = null;
        int address = 0;
        int id = -1;
        Vector3 position = new Vector3();

        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("Name: ")) {
                name = line.split(": ")[1];
            } else if (line.startsWith("Profile: ")) {
                profile = ProfilesUI.profileByName(line.split(": ")[1]);
            } else if (line.startsWith("Address: ")) {
                address = Integer.parseInt(line.split(": ")[1]);
            } else if (line.startsWith("ID: ")) {
                id = Integer.parseInt(line.split(": ")[1]);

            } else if (line.startsWith("  X: ")) {
                position.set(Float.parseFloat(line.split(": ")[1]), position.y, position.z);
            } else if (line.startsWith("  Y: ")) {
                position.set(position.x, Float.parseFloat(line.split(": ")[1]), position.z);
            } else if (line.startsWith("  Z: ")) {
                position.set(position.x, position.y, Float.parseFloat(line.split(": ")[1]));
            }
        }

        if (profile == null) return;
        if (address == 0) return;
        if (id <= -1) return;

        add(new Fixture(name, profile, address, id, position));
    }

    private void loadGroup(FileHandle fileHandle) {
        if (fileHandle.name().startsWith(".")) return;
        String name = fileHandle.name().replace(".txt", "");
        Group group = new Group(name);

        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("Name: ")) {
                group.rename(line.split(": ")[1]);
            } else if (line.startsWith("Fixtures:")) {
                // do nothing
            } else if (line.startsWith("  - ")) {
                int id = Integer.parseInt(line.replaceFirst("  - ", ""));
                Fixture fixture = fixtureByID(id);
                if (fixture != null) group.add(fixture);
            }
        }

        add(group);
    }

    @Override
    public void save() {
        UI.super.save();

        for (Fixture fixture : fixtures()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Fixtures/" + fixture.getName() + ".txt");
            fileHandle.writeString("", false);

            fileHandle.writeString("Name: " + fixture.getName() + "\r\n", true);
            fileHandle.writeString("Profile: " + fixture.getProfile() + "\r\n", true);
            fileHandle.writeString("Address: " + fixture.getAddress() + "\r\n", true);
            fileHandle.writeString("ID: " + fixture.getID() + "\r\n", true);

            Vector3 position = fixture.getPosition();
            fileHandle.writeString("Position:\r\n", true);
            fileHandle.writeString("  X: " + position.x + "\r\n", true);
            fileHandle.writeString("  Y: " + position.y + "\r\n", true);
            fileHandle.writeString("  Z: " + position.z + "\r\n", true);
        }

        for (Group group : groups()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Groups/" + group.getName() + ".txt");
            fileHandle.writeString("", false);

            fileHandle.writeString("Name: " + group.getName() + "\r\n", true);

            fileHandle.writeString("Fixtures:\r\n", true);
            for (Fixture fixture : group.fixtures())
                fileHandle.writeString("  - " + fixture.getID() + "\r\n", true);
        }
    }

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
        for (Fixture fixture : fixtures()) {
            idWidth = Math.max(idWidth, renderer.getWidth(Integer.toString(fixture.getID())) + 10);
            nameWidth = Math.max(nameWidth, renderer.getWidth(fixture.getName()) + 10);
            profileWidth = Math.max(profileWidth, renderer.getWidth(fixture.getProfile()) + 10);
            addressWidth = Math.max(addressWidth, renderer.getWidth(Integer.toString(fixture.getAddress())) + 10);
            width = Math.max(width, idWidth + nameWidth + profileWidth + addressWidth);
        }

        Util.box(renderer, x, y, uiWidth, cellHeight, Lights.DARK_BLUE, "Patch", Task.TextPosition.CENTER);
        drag(x, y, uiWidth, cellHeight);
        y -= cellHeight;

        Util.box(renderer, x, y, idWidth, cellHeight, Lights.DARK_BLUE, "ID", Task.TextPosition.CENTER);
        Util.box(renderer, x + idWidth, y, nameWidth, cellHeight, Lights.DARK_BLUE, "Name", Task.TextPosition.CENTER);
        Util.box(renderer, x + idWidth + nameWidth, y, profileWidth, cellHeight, Lights.DARK_BLUE, "Profile", Task.TextPosition.CENTER);
        Util.box(renderer, x + idWidth + nameWidth + profileWidth, y, addressWidth, cellHeight, Lights.DARK_BLUE, "Address", Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (Fixture fixture : fixtures()) {
            if (Util.containsMouse(x, y, width, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(500)) {
                    if (fixture.equals(getSelectedFixture())) {
                        select(null);
                    } else {
                        select(fixture);
                    }
                }
            }
            Color color = fixture.equals(getSelectedFixture()) ? Lights.DARK_GREEN : Lights.medium();
            Util.box(renderer, x, y, idWidth, cellHeight, color, setWidth(renderer, Integer.toString(fixture.getID())), Task.TextPosition.CENTER);
            Util.box(renderer, x + idWidth, y, nameWidth, cellHeight, color, setWidth(renderer, fixture.getName()));
            Util.box(renderer, x + idWidth + nameWidth, y, profileWidth, cellHeight, color, setWidth(renderer, fixture.getProfile()));
            Util.box(renderer, x + idWidth + nameWidth + profileWidth, y, addressWidth, cellHeight, color, setWidth(renderer, Integer.toString(fixture.getAddress())));
            y -= cellHeight;
        }

        setHeightBasedOnY(y);

        if (hasFixtureSelected()) {
            int perRow = (int) DMX.MAX_CHANNELS / fixtures().size();
            float addressesWidth = perRow * cellHeight;
            x = getX() + width;
            y = getY() - cellHeight;

            for (int address = 1; address <= DMX.MAX_CHANNELS; address++) {
                boolean occupied = false;
                for (Fixture fixture : fixtures()) {
                    if (!fixture.equals(getSelectedFixture()) && fixture.getAddress() <= address && address <= fixture.getAddress() + fixture.getPhysicalChannels() - 1) {
                        occupied = true;
                        break;
                    }
                }
                boolean selected = getSelectedFixture().getAddress() <= address && address <= getSelectedFixture().getAddress() + getSelectedFixture().getPhysicalChannels() - 1;
                Color color = Lights.medium();
                if (selected) color = Lights.DARK_GREEN;
                if (occupied) color = Lights.DARK_BLUE;
                if (selected && occupied) color = Lights.DARK_CYAN;
                Util.box(renderer, x, y, cellHeight, cellHeight, color, Integer.toString(address));
                if (Util.containsMouse(x, y, cellHeight, cellHeight) && canInteract()) {
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

    public static void add(Fixture fixture) {
        patch.fixtures.add(fixture);
    }

    public static void add(Group group) {
        patch.groups.add(group);
    }

    public static int getTopID() {
        int id = 1;
        for (Fixture fixture : patch.fixtures)
            if (fixture.getID() > id)
                id = fixture.getID();

        return id;
    }

    public static Fixture fixtureByID(int id) {
        for (Fixture fixture : patch.fixtures)
            if (fixture.getID() == id)
                return fixture;
        return null;
    }

    public static Fixture fixtureByName(String name) {
        for (Fixture fixture : patch.fixtures)
            if (fixture.getName().equalsIgnoreCase(name))
                return fixture;
        return null;
    }

    public static Group groupByName(String name) {
        for (Group group : patch.groups)
            if (group.getName().equalsIgnoreCase(name))
                return group;
        return null;
    }

    public static List<Fixture> fixtures() {
        List<Fixture> fixtures = new ArrayList<>();
        for (int id = 1; id <= getTopID(); id++)
            if (fixtureByID(id) != null)
                fixtures.add(fixtureByID(id));
        return fixtures;
    }

    public static List<Group> groups() {
        return new ArrayList<>(patch.groups);
    }

}