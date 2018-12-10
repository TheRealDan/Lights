package me.therealdan.lights.fixtures;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import me.therealdan.lights.LightsCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Profile {

    private static ModelBuilder modelBuilder = new ModelBuilder();

    private String name;
    private List<Channel> channels;

    private int virtualChannels;
    private int physicalChannels;

    private boolean virtualIntensity = false;

    private List<ModelDesign> modelDesigns = new ArrayList<>();

    public Profile(String name, int physicalChannels, Channel... channels) {
        this(name, channels);
        setPhysicalChannels(physicalChannels);
    }

    public Profile(String name, Channel... channels) {
        this.name = name;
        this.channels = new ArrayList<>(Arrays.asList(channels));

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

    public boolean hasVirtualIntensity() {
        return virtualIntensity;
    }

    public List<ModelDesign> getModelDesigns() {
        if (modelDesigns.size() == 0) {
            switch (getName()) {
                case "Ming":
                    modelDesigns.add(createModel(2f / 3f, 0.2f, 0.2f, -(2f / 3f), 0f, 0f));
                    modelDesigns.add(createModel(2f / 3f, 0.2f, 0.2f));
                    modelDesigns.add(createModel(2f / 3f, 0.2f, 0.2f, 2f / 3f, 0f, 0f));
                    break;

                case "LED Strip":
                    modelDesigns.add(createModel(0.2f, 2f, 0.2f));
                    modelDesigns.add(createModel(0.2f, 2f, 0.2f, 0.5f, 0f, 0f));
                    modelDesigns.add(createModel(0.2f, 2f, 0.2f, 1f, 0f, 0f));
                    modelDesigns.add(createModel(0.2f, 2f, 0.2f, 1.5f, 0f, 0f));
                    break;

                case "Par Can":
                    modelDesigns.add(createModel(0.5f, 0.5f, 0.5f));
                    break;
            }
        }
        return new ArrayList<>(modelDesigns);
    }

    private static ModelDesign createModel(float width, float height, float depth) {
        return createModel(width, height, depth, 0, 0, 0);
    }

    private static ModelDesign createModel(float width, float height, float depth, float xOffset, float yOffset, float zOffset) {
        return new ModelDesign(
                modelBuilder.createBox(width, height, depth, new Material(ColorAttribute.createDiffuse(LightsCore.BLACK)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal),
                new Vector3(xOffset, yOffset, zOffset),
                new Vector3(width, height, depth)
        );
    }
}