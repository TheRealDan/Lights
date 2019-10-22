package me.therealdan.lights.fixtures.fixture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.fixtures.fixture.profile.Channel;
import me.therealdan.lights.fixtures.fixture.profile.ModelDesign;
import me.therealdan.lights.fixtures.fixture.profile.MutableProfile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Profile {

    private static HashSet<Profile> profiles = new HashSet<>();

    private String name;
    private List<ModelDesign> modelDesigns;
    private List<Channel> channels;

    public Profile(String name, List<ModelDesign> modelDesigns, List<Channel> channels) {
        this.name = name;
        this.modelDesigns = modelDesigns;
        this.channels = new ArrayList<>(channels);
    }

    public void update(MutableProfile mutableProfile) {
        this.name = mutableProfile.getName();
        this.modelDesigns = mutableProfile.getModelDesigns();
        this.channels = mutableProfile.channels();
    }

    public void rename(String name) {
        this.name = name;

        // TODO - Need to test name changing, it should be fine since Fixtures hold an object reference rather than the profile name as a string
    }

    public String getName() {
        return name;
    }

    public List<Channel> channels() {
        return new ArrayList<>(channels);
    }

    public int getPhysicalChannels() {
        int physicalChannels = 0;
        for (Channel channel : channels())
            for (int offsets : channel.addressOffsets())
                if (offsets + 1 > physicalChannels)
                    physicalChannels = offsets + 1;
        return physicalChannels;

        // TODO - Could probably cache this and update whenever channels are modified
    }

    public int getVirtualChannels() {
        return channels.size();
    }

    public int countModels() {
        return modelDesigns.size();
    }

    public boolean hasVirtualIntensity() {
        for (Channel channel : channels()) {
            if (channel.getType().equals(Channel.Type.INTENSITY)) {
                for (Channel eachChannel : channels()) {
                    if (!eachChannel.getType().equals(Channel.Type.INTENSITY)) {
                        for (int offset : channel.addressOffsets()) {
                            if (eachChannel.addressOffsets().contains(offset)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;

        // TODO - Could probably cache this and update whenever channels are modified
    }

    public List<ModelDesign> getModelDesigns() {
        // TODO - Still using hardcoded model designs based off profile name
        if (modelDesigns.size() == 0) {
            switch (getName()) {
                case "Ming":
                    modelDesigns.add(new ModelDesign(2f / 3f, 0.2f, 0.2f, -(2f / 3f), 0f, 0f));
                    modelDesigns.add(new ModelDesign(2f / 3f, 0.2f, 0.2f));
                    modelDesigns.add(new ModelDesign(2f / 3f, 0.2f, 0.2f, 2f / 3f, 0f, 0f));
                    break;

                case "LED Strip":
                    modelDesigns.add(new ModelDesign(0.2f, 2f, 0.2f));
                    modelDesigns.add(new ModelDesign(0.2f, 2f, 0.2f, 0.5f, 0f, 0f));
                    modelDesigns.add(new ModelDesign(0.2f, 2f, 0.2f, 1f, 0f, 0f));
                    modelDesigns.add(new ModelDesign(0.2f, 2f, 0.2f, 1.5f, 0f, 0f));
                    break;

                case "Par Can":
                    modelDesigns.add(new ModelDesign(0.5f));
                    break;
            }
        }
        return new ArrayList<>(modelDesigns);
    }

    public static void loadProfilesFromFile() {
        FileHandle fileHandle = Gdx.files.local("Lights/Profiles/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle child : fileHandle.list())
                loadProfileFromFile(child);
    }

    private static void loadProfileFromFile(FileHandle fileHandle) {
        if (fileHandle.name().contains(".DS_Store")) return;

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
        Profile.add(profile);
    }

    public static void saveProfilesToFile() {
        for (Profile profile : Profile.profiles()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Profiles/" + profile.getName() + ".txt");
            fileHandle.writeString("", false);

            fileHandle.writeString("Name: " + profile.getName() + "\r\n", true);

            fileHandle.writeString("Channels:\r\n", true);
            for (Channel channel : profile.channels())
                fileHandle.writeString("  " + channel.toString() + "\r\n", true);

            fileHandle.writeString("Models:\r\n", true);
            for (ModelDesign modelDesign : profile.getModelDesigns())
                fileHandle.writeString("- " + modelDesign.toString() + "\r\n", true);
        }
    }

    public static void add(Profile profile) {
        profiles.add(profile);
    }

    public static void delete(Profile profile) {
        profiles.remove(profile);
    }

    public static int count() {
        return profiles.size();
    }

    public static Profile profileByName(String name) {
        for (Profile profile : profiles(SortBy.NAME))
            if (profile.getName().equalsIgnoreCase(name))
                return profile;

        return null;
    }

    public static List<Profile> profiles(SortBy... sortBy) {
        List<Profile> profiles = new ArrayList<>(Profile.profiles);
        if (sortBy.length == 0) return profiles;

        List<Profile> sorted = new ArrayList<>();

        while (profiles.size() > 0) {
            Profile next = null;
            for (Profile profile : profiles) {
                if (next == null) {
                    next = profile;
                } else {
                    sort:
                    for (Profile.SortBy each : sortBy) {
                        switch (each) {
                            case NAME:
                                if (profile.getName().compareTo(next.getName()) == 0) break;
                                if (profile.getName().compareTo(next.getName()) < 0) next = profile;
                                break sort;
                        }
                    }
                }
            }
            sorted.add(next);
            profiles.remove(next);
        }

        return sorted;
    }

    // TODO - Add more sorting options
    public enum SortBy {
        NAME
    }
}