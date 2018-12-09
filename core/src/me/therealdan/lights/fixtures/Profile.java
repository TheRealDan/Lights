package me.therealdan.lights.fixtures;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import me.therealdan.lights.LightsCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Profile {

    private static List<Profile> profiles = new ArrayList<>();

    private static ModelBuilder modelBuilder = new ModelBuilder();

    private String name;
    private List<Channel> channels;

    private int virtualChannels;
    private int physicalChannels;

    private boolean virtualIntensity = false;

    private Vector3 dimensions;
    private LinkedHashMap<Model, Vector3> models = new LinkedHashMap<>();

    private Profile(String name, Channel... channels) {
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

        profiles.add(this);
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

    public List<Model> getModels() {
        if (models.size() == 0) {
            switch (getName()) {
                case "Ming":
                    dimensions = new Vector3(2f, 0.2f, 0.2f);
                    createModel(this, 2f / 3f, 0.2f, 0.2f, -(2f / 3f), 0f, 0f);
                    createModel(this, 2f / 3f, 0.2f, 0.2f);
                    createModel(this, 2f / 3f, 0.2f, 0.2f, 2f / 3f, 0f, 0f);
                    break;

                case "LED Strip":
                    dimensions = new Vector3(0.2f, 2f, 0.2f);
                    createModel(this, 0.2f, 2f, 0.2f);
                    createModel(this, 0.2f, 2f, 0.2f, 0.5f, 0f, 0f);
                    createModel(this, 0.2f, 2f, 0.2f, 1f, 0f, 0f);
                    createModel(this, 0.2f, 2f, 0.2f, 1.5f, 0f, 0f);
                    break;

                case "Par Can":
                    dimensions = new Vector3(0.5f, 0.5f, 0.5f);
                    createModel(this, 0.5f, 0.5f, 0.5f);
                    break;
            }
        }
        return new ArrayList<>(models.keySet());
    }

    public Vector3 getOffset(Model model) {
        return models.get(model);
    }

    public Vector3 getDimensions() {
        return dimensions;
    }

    private static void createModel(Profile profile, float width, float height, float depth) {
        createModel(profile, width, height, depth, 0, 0, 0);
    }

    private static void createModel(Profile profile, float width, float height, float depth, float xOffset, float yOffset, float zOffset) {
        Model model = modelBuilder.createBox(width, height, depth, new Material(ColorAttribute.createDiffuse(LightsCore.BLACK)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Vector3 offset = new Vector3(xOffset, yOffset, zOffset);
        profile.models.put(model, offset);
    }

    public static Profile create(String name, Channel... channels) {
        return new Profile(name, channels);
    }

    public static Profile get(String name) {
        for (Profile profile : Profile.values())
            if (profile.getName().equalsIgnoreCase(name))
                return profile;

        return null;
    }

    public static List<Profile> values() {
        return new ArrayList<>(profiles);
    }
}