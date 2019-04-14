package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.ModelDesign;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.UIHandler;

import java.util.ArrayList;
import java.util.List;

public class ProfilesUI implements UI {

    private static ProfilesUI profilesUI;

    private final int ROWS = 6;

    private List<Profile> profiles = new ArrayList<>();

    private Profile selectedProfile = null;

    private int profileScroll = 0;
    private boolean canEditName = false;

    public ProfilesUI() {
        profilesUI = this;

        FileHandle fileHandle = Gdx.files.local("Lights/Profiles/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle child : fileHandle.list())
                load(child);
    }

    private void load(FileHandle fileHandle) {
        String name = fileHandle.name().replace(".txt", "");

        List<Channel> channels = new ArrayList<>();
        List<ModelDesign> modelDesigns = new ArrayList<>();

        boolean isChannel = false, isModel = false;
        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("Name: ")) {
                name = line.split(": ")[1];
                continue;
            } else if (line.startsWith("Channels:")) {
                isChannel = true;
                isModel = false;
                continue;
            } else if (line.startsWith("Models:")) {
                isChannel = false;
                isModel = true;
                continue;
            }

            if (isChannel) {
                channels.add(new Channel(line.substring(2)));
            } else if (isModel) {
                modelDesigns.add(ModelDesign.fromString(line.substring(2)));
            }
        }

        Profile profile = new Profile(name, modelDesigns, channels);
        add(profile);
    }

    @Override
    public void save() {
        UI.super.save();

        for (Profile profile : profiles()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Profiles/" + profile.getName() + ".txt");
            fileHandle.writeString("", false);

            fileHandle.writeString("Name: " + profile.getName() + "\r\n", true);

            fileHandle.writeString("Channels:\r\n", true);
            for (Channel channel : profile.channels()) {
                fileHandle.writeString("  " + channel.getType().toString() + ": ", true);
                StringBuilder offsets = new StringBuilder();
                for (int offset : channel.addressOffsets())
                    offsets.append(", ").append(offset);
                fileHandle.writeString(offsets.toString().replaceFirst(", ", ""), true);
                fileHandle.writeString("\r\n", true);
            }

            fileHandle.writeString("Models:\r\n", true);
            for (ModelDesign modelDesign : profile.getModelDesigns())
                fileHandle.writeString("- " + modelDesign.toString() + "\r\n", true);
        }
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.PROFILES);
        boolean interacted = false;
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        // PROFILES: #

        float x = getX();
        float y = getY();
        float profilesWidth = renderer.getWidth("Profiles: " + countProfiles()) + 10;
        float cellHeight = 30;

        for (Profile profile : profiles())
            profilesWidth = Math.max(profilesWidth, renderer.getWidth(profile.getName()) + 25);

        renderer.box(x, y, getWidth(), getHeight(), Lights.color.DARK);
        renderer.box(x, y, profilesWidth, cellHeight, Lights.color.DARK_BLUE, "Profiles: " + countProfiles(), Task.TextPosition.CENTER);
        drag(x, y, profilesWidth, cellHeight);
        y -= cellHeight;

        int i = 0;
        boolean display = false;
        int current = 0;
        for (Profile profile : profiles(true)) {
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
                add(new Profile("New Profile", new ArrayList<>(), new ArrayList<>()));
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
                    delete(getSelectedProfile());
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
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

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
        if (getProfileScroll() > Math.max(0, countProfiles() - (ROWS - 1))) profileScroll = Math.max(0, countProfiles() - (ROWS - 1));
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

    public static void add(Profile profile) {
        profilesUI.profiles.add(profile);
    }

    private static void delete(Profile profile) {
        profilesUI.profiles.remove(profile);
    }

    public static int countProfiles() {
        return profilesUI.profiles.size();
    }

    public static Profile profileByName(String name) {
        for (Profile profile : profiles())
            if (profile.getName().equalsIgnoreCase(name))
                return profile;

        return null;
    }

    public static List<Profile> profiles() {
        return profiles(false);
    }

    public static List<Profile> profiles(boolean alphabeticalOrder) {
        if (!alphabeticalOrder) return new ArrayList<>(profilesUI.profiles);

        List<Profile> profiles = profiles(false);
        List<Profile> alphabetical = new ArrayList<>();
        while (profiles.size() > 0) {
            Profile first = null;
            for (Profile profile : profiles)
                if (first == null || first.getName().compareTo(profile.getName()) > 0)
                    first = profile;
            profiles.remove(first);
            alphabetical.add(first);
        }
        return alphabetical;
    }
}