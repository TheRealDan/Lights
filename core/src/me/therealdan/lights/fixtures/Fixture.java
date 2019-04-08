package me.therealdan.lights.fixtures;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import me.therealdan.lights.dmx.DMX;

import java.util.ArrayList;
import java.util.List;

public class Fixture {

    private String name;
    private Profile profile;
    private int address;
    private int id;

    private Vector3 position;
    private List<Model> models = new ArrayList<>();

    public Fixture(String name, Profile profile, int address, int id) {
        this(name, profile, address, id, new Vector3());
    }

    public Fixture(String name, Profile profile, int address, int id, Vector3 position) {
        this.name = name;
        this.profile = profile;
        this.address = address;
        this.id = id;
        this.position = position;
    }

    public void buildModels() {
        models.clear();
        for (ModelDesign modelDesign : profile.getModelDesigns()) {
            Vector3 position = new Vector3(
                    getPosition().x + modelDesign.getOffset().x,
                    getPosition().y + modelDesign.getOffset().y,
                    getPosition().z + modelDesign.getOffset().z
            );
            models.add(new Model(modelDesign, position));
        }
    }

    private void setColor(Color color) {
        for (Model model : getModels())
            setColor(model, color);
    }

    private void setColor(Model model, Color color) {
        model.setColor(color);
    }

    public void updateColor(DMX visualiser) {
        switch (getProfile()) {
            case "Ming":
                int parameter = 1;
                for (Model model : getModels()) {
                    setColor(model, new Color(
                            getValue(visualiser, Channel.Type.RED, parameter) / 255f,
                            getValue(visualiser, Channel.Type.GREEN, parameter) / 255f,
                            getValue(visualiser, Channel.Type.BLUE, parameter) / 255f,
                            1
                    ));
                    parameter++;
                }
                break;

            case "LED Strip":
                setColor(new Color(
                        getValue(visualiser, Channel.Type.RED, 1) / 255f,
                        getValue(visualiser, Channel.Type.GREEN, 1) / 255f,
                        getValue(visualiser, Channel.Type.BLUE, 1) / 255f,
                        1
                ));
                break;

            case "Par Can":
            default:
                float intensity = getValue(visualiser, Channel.Type.INTENSITY, 1) / 255f;
                setColor(new Color(
                        intensity * (getValue(visualiser, Channel.Type.RED, 1) / 255f),
                        intensity * (getValue(visualiser, Channel.Type.GREEN, 1) / 255f),
                        intensity * (getValue(visualiser, Channel.Type.BLUE, 1) / 255f),
                        1
                ));
                break;
        }
    }

    public void move(float x, float y, float z) {
        position.add(x, y, z);
        for (Model model : getModels())
            model.move(x, y, z);
    }

    public void teleport(float x, float y, float z) {
        position.set(x, y, z);
        for (Model model : getModels())
            model.teleport(x, y, z, true);
    }

    public void rename(String name) {
        this.name = name;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile.getName();
    }

    public int getAddress() {
        return address;
    }

    public int getID() {
        return id;
    }

    public Vector3 getPosition() {
        return position;
    }

    public List<Model> getModels() {
        if (models.size() == 0) buildModels();
        return new ArrayList<>(models);
    }

    public List<ModelInstance> getModelInstances() {
        List<ModelInstance> modelInstances = new ArrayList<>();
        for (Model model : getModels())
            modelInstances.add(model.getModelInstance());
        return modelInstances;
    }

    public int getVirtualChannels() {
        return profile.getVirtualChannels();
    }

    public int getPhysicalChannels() {
        return profile.getPhysicalChannels();
    }

    public boolean hasVirtualIntensity() {
        return profile.hasVirtualIntensity();
    }

    public float getHighestValue(DMX dmx, Channel.Type channelType) {
        float value = 0;
        for (int address : getAddresses(channelType))
            value = Math.max(dmx.get(address), value);
        return value;
    }

    public float getValue(DMX dmx, Channel.Type channelType, int parameter) {
        float value = 0;
        for (int address : getAddresses(channelType, parameter))
            value = Math.max(dmx.get(address), value);
        return value;
    }

    public boolean hasChannel(Channel.Type channelType) {
        for (Channel channel : channels())
            if (channel.getType().equals(channelType))
                return true;

        return false;
    }

    public List<Integer> getParameters(Channel.Type channelType) {
        List<Integer> parameters = new ArrayList<>();
        int parameter = 1;
        for (Channel channel : channels()) {
            if (channel.getType().equals(channelType)) {
                parameters.add(parameter);
                parameter++;
            }
        }
        return parameters;
    }

    public List<Integer> getAddresses() {
        List<Integer> addresses = new ArrayList<>();
        for (Channel channel : channels())
            for (int offset : channel.addressOffsets())
                addresses.add(getAddress() + offset);
        return addresses;
    }

    public List<Integer> getAddresses(Channel.Type channelType) {
        List<Integer> addresses = new ArrayList<>();
        for (Channel channel : channels())
            if (channelType.equals(channel.getType()))
                for (int offset : channel.addressOffsets())
                    addresses.add(getAddress() + offset);
        return addresses;
    }

    public List<Integer> getAddresses(Channel.Type channelType, int parameter) {
        int current = 1; // First parameter is at 1
        for (Channel channel : channels()) {
            if (channelType.equals(channel.getType())) {
                if (current == parameter) {
                    List<Integer> addresses = new ArrayList<>();
                    for (int offset : channel.addressOffsets())
                        addresses.add(getAddress() + offset);
                    return addresses;
                }
                current++;
            }
        }

        return new ArrayList<>();
    }

    public List<Channel> channels() {
        return profile.channels();
    }

}