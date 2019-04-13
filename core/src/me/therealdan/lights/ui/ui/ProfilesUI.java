package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.Lights;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.ModelDesign;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.UIHandler;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ProfilesUI implements UI {

    private static ProfilesUI profilesUI;

    private final int MIN_ROWS = 5;
    private final int MAX_ROWS = 8;

    private List<Profile> profiles = new ArrayList<>();

    private Profile selectedProfile = null;
    private Section section;

    private Profile profileScroll = null;
    private boolean canScrollProfiles = false;
    private int channelsScroll = 0;
    private boolean canScrollChannels = false;
    private int modelsScroll = 0;
    private boolean canScrollModels = false;

    public ProfilesUI() {
        profilesUI = this;

        FileHandle fileHandle = Gdx.files.local("Lights/Profiles/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle child : fileHandle.list())
                load(child);
    }

    private void load(FileHandle fileHandle) {
        String name = fileHandle.name().replace(".txt", "");

        int physicalChannels = 0;
        List<Channel> channels = new ArrayList<>();
        List<ModelDesign> modelDesigns = new ArrayList<>();

        boolean isChannel = false, isModel = false;
        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("Name: ")) {
                name = line.split(": ")[1];
                continue;
            } else if (line.startsWith("Physical Channels: ")) {
                physicalChannels = Integer.parseInt(line.split(": ")[1]);
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
        profile.setPhysicalChannels(physicalChannels);
        add(profile);
    }

    @Override
    public void save() {
        UI.super.save();

        for (Profile profile : profiles()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Profiles/" + profile.getName() + ".txt");
            fileHandle.writeString("", false);

            fileHandle.writeString("Name: " + profile.getName() + "\r\n", true);
            fileHandle.writeString("Physical Channels: " + profile.getPhysicalChannels() + "\r\n", true);

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

        Util.box(renderer, x, y, getWidth(), getHeight(), Lights.dark());
        Util.box(renderer, x, y, profilesWidth, cellHeight, Lights.DARK_BLUE, "Profiles: " + countProfiles(), Task.TextPosition.CENTER);
        drag(x, y, profilesWidth, cellHeight);
        canScrollProfiles = Util.containsMouse(x, y, profilesWidth, getHeight());
        y -= cellHeight;

        int i = 0;
        boolean display = false;
        for (Profile profile : profiles(true)) {
            if (profile.equals(getProfileScroll())) display = true;
            if (display) {
                Util.box(renderer, x, y, profilesWidth, cellHeight, profile.equals(getSelectedProfile()) ? Lights.DARK_GREEN : Lights.medium(), setWidth(renderer, profile.getName()));
                if (Util.containsMouse(x, y, profilesWidth, cellHeight) && canInteract()) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                        select(profile);
                    }
                }
                y -= cellHeight;
                if (++i == MAX_ROWS) break;
            }
        }

        Util.box(renderer, x, y, profilesWidth, cellHeight, Lights.medium(), "Add New");
        if (Util.containsMouse(x, y, profilesWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(500)) {
                add(new Profile("New Profile", new ArrayList<>(), new ArrayList<>()));
            }
        }
        y -= cellHeight;

        while (i < MIN_ROWS - 1) {
            i++;
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        if (!hasSelectedProfile()) {
            setWidth(profilesWidth);
            return interacted;
        }

        // PROFILE OPTIONS

        float optionsWidth = 200;

        x += profilesWidth;
        y = getY();
        Util.box(renderer, x, y, optionsWidth, cellHeight, Lights.DARK_BLUE, "Profile Options", Task.TextPosition.CENTER);
        drag(x, y, optionsWidth, cellHeight);
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, canEdit(Section.NAME) ? Lights.DARK_GREEN : Lights.medium(), "Name: " + getSelectedProfile().getName());
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(500)) {
                toggleEdit(Section.NAME);
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, canEdit(Section.PHYSICAL_CHANNELS) ? Lights.DARK_GREEN : Lights.medium(), "Physical Channels: " + getSelectedProfile().getPhysicalChannels());
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(500)) {
                toggleEdit(Section.PHYSICAL_CHANNELS);
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, canEdit(Section.CHANNELS) ? Lights.DARK_GREEN : Lights.medium(), "Channels");
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(500)) {
                toggleEdit(Section.CHANNELS);
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, canEdit(Section.MODELS) ? Lights.DARK_GREEN : Lights.medium(), "Models");
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(500)) {
                toggleEdit(Section.MODELS);
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, optionsWidth, cellHeight, Lights.medium(), Lights.RED, "Delete");
        if (Util.containsMouse(x, y, optionsWidth, cellHeight) && canInteract() && shift) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(500)) {
                delete(getSelectedProfile());
                select(null);
            }
        }
        y -= cellHeight;

        x += optionsWidth;

        // CHANNELS
        // TODO - Need channel and model editor, perhaps a new view (like V3D) that overlays everything?

        float channelsWidth = 0;
        if (canEdit(Section.CHANNELS)) {
            channelsWidth = 300;

            y = getY();
            Util.box(renderer, x, y, channelsWidth, cellHeight, Lights.DARK_BLUE, "Channels: " + getSelectedProfile().countChannels(), Task.TextPosition.CENTER);
            drag(x, y, channelsWidth, cellHeight);
            y -= cellHeight;
            canScrollChannels = Util.containsMouse(x, y, channelsWidth, getHeight());

            i = 0;
            display = false;
            int current = 0;
            for (Channel channel : getSelectedProfile().channels()) {
                if (current == getChannelsScroll()) display = true;
                current++;
                if (display) {
                    Util.box(renderer, x, y, channelsWidth, cellHeight, Lights.medium(), channel.getType().getName() + ": " + channel.addressOffsets());
                    y -= cellHeight;
                    if (i++ == MAX_ROWS) break;
                }
            }

            x += channelsWidth;
        }

        // MODELS

        float modelsWidth = 0;
        if (canEdit(Section.MODELS)) {
            modelsWidth = 300;

            y = getY();
            Util.box(renderer, x, y, modelsWidth, cellHeight, Lights.DARK_BLUE, "Models: " + getSelectedProfile().countModels(), Task.TextPosition.CENTER);
            drag(x, y, modelsWidth, cellHeight);
            y -= cellHeight;
            canScrollModels = Util.containsMouse(x, y, modelsWidth, getHeight());

            i = 0;
            display = false;
            int current = 0;
            for (ModelDesign modelDesign : getSelectedProfile().getModelDesigns()) {
                if (current == getModelsScroll()) display = true;
                current++;
                if (display) {
                    Util.box(renderer, x, y, modelsWidth, cellHeight, Lights.medium(), modelDesign.toString());
                    y -= cellHeight;
                    if (i++ == MAX_ROWS) break;
                }
            }
        }

        setWidth(profilesWidth + optionsWidth + channelsWidth + modelsWidth);
        return interacted;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        if (canEdit(Section.NAME)) {
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

        if (canEdit(Section.PHYSICAL_CHANNELS)) {
            String physicalChannels = Integer.toString(getSelectedProfile().getPhysicalChannels());
            if (keycode == Input.Keys.BACKSPACE) {
                if (physicalChannels.length() > 0) physicalChannels = physicalChannels.substring(0, physicalChannels.length() - 1);
                if (shift) physicalChannels = "0";
            } else {
                String string = Input.Keys.toString(keycode);
                if ("1234567890".contains(string)) physicalChannels += string;
            }
            if (physicalChannels.length() == 0) physicalChannels = "0";
            getSelectedProfile().setPhysicalChannels(Integer.parseInt(physicalChannels));
        }

        return true;
    }

    @Override
    public void scrolled(int amount) {
        if (canScrollProfiles()) {
            if (amount > 0) {
                boolean next = false;
                int i = 0;
                for (Profile profile : profiles(true)) {
                    if (i++ > countProfiles() - MAX_ROWS) return;
                    if (next) {
                        setProfileScroll(profile);
                        return;
                    }
                    if (profile.equals(getProfileScroll())) next = true;
                }
            } else {
                Profile previous = null;
                for (Profile profile : profiles(true)) {
                    if (profile.equals(getProfileScroll()) && previous != null) {
                        setProfileScroll(previous);
                        return;
                    }
                    previous = profile;
                }
            }
        }

        if (canScrollChannels()) {
            if (amount > 0) {
                if (getChannelsScroll() < getSelectedProfile().countChannels() - MAX_ROWS) channelsScroll += 1;
            } else {
                channelsScroll -= 1;
                if (getChannelsScroll() < 0) channelsScroll = 0;
            }
        }

        if (canScrollModels()) {
            if (amount > 0) {
                if (getModelsScroll() < getSelectedProfile().countModels() - MAX_ROWS) modelsScroll += 1;
            } else {
                modelsScroll -= 1;
                if (getModelsScroll() < 0) modelsScroll = 0;
            }
        }
    }

    private void toggleEdit(Section section) {
        if (section.equals(Section.CHANNELS)) {
            if (Section.MODELS.equals(this.section)) {
                edit(Section.CHANNELS_AND_MODELS);
                return;
            } else if (Section.CHANNELS_AND_MODELS.equals(this.section)) {
                edit(Section.MODELS);
                return;
            }
        }

        if (section.equals(Section.MODELS)) {
            if (Section.CHANNELS.equals(this.section)) {
                edit(Section.CHANNELS_AND_MODELS);
                return;
            } else if (Section.CHANNELS_AND_MODELS.equals(this.section)) {
                edit(Section.CHANNELS);
                return;
            }
        }

        if (canEdit(section)) {
            edit(null);
        } else {
            edit(section);
        }
    }

    private void edit(Section section) {
        this.section = section;
    }

    private boolean canEdit(Section section) {
        if (Section.CHANNELS_AND_MODELS.equals(this.section)) {
            if (section.equals(Section.CHANNELS)) return true;
            if (section.equals(Section.MODELS)) return true;
        }

        return section.equals(this.section);
    }

    private void setProfileScroll(Profile profile) {
        this.profileScroll = profile;
    }

    private boolean canScrollProfiles() {
        return canScrollProfiles;
    }

    private Profile getProfileScroll() {
        if (profileScroll == null) setProfileScroll(profiles(true).get(0));
        if (!profiles().contains(profileScroll)) setProfileScroll(profiles(true).get(0));
        return profileScroll;
    }

    private boolean canScrollChannels() {
        return canScrollChannels;
    }

    private int getChannelsScroll() {
        return channelsScroll;
    }

    private boolean canScrollModels() {
        return canScrollModels;
    }

    private int getModelsScroll() {
        return modelsScroll;
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

    public enum Section {
        NAME,
        PHYSICAL_CHANNELS,
        CHANNELS, MODELS, CHANNELS_AND_MODELS;
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