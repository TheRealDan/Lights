package dev.therealdan.lights.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.fixtures.fixture.Profile;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.util.Util;

import static dev.therealdan.lights.util.sorting.Sortable.Sort.ID;
import static dev.therealdan.lights.util.sorting.Sortable.Sort.NAME;

public class FixtureEditor implements Visual {

    private DisplayHandler _displayHandler;

    private Fixture fixture;

    private Profile profile;
    private String name;
    private int address;
    private int count;
    private int step;

    private Section section;

    public FixtureEditor(DisplayHandler displayHandler) {
        _displayHandler = displayHandler;
    }

    @Override
    public boolean draw(Mouse mouse, Renderer renderer) {
        float X = 0;
        float Y = Gdx.graphics.getHeight();
        float WIDTH = Gdx.graphics.getWidth();
        float cellHeight = 30;

        float x = X;
        float y = Y;
        float width = WIDTH;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().DARK_BLUE, "Fixture Editor", Task.TextPosition.CENTER);
        y -= cellHeight;

        // EXISTING FIXTURES
        width = WIDTH / 3;
        renderer.box(x, y, width, cellHeight, renderer.getTheme().DARK_BLUE, "Fixtures", Task.TextPosition.CENTER);
        y -= cellHeight;
        for (Fixture fixture : Fixture.fixtures(ID)) {
            renderer.box(x, y, width, cellHeight, fixture.equals(getFixture()) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, fixture.getName(), Task.TextPosition.LEFT_CENTER);
            if (mouse.within(x, y, width, cellHeight)) {
                if (mouse.leftClicked()) {
                    setFixture(fixture);
                }
            }
            y -= cellHeight;
        }
        x += width;
        y = Y - cellHeight;

        // PROFILES
        renderer.box(x, y, width, cellHeight, renderer.getTheme().DARK_BLUE, "Profiles", Task.TextPosition.CENTER);
        y -= cellHeight;
        for (Profile profile : Profile.profiles(NAME)) {
            renderer.box(x, y, width, cellHeight, profile.equals(getProfile()) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, profile.getName(), Task.TextPosition.LEFT_CENTER);
            if (mouse.within(x, y, width, cellHeight)) {
                if (mouse.leftClicked()) {
                    setProfile(profile);
                }
            }
            y -= cellHeight;
        }
        x += width;
        y = Y - cellHeight;


        // ACTIONS
        renderer.box(x, y, width, cellHeight, renderer.getTheme().DARK_BLUE, "Actions", Task.TextPosition.CENTER);
        y -= cellHeight;

        if (getFixture() != null) {
            renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, renderer.getTheme().RED, "Delete");
            if (mouse.within(x, y, width, cellHeight) && Util.isShiftHeld()) {
                if (mouse.leftClicked()) {
                    Fixture.remove(getFixture());
                    setFixture(null);
                    return false;
                }
            }
            y -= cellHeight;
        }

        if (getProfile() != null) {
            renderer.box(x, y, width, cellHeight, isEditing(Section.NAME) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, "Name: " + getName());
            if (mouse.within(x, y, width, cellHeight)) {
                if (mouse.leftClicked()) {
                    edit(Section.NAME);
                }
            }
            y -= cellHeight;

            renderer.box(x, y, width, cellHeight, isEditing(Section.ADDRESS) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, "Address: " + getAddress());
            if (mouse.within(x, y, width, cellHeight)) {
                if (mouse.leftClicked()) {
                    edit(Section.ADDRESS);
                }
            }
            y -= cellHeight;

            renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, "Count: " + getCount());
            if (mouse.within(x, y, width, cellHeight)) {
                if (mouse.leftClicked(500)) {
                    setCount(getCount() + 1);
                } else if (mouse.rightClicked()) {
                    setCount(getCount() - 1);
                }
            }
            y -= cellHeight;

            if (getCount() > 1) {
                renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, "Step: " + getStep());
                if (mouse.within(x, y, width, cellHeight)) {
                    if (mouse.leftClicked(500)) {
                        setStep(getStep() + 1);
                    } else if (mouse.rightClicked()) {
                        setStep(getStep() - 1);
                    }
                }
                y -= cellHeight;
            }

            renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, renderer.getTheme().YELLOW, "Create Fixture" + (getCount() > 1 ? "s" : ""));
            if (mouse.within(x, y, width, cellHeight)) {
                if (mouse.leftClicked(500)) {
                    int address = getAddress();
                    for (int count = 1; count <= getCount(); count++) {
                        Fixture.add(new Fixture(getName() + " " + count, getProfile(), address, Fixture.getFreeID()));
                        address += getStep();
                    }
                }
            }
            y -= cellHeight;
        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE:
                _displayHandler.setFocus(DisplayHandler.Focus.MAIN_VIEW);
                return false;
        }

        if (isEditing(Section.NAME)) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getName().length() > 0)
                        setName(getName().substring(0, getName().length() - 1));
                    if (Util.isShiftHeld()) setName("");
                    return false;
                case Input.Keys.SPACE:
                    setName(getName() + " ");
                    return false;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                        if (!Util.isShiftHeld()) string = string.toLowerCase();
                        setName(getName() + string);
                    }
                    return false;
            }
        }

        if (isEditing(Section.ADDRESS)) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getAddress() < 10) setAddress(0);
                    if (Integer.toString(getAddress()).length() > 1) setAddress(Integer.parseInt(Integer.toString(getAddress()).substring(0, Integer.toString(getAddress()).length() - 1)));
                    if (Util.isShiftHeld()) setAddress(0);
                    return false;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("1234567890".contains(string)) {
                        setAddress(Integer.parseInt(getAddress() + string));
                    }
                    return false;
            }
        }

        return true;
    }

    public void clear() {
        this.name = "New Fixture";
        this.address = 1;
        this.count = 1;
        this.step = 0;
    }

    public void setFixture(Fixture fixture) {
        this.profile = null;
        this.fixture = fixture;
    }

    public void setProfile(Profile profile) {
        this.fixture = null;
        this.profile = profile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(int address) {
        this.address = Math.min(address, 512);
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void edit(Section section) {
        this.section = section;
    }

    public boolean isEditing(Section section) {
        return section.equals(this.section);
    }

    public Fixture getFixture() {
        return fixture;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public int getAddress() {
        return address;
    }

    public int getCount() {
        return count;
    }

    public int getStep() {
        return step;
    }

    public enum Section {
        NAME, ADDRESS;
    }
}