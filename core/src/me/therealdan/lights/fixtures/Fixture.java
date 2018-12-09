package me.therealdan.lights.fixtures;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import me.therealdan.lights.dmx.DMX;

import java.util.ArrayList;
import java.util.List;

public class Fixture {

    private String name;
    private Profile profile;
    private int address;
    private int id;

    private List<ModelInstance> models = new ArrayList<>();
    private Vector3 position;

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

    public void buildModel() {
        for (Model model : profile.getModels())
            models.add(new ModelInstance(model, new Vector3(
                    position.x + profile.getOffset(model).x,
                    position.y + profile.getOffset(model).y,
                    position.z + profile.getOffset(model).z
            )));
    }

    private void setColor(Color color) {
        for (ModelInstance model : models)
            setColor(model, color);
    }

    private void setColor(ModelInstance model, Color color) {
        model.materials.get(0).set(ColorAttribute.createDiffuse(color));
    }

    public void updateColor(DMX visualiser) {
        switch (getProfile()) {
            case "Ming":
                int parameter = 1;
                for (ModelInstance model : getModels()) {
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
        for (ModelInstance model : getModels())
            model.transform.setTranslation(position.set(
                    getPosition().x + x,
                    getPosition().y + y,
                    getPosition().z + z
            ));
    }

    public void teleport(float x, float y, float z) {
        for (ModelInstance model : getModels())
            model.transform.setTranslation(position.set(x, y, z));
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

    public List<ModelInstance> getModels() {
        if (models.size() == 0) buildModel();
        return models;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getDimensions() {
        return profile.getDimensions();
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