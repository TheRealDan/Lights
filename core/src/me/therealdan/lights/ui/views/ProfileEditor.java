package me.therealdan.lights.ui.views;

import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.view.Tab;

import java.util.ArrayList;
import java.util.List;

public class ProfileEditor implements Tab {

    private static ProfileEditor profileEditor;

    private List<Profile> profiles = new ArrayList<>();

    public ProfileEditor() {
        profileEditor = this;

        Profile dimmer = Profile.create("Dimmer",
                new Channel(Channel.Type.INTENSITY, 0),
                new Channel(Channel.Type.INTENSITY, 1),
                new Channel(Channel.Type.INTENSITY, 2),
                new Channel(Channel.Type.INTENSITY, 3),
                new Channel(Channel.Type.INTENSITY, 4),
                new Channel(Channel.Type.INTENSITY, 5),
                new Channel(Channel.Type.INTENSITY, 6),
                new Channel(Channel.Type.INTENSITY, 7),
                new Channel(Channel.Type.INTENSITY, 8),
                new Channel(Channel.Type.INTENSITY, 9),
                new Channel(Channel.Type.INTENSITY, 10),
                new Channel(Channel.Type.INTENSITY, 11)
        );

        Profile ming = Profile.create("Ming",
                new Channel(Channel.Type.INTENSITY, 4, 5, 6),
                new Channel(Channel.Type.RED, 4),
                new Channel(Channel.Type.GREEN, 5),
                new Channel(Channel.Type.BLUE, 6),

                new Channel(Channel.Type.INTENSITY, 7, 8, 9),
                new Channel(Channel.Type.RED, 7),
                new Channel(Channel.Type.GREEN, 8),
                new Channel(Channel.Type.BLUE, 9),

                new Channel(Channel.Type.INTENSITY, 10, 11, 12),
                new Channel(Channel.Type.RED, 10),
                new Channel(Channel.Type.GREEN, 11),
                new Channel(Channel.Type.BLUE, 12)
        );
        ming.setPhysicalChannels(13);

        Profile ledStrip = Profile.create("LED Strip",
                new Channel(Channel.Type.INTENSITY, 0, 1, 2),
                new Channel(Channel.Type.RED, 0),
                new Channel(Channel.Type.GREEN, 1),
                new Channel(Channel.Type.BLUE, 2)
        );

        Profile parcan = Profile.create("Par Can",
                new Channel(Channel.Type.INTENSITY, 0),
                new Channel(Channel.Type.RED, 1),
                new Channel(Channel.Type.GREEN, 2),
                new Channel(Channel.Type.BLUE, 3)
        );
        parcan.setPhysicalChannels(8);
    }

    @Override
    public void save() {

    }

    @Override
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
    }

    public static Profile profileByName(String name) {
        for (Profile profile : profiles())
            if (profile.getName().equalsIgnoreCase(name))
                return profile;

        return null;
    }

    public static List<Profile> profiles() {
        return new ArrayList<>(profileEditor.profiles);
    }
}