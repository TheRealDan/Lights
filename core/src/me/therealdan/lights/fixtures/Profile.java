package me.therealdan.lights.fixtures;

import java.util.ArrayList;
import java.util.List;

public class Profile {

    private String name;
    private List<Channel> channels;

    private int virtualChannels;
    private int physicalChannels;

    private boolean virtualIntensity = false;

    private List<ModelDesign> modelDesigns;

    public Profile(String name, List<ModelDesign> modelDesigns, List<Channel> channels) {
        this.name = name;
        this.modelDesigns = modelDesigns;
        this.channels = new ArrayList<>(channels);

        this.virtualChannels = this.channels.size();

        this.physicalChannels = 0;
        List<Integer> address = new ArrayList<>();
        for (Channel channel : channels()) {
            for (int offsets : channel.addressOffsets()) {
                if (!address.contains(offsets)) {
                    this.physicalChannels++;
                    address.add(offsets);
                }
            }
        }

        for (Channel channel : channels()) {
            if (channel.getType().equals(Channel.Type.INTENSITY)) {
                for (Channel eachChannel : channels()) {
                    if (!eachChannel.getType().equals(Channel.Type.INTENSITY)) {
                        for (int offset : channel.addressOffsets()) {
                            if (eachChannel.addressOffsets().contains(offset)) {
                                virtualIntensity = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void setPhysicalChannels(int physicalChannels) {
        this.physicalChannels = physicalChannels;
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

    public int getVirtualChannels() {
        return virtualChannels;
    }

    public int getPhysicalChannels() {
        return physicalChannels;
    }

    public int countChannels() {
        return channels.size();
    }

    public int countModels() {
        return modelDesigns.size();
    }

    public boolean hasVirtualIntensity() {
        return virtualIntensity;
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