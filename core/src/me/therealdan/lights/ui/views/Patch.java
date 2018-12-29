package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.fixtures.Group;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Patch implements Tab {

    private static Patch patch;

    private HashMap<String, Float> cellWidth = new HashMap<>();

    private List<Fixture> fixtures = new ArrayList<>();
    private Fixture selectedFixture = null;

    private List<Group> groups = new ArrayList<>();
    private Group selectedGroup = null;

    private int fixtureStart = 0, groupStart = 0;
    private int maxRows = 10;

    private int firstAddress = 1;
    private boolean canSee512 = false;

    private Section section = null;
    private boolean mouseUp = true;

    public Patch() {
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
                profile = ProfileEditor.profileByName(line.split(": ")[1]);
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
        String name = fileHandle.name().replace(".txt", "");
        Group group = new Group(name);

        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("Name: ")) {
                group.rename(line.split(": ")[1]);
            } else if (line.startsWith("Fixtures:")) {
                // do nothing
            } else {
                int id = Integer.parseInt(line.split("- ")[1]);
                Fixture fixture = fixtureByID(id);
                if (fixture != null) group.add(fixture);
            }
        }
    }

    @Override
    public void save() {
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
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        float y = HEIGHT - LightsCore.edge();
        float width = (WIDTH - (LightsCore.edge() * 3f)) / 2f;
        float cellHeight = 25;

        float fixtureListFloor = fixtureList(renderer, LightsCore.edge(), y, width, cellHeight);
        float groupsListFloor = groupsList(renderer, LightsCore.edge() * 2f + width, y, width, cellHeight);
        y = fixtureListFloor - LightsCore.edge();
        if (y < HEIGHT / 2 - 100) maxRows--;
        if (y > HEIGHT / 2) maxRows++;

        width += width + LightsCore.edge();
        float height = (HEIGHT - (LightsCore.edge() * 2f)) - y;
        if (hasFixtureSelected()) selectedFixture(renderer, LightsCore.edge(), y, width, (HEIGHT - height) - (LightsCore.edge() * 3f), cellHeight);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        this.mouseUp = true;
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        switch (keycode) {
            case Input.Keys.UP:
                up();
                break;
            case Input.Keys.DOWN:
                down();
                break;
            case Input.Keys.ENTER:
            case Input.Keys.ESCAPE:
                deselect();
                break;
            case Input.Keys.BACKSPACE:
                backspace();
                break;
            case Input.Keys.SPACE:
                space();
                break;

            default:
                String key = Input.Keys.toString(keycode);
                if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(key.toUpperCase())) {
                    key = shift ? key.toUpperCase() : key.toLowerCase();
                    if (hasGroupSelected()) {
                        getSelectedGroup().rename(getSelectedGroup().getName() + key);
                    } else if (hasFixtureSelected()) {
                        getSelectedFixture().rename(getSelectedFixture().getName() + key);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        if (section == null) return true;

        switch (section) {
            case FIXTURES:
                fixtureStart += amount;
                if (fixtureStart < 0) fixtureStart = 0;
                if (fixtureStart > fixtures().size() - maxRows) fixtureStart = fixtures().size() - maxRows;
                break;

            case GROUPS:
                groupStart += amount;
                if (groupStart < 0) groupStart = 0;
                if (groupStart > groups().size() - maxRows) groupStart = groups().size() - maxRows;
                break;

            case PATCH:
                if (amount > 0 && canSee512) {
                    return true;
                } else {
                    canSee512 = false;
                }

                firstAddress += amount;

                if (firstAddress > DMX.MAX_CHANNELS) firstAddress = (int) DMX.MAX_CHANNELS;
                if (firstAddress < 1) firstAddress = 1;
                break;
        }

        return true;
    }

    private float fixtureList(Renderer renderer, float x, float y, float width, float cellHeight) {
        fixture(renderer, x, y, width, cellHeight, "Fixture Name", "ID", "Profile", "Address", LightsCore.DARK_BLUE);

        int current = 0;
        boolean alternate = true;
        for (Fixture fixture : fixtures()) {
            if (current - fixtureStart >= maxRows) break;
            if (current >= fixtureStart) {
                y -= cellHeight;

                if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                    section = Section.FIXTURES;
                    if (Gdx.input.isButtonPressed(Input.Keys.LEFT))
                        select(fixture);
                }

                boolean selected;
                if (hasGroupSelected()) {
                    this.selectedFixture = null;
                    selected = false;
                    for (Fixture each : getSelectedGroup().fixtures())
                        if (fixture.equals(each))
                            selected = true;
                } else {
                    selected = fixture.equals(selectedFixture);
                }

                fixture(renderer, x, y, width, cellHeight, fixture.getName(), Integer.toString(fixture.getID()), fixture.getProfile(), Integer.toString(fixture.getAddress()),
                        selected ? LightsCore.DARK_RED :
                                alternate ? LightsCore.medium() : LightsCore.dark());

                alternate = !alternate;
            }
            current++;
        }

        return y - cellHeight;
    }

    private void fixture(Renderer renderer, float x, float y, float width, float cellHeight, String name, String id, String profile, String address, Color color) {
        float xOffset = 5;

        float extraSpace = width;
        extraSpace -= getCellWidth("ID");
        extraSpace -= getCellWidth("FixtureName");
        extraSpace -= getCellWidth("Profile");
        extraSpace -= getCellWidth("Address");
        extraSpace /= 4f;
        if (extraSpace < 0) extraSpace = 0;

        // Box
        renderer.queue(new Task(x, y).rect(width, -cellHeight).setColor(color));
        renderer.queue(new Task(x, y).rectOutline(width, -cellHeight).setColor(LightsCore.light()));

        // ID
        renderer.queue(new Task(x + xOffset, y - 3 - cellHeight / 2).text(id, Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));

        // Name
        x += setCellWidth(renderer, "ID", id, xOffset);
        x += extraSpace;
        line(renderer, x, y, cellHeight);
        renderer.queue(new Task(x + xOffset, y - 3 - cellHeight / 2).text(name, Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));

        // Profile
        x += setCellWidth(renderer, "FixtureName", name, xOffset);
        x += extraSpace;
        line(renderer, x, y, cellHeight);
        renderer.queue(new Task(x + xOffset, y - 3 - cellHeight / 2).text(profile, Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));

        // Address
        x += setCellWidth(renderer, "Profile", profile, xOffset);
        x += extraSpace;
        line(renderer, x, y, cellHeight);
        renderer.queue(new Task(x + xOffset, y - 3 - cellHeight / 2).text(address, Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));
        setCellWidth(renderer, "Address", address, xOffset);
    }

    private float groupsList(Renderer renderer, float X, float Y, float WIDTH, float cellHeight) {
        renderer.queue(new Task(X, Y - cellHeight).rect(WIDTH, cellHeight).setColor(LightsCore.DARK_BLUE));
        renderer.queue(new Task(X, Y - cellHeight).rectOutline(WIDTH, cellHeight).setColor(LightsCore.light()));
        renderer.queue(new Task(X + 5, Y - 3 - cellHeight / 2).text("Fixture Groups", Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));

        Y -= cellHeight;

        int current = 0;
        boolean alternate = true;
        for (Group group : groups()) {
            if (current - groupStart >= maxRows - 1) break;
            if (current >= groupStart) {
                renderer.queue(new Task(X, Y - cellHeight).rect(WIDTH, cellHeight).setColor(
                        group.equals(selectedGroup) ? LightsCore.DARK_RED :
                                alternate ? LightsCore.medium() : LightsCore.dark()));
                renderer.queue(new Task(X, Y - cellHeight).rectOutline(WIDTH, cellHeight).setColor(LightsCore.light()));
                renderer.queue(new Task(X + 5, Y - 3 - cellHeight / 2f).text(group.getName(), Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));

                if (Util.containsMouse(X, Gdx.graphics.getHeight() - Y, WIDTH, cellHeight)) {
                    section = Section.GROUPS;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                        selectedGroup = group;
                    }
                }

                Y -= cellHeight;
                alternate = !alternate;
            }
            current++;
        }

        if (selectedGroup != null) return Y -= cellHeight;

        renderer.queue(new Task(X, Y - cellHeight).rect(WIDTH, cellHeight).setColor(LightsCore.DARK_BLUE));
        renderer.queue(new Task(X, Y - cellHeight).rectOutline(WIDTH, cellHeight).setColor(LightsCore.light()));
        renderer.queue(new Task(X + 5, Y - 3 - cellHeight / 2).text("Create New Group", Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));

        if (Util.containsMouse(X, Gdx.graphics.getHeight() - Y, WIDTH, cellHeight)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && this.mouseUp) {
                this.mouseUp = false;
                add(new Group("Group " + (groups().size() + 1)));
            }
        }

        return Y - cellHeight;
    }

    private void selectedFixture(Renderer renderer, float x, float y, float width, float height, float cellHeight) {
        float cellSize = 35f;
        for (int address = firstAddress; address <= DMX.MAX_CHANNELS; address++) {
            if (x + cellSize > Gdx.graphics.getWidth() - LightsCore.edge()) {
                x = LightsCore.edge();
                y -= cellSize;
            }

            if (y - cellSize <= 0) {
                return;
            }

            boolean occupied = false;
            for (Fixture fixture : fixtures()) {
                if (!selectedFixture.equals(fixture) && fixture.getAddress() <= address && address <= fixture.getAddress() + fixture.getPhysicalChannels() - 1) {
                    occupied = true;
                    break;
                }
            }

            renderer.queue(new Task(x + 5, y - 3 - cellSize / 2).text(Integer.toString(address), Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));
            y -= cellSize;
            renderer.queue(new Task(x, y).rect(cellSize, cellSize).setColor(
                    selectedFixture.getAddress() <= address && address <= selectedFixture.getAddress() + selectedFixture.getPhysicalChannels() - 1 ?
                            occupied ? LightsCore.DARK_RED : LightsCore.DARK_GREEN :
                            occupied ? LightsCore.DARK_BLUE : LightsCore.medium()
            ));
            renderer.queue(new Task(x, y).rectOutline(cellSize, cellSize).setColor(LightsCore.light()));
            y += cellSize;
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, cellSize, cellSize)) {
                section = Section.PATCH;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                    selectedFixture.setAddress(address);
            }

            x += cellSize;
        }

        if (x <= Gdx.graphics.getWidth()) canSee512 = true;
    }

    private void line(Renderer renderer, float x, float y, float height) {
        renderer.queue(new Task(x, y).line(x, y - height).setColor(LightsCore.light()));
    }

    private void deselect() {
        if (hasGroupSelected()) {
            this.selectedGroup = null;
        } else if (hasFixtureSelected()) {
            this.selectedFixture = null;
        }
    }

    private void select(Fixture fixture) {
        if (hasGroupSelected()) {
            if (mouseUp) {
                mouseUp = false;
                if (getSelectedGroup().contains(fixture)) {
                    getSelectedGroup().remove(fixture);
                } else {
                    getSelectedGroup().add(fixture);
                }
            }
        } else {
            this.selectedFixture = fixture;
        }
    }

    private void select(Group group) {
        this.selectedGroup = group;
    }

    private void up() {
        if (hasGroupSelected()) {
            Group previous = null;
            for (Group group : groups()) {
                if (selectedGroup.equals(group)) {
                    if (previous == null) return;
                    select(previous);
                    return;
                }
                previous = group;
            }
        } else if (hasFixtureSelected()) {
            Fixture previous = null;
            for (Fixture fixture : fixtures()) {
                if (selectedFixture.equals(fixture)) {
                    if (previous == null) return;
                    select(previous);
                    return;
                }
                previous = fixture;
            }
        }
    }

    private void down() {
        if (hasGroupSelected()) {
            boolean next = false;
            for (Group group : groups()) {
                if (next) {
                    select(group);
                    return;
                }
                if (selectedGroup.equals(group)) next = true;
            }
        } else if (hasFixtureSelected()) {
            boolean next = false;
            for (Fixture fixture : fixtures()) {
                if (next) {
                    select(fixture);
                    return;
                }
                if (selectedFixture.equals(fixture)) next = true;
            }
        }
    }

    private void backspace() {
        if (hasGroupSelected()) {
            if (getSelectedGroup().getName().length() == 0) return;
            if (getSelectedGroup().getName().length() == 1) {
                getSelectedGroup().rename("");
                return;
            }
            getSelectedGroup().rename(getSelectedGroup().getName().substring(0, getSelectedGroup().getName().length() - 1));
        } else if (hasFixtureSelected()) {
            if (getSelectedFixture().getName().length() == 0) return;
            if (getSelectedFixture().getName().length() == 1) {
                getSelectedFixture().rename("");
                return;
            }
            getSelectedFixture().rename(getSelectedFixture().getName().substring(0, getSelectedFixture().getName().length() - 1));
        }
    }

    private void space() {
        if (hasGroupSelected()) {
            getSelectedGroup().rename(getSelectedGroup().getName() + " ");
        } else if (hasFixtureSelected()) {
            getSelectedFixture().rename(getSelectedFixture().getName() + " ");
        }
    }

    private float setCellWidth(Renderer renderer, String cell, String text, float xOffset) {
        float currentWidth = getCellWidth(cell);
        float width = renderer.getWidth(text) + xOffset * 2;
        if (width > currentWidth) {
            cellWidth.put(cell, width);
            return width;
        }
        return currentWidth;
    }

    private float getCellWidth(String cell) {
        return cellWidth.getOrDefault(cell, 1f);
    }

    public boolean hasFixtureSelected() {
        return selectedFixture != null;
    }

    public boolean hasGroupSelected() {
        return selectedGroup != null;
    }

    public Fixture getSelectedFixture() {
        return selectedFixture;
    }

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    public static void add(Fixture fixture) {
        patch.fixtures.add(fixture);
    }

    public static void add(Group group) {
        patch.groups.add(group);
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
        return new ArrayList<>(patch.fixtures);
    }

    public static List<Group> groups() {
        return new ArrayList<>(patch.groups);
    }

    public enum Section {
        FIXTURES, GROUPS, PATCH,
    }
}