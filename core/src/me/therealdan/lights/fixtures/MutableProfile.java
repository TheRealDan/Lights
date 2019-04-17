package me.therealdan.lights.fixtures;

import java.util.ArrayList;
import java.util.List;

public class MutableProfile extends Profile {

    private String name;
    private List<ModelDesign> modelDesigns = new ArrayList<>();
    private List<Channel> channels = new ArrayList<>();

    public MutableProfile(Profile profile) {
        super(profile.getName(), profile.getModelDesigns(), profile.channels());

        this.name = profile.getName();

        for (ModelDesign modelDesign : profile.getModelDesigns())
            modelDesigns.add(modelDesign.clone());

        for (Channel channel : profile.channels())
            channels.add(channel.clone());
    }

    @Override
    public void rename(String name) {
        this.name = name;
    }

    public void addChannel(Channel.Type type, Integer... addressOffsets) {
        this.channels.add(new Channel(type, addressOffsets));
    }

    // TODO - Might be more useful as object rather than index
    public void removeChannel(int index) {
        this.channels.remove(index);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ModelDesign> getModelDesigns() {
        return new ArrayList<>(modelDesigns);
    }

    @Override
    public List<Channel> channels() {
        return new ArrayList<>(channels);
    }

    public Profile buildProfile() {
        return new Profile(getName(), getModelDesigns(), channels());
    }
}