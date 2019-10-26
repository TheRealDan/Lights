package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.fixtures.fixture.Profile;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.ui.UIHandler;

import java.util.ArrayList;

import static dev.therealdan.lights.util.sorting.Sortable.Sort.NAME;

public class ProfilesPanel implements Panel {

    private final int ROWS = 6;

    private Profile selectedProfile = null;

    private int profileScroll = 0;
    private boolean canEditName = false;

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.PROFILES);
        boolean interacted = false;
        boolean shift = Lights.keyboard.isShift();

        // PROFILES: #

        float x = getX();
        float y = getY();
        float profilesWidth = renderer.getWidth("Profiles: " + Profile.count()) + 10;
        float cellHeight = 30;

        for (Profile profile : Profile.profiles())
            profilesWidth = Math.max(profilesWidth, renderer.getWidth(profile.getName()) + 25);

        renderer.box(x, y, getWidth(), getHeight(), Lights.color.DARK);
        renderer.box(x, y, profilesWidth, cellHeight, Lights.color.DARK_BLUE, "Profiles: " + Profile.count(), Task.TextPosition.CENTER);
        drag(x, y, profilesWidth, cellHeight);
        y -= cellHeight;

        int i = 0;
        boolean display = false;
        int current = 0;
        for (Profile profile : Profile.profiles(NAME)) {
            if (current == getProfileScroll()) display = true;
            current++;
            if (display) {
                renderer.box(x, y, profilesWidth, cellHeight, profile.equals(getSelectedProfile()) ? Lights.color.DARK_GREEN : Lights.color.MEDIUM, setWidth(renderer, profile.getName()));
                if (Lights.mouse.contains(x, y, profilesWidth, cellHeight) && canInteract()) {
                    interacted = true;
                    if (Lights.mouse.leftClicked()) {
                        select(profile);
                    }
                }
                y -= cellHeight;
                if (++i == ROWS - 1) break;
            }
        }

        renderer.box(x, y, profilesWidth, cellHeight, Lights.color.MEDIUM, Lights.color.GREEN, "Add New");
        if (Lights.mouse.contains(x, y, profilesWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                Profile newProfile = new Profile("New Profile", new ArrayList<>(), new ArrayList<>());
                Profile.add(newProfile);
                select(newProfile);
            }
        }
        y -= cellHeight;

        while (++i < ROWS) y -= cellHeight;

        setHeightBasedOnY(y);
        if (!hasSelectedProfile()) {
            setWidth(profilesWidth);
            return interacted;
        }

        // PROFILE OPTIONS

        float optionsWidth = 200;

        x += profilesWidth;
        y = getY();
        renderer.box(x, y, optionsWidth, cellHeight, Lights.color.DARK_BLUE, "Profile Options", Task.TextPosition.CENTER);
        drag(x, y, optionsWidth, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, canEditName() ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Name: " + getSelectedProfile().getName());
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Lights.mouse.leftClicked(500)) toggleCanEditName();
        }
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, Lights.color.MEDIUM, "Physical Channels: " + getSelectedProfile().getPhysicalChannels());
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) interacted = true;
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, Lights.color.MEDIUM, "Virtual Channels: " + getSelectedProfile().getVirtualChannels());
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) interacted = true;
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, Lights.color.MEDIUM, "Model: " + getSelectedProfile().countModels());
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) interacted = true;
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Advanced");
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Lights.mouse.leftClicked()) Lights.openProfileEditor(getSelectedProfile());
        }
        y -= cellHeight;

        renderer.box(x, y, optionsWidth, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete");
        if (Lights.mouse.contains(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Lights.mouse.leftClicked()) {
                if (shift) {
                    Profile.delete(getSelectedProfile());
                    select(null);
                    return interacted;
                }
            }
        }
        y -= cellHeight;

        setWidth(profilesWidth + optionsWidth);
        return interacted;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Lights.keyboard.isShift();

        if (canEditName()) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getSelectedProfile().getName().length() > 0)
                        getSelectedProfile().rename(getSelectedProfile().getName().substring(0, getSelectedProfile().getName().length() - 1));
                    if (shift) getSelectedProfile().rename("");
                    break;
                case Input.Keys.SPACE:
                    getSelectedProfile().rename(getSelectedProfile().getName() + " ");
                    break;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                        if (!shift) string = string.toLowerCase();
                        getSelectedProfile().rename(getSelectedProfile().getName() + string);
                    }
            }
        }

        return true;
    }

    @Override
    public void scrolled(int amount) {
        profileScroll += amount;
        if (getProfileScroll() < 0) profileScroll = 0;
        if (getProfileScroll() > Math.max(0, Profile.count() - (ROWS - 1))) profileScroll = Math.max(0, Profile.count() - (ROWS - 1));
    }

    private void toggleCanEditName() {
        this.canEditName = !this.canEditName;
    }

    private boolean canEditName() {
        return canEditName;
    }

    private int getProfileScroll() {
        return profileScroll;
    }

    private void select(Profile profile) {
        this.selectedProfile = profile;
    }

    private boolean hasSelectedProfile() {
        return getSelectedProfile() != null;
    }

    private Profile getSelectedProfile() {
        return selectedProfile;
    }
}