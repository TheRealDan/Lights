package dev.therealdan.lights.fixtures.fixture;

import dev.therealdan.lights.fixtures.fixture.profile.Channel;
import dev.therealdan.lights.fixtures.fixture.profile.ModelDesign;
import dev.therealdan.lights.fixtures.fixture.profile.MutableProfile;
import dev.therealdan.lights.util.sorting.Sortable;

import java.util.ArrayList;
import java.util.List;

public class Profile implements Sortable {

    private String name;
    private List<ModelDesign> modelDesigns;
    private List<Channel> channels;

    // TODO - Still using hardcoded model designs based off profile name
    private boolean usingHardcodedModelDesignsBasedOnProfileName = false;

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
    }

    @Override
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

    public boolean isUsingHardcodedModelDesignsBasedOnProfileName() {
        return usingHardcodedModelDesignsBasedOnProfileName;
    }

    public List<ModelDesign> getModelDesigns() {
        if (modelDesigns.size() == 0) {
            switch (getName()) {
                case "Ming":
                    modelDesigns.add(new ModelDesign(2f / 3f, 0.2f, 0.2f, -(2f / 3f), 0f, 0f));
                    modelDesigns.add(new ModelDesign(2f / 3f, 0.2f, 0.2f));
                    modelDesigns.add(new ModelDesign(2f / 3f, 0.2f, 0.2f, 2f / 3f, 0f, 0f));
                    usingHardcodedModelDesignsBasedOnProfileName = true;
                    break;

                case "LED Strip":
                    modelDesigns.add(new ModelDesign(0.2f, 2f, 0.2f));
                    modelDesigns.add(new ModelDesign(0.2f, 2f, 0.2f, 0.5f, 0f, 0f));
                    modelDesigns.add(new ModelDesign(0.2f, 2f, 0.2f, 1f, 0f, 0f));
                    modelDesigns.add(new ModelDesign(0.2f, 2f, 0.2f, 1.5f, 0f, 0f));
                    usingHardcodedModelDesignsBasedOnProfileName = true;
                    break;

                case "Par Can":
                    modelDesigns.add(new ModelDesign(0.5f));
                    usingHardcodedModelDesignsBasedOnProfileName = true;
                    break;
            }
        }
        return new ArrayList<>(modelDesigns);
    }
}