package me.therealdan.lights.fixtures;

import java.util.ArrayList;
import java.util.List;

public class Profile {

    private String name;
    private List<ModelDesign> modelDesigns;
    private List<Channel> channels;

    public Profile(String name, List<ModelDesign> modelDesigns, List<Channel> channels) {
        this.name = name;
        this.modelDesigns = modelDesigns;
        this.channels = new ArrayList<>(channels);
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
        List<Integer> address = new ArrayList<>();
        for (Channel channel : channels()) {
            for (int offsets : channel.addressOffsets()) {
                if (!address.contains(offsets)) {
                    physicalChannels++;
                    address.add(offsets);
                }
            }
        }
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
}