package me.therealdan.lights.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;

public class FixtureEditor implements Visual {

    private Fixture fixture;

    private Profile profile;
    private String name;
    private int address;

    private Section section;

    public FixtureEditor() {
    }

    @Override
    public boolean draw(Renderer renderer) {
        float X = 0;
        float Y = Gdx.graphics.getHeight();
        float WIDTH = Gdx.graphics.getWidth();
        float cellHeight = 30;

        float x = X;
        float y = Y;
        float width = WIDTH;

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Fixture Creator", Task.TextPosition.CENTER);
        y -= cellHeight;

        // EXISTING FIXTURES
        width = WIDTH / 3;
        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Fixtures", Task.TextPosition.CENTER);
        y -= cellHeight;
        for (Fixture fixture : Fixture.fixtures(Fixture.SortBy.ID)) {
            renderer.box(x, y, width, cellHeight, fixture.equals(getFixture()) ? Lights.color.DARK_RED : Lights.color.MEDIUM, fixture.getName(), Task.TextPosition.LEFT_CENTER);
            if (Lights.mouse.contains(x, y, width, cellHeight)) {
                if (Lights.mouse.leftClicked()) {
                    setFixture(fixture);
                }
            }
            y -= cellHeight;
        }
        x += width;
        y = Y - cellHeight;

        // PROFILES
        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Profiles", Task.TextPosition.CENTER);
        y -= cellHeight;
        for (Profile profile : Profile.profiles(Profile.SortBy.NAME)) {
            renderer.box(x, y, width, cellHeight, profile.equals(getProfile()) ? Lights.color.DARK_RED : Lights.color.MEDIUM, profile.getName(), Task.TextPosition.LEFT_CENTER);
            if (Lights.mouse.contains(x, y, width, cellHeight)) {
                if (Lights.mouse.leftClicked()) {
                    setProfile(profile);
                }
            }
            y -= cellHeight;
        }
        x += width;
        y = Y - cellHeight;


        // ACTIONS
        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Actions", Task.TextPosition.CENTER);
        y -= cellHeight;

        if (getFixture() != null) {
            renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete");
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.keyboard.isShift()) {
                if (Lights.mouse.leftClicked()) {
                    Fixture.remove(getFixture());
                    setFixture(null);
                    return false;
                }
            }
            y -= cellHeight;
        }

        if (getProfile() != null) {
            renderer.box(x, y, width, cellHeight, isEditing(Section.NAME) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Name: " + getName());
            if (Lights.mouse.contains(x, y, width, cellHeight)) {
                if (Lights.mouse.leftClicked()) {
                    edit(Section.NAME);
                }
            }
            y -= cellHeight;

            renderer.box(x, y, width, cellHeight, isEditing(Section.ADDRESS) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Address: " + getAddress());
            if (Lights.mouse.contains(x, y, width, cellHeight)) {
                if (Lights.mouse.leftClicked()) {
                    edit(Section.ADDRESS);
                }
            }
            y -= cellHeight;

            renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Create Fixture");
            if (Lights.mouse.contains(x, y, width, cellHeight)) {
                if (Lights.mouse.leftClicked(500)) {
                    Fixture.add(new Fixture(getName(), getProfile(), getAddress(), Fixture.getFreeID()));
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
                Lights.openMainView();
                return false;
        }

        if (isEditing(Section.NAME)) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getName().length() > 0)
                        setName(getName().substring(0, getName().length() - 1));
                    if (Lights.keyboard.isShift()) setName("");
                    return false;
                case Input.Keys.SPACE:
                    setName(getName() + " ");
                    return false;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                        if (!Lights.keyboard.isShift()) string = string.toLowerCase();
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
                    if (Lights.keyboard.isShift()) setAddress(0);
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
        this.address = address;
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

    public enum Section {
        NAME, ADDRESS;
    }
}