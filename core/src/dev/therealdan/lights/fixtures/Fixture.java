package dev.therealdan.lights.fixtures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import dev.therealdan.lights.dmx.DMX;
import dev.therealdan.lights.fixtures.fixture.Model;
import dev.therealdan.lights.fixtures.fixture.Profile;
import dev.therealdan.lights.fixtures.fixture.profile.Channel;
import dev.therealdan.lights.fixtures.fixture.profile.ModelDesign;
import dev.therealdan.lights.util.sorting.Sortable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static dev.therealdan.lights.util.sorting.Sortable.Sort.ID;

public class Fixture implements Sortable {

    private static HashSet<Fixture> fixtures = new HashSet<>();

    private String fileName;
    private String name;
    private Profile profile;
    private int address;
    private int id;

    private Vector3 location;
    private List<Model> models = new ArrayList<>();

    public Fixture(String name, Profile profile, int address, int id) {
        this(name, profile, address, id, new Vector3());
    }

    public Fixture(String name, Profile profile, int address, int id, Vector3 position, String fileName) {
        this(name, profile, address, id, position);
        this.fileName = fileName;
    }

    public Fixture(String name, Profile profile, int address, int id, Vector3 location) {
        this.name = name;
        this.profile = profile;
        this.address = address;
        this.id = id;
        this.location = location;
    }

    public void buildModels() {
        models.clear();
        for (ModelDesign modelDesign : profile.getModelDesigns()) {
            Vector3 position = new Vector3(
                    getLocation().x + modelDesign.getOffset().x,
                    getLocation().y + modelDesign.getOffset().y,
                    getLocation().z + modelDesign.getOffset().z
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
        // TODO - Still using hardcoded colors for 3DV models based on profile names

        boolean hasColor = false;
        for (Channel channel : profile.channels()) {
            if (channel.getType().getCategory().equals(Channel.Category.COLOR)) {
                hasColor = true;
                continue;
            }
        }

        if (!hasColor) {
            float value = getValue(visualiser, Channel.Type.INTENSITY, 1) / 255f;
            for (Model model : getModels()) setColor(model, new Color(value, value, value, 1));
            return;
        }

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
        location.add(x, y, z);
        for (Model model : getModels())
            model.move(x, y, z);
    }

    public void teleport(float x, float y, float z) {
        location.set(x, y, z);
        for (Model model : getModels())
            model.teleport(x, y, z, true);
    }

    public void rename(String name) {
        this.name = name;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getFileName() {
        if (fileName == null) fileName = getID() + "_" + getName();
        return fileName;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile.getName();
    }

    public int getAddress() {
        return address;
    }

    @Override
    public int getID() {
        return id;
    }

    public Vector3 getLocation() {
        return location;
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

    public static void add(Fixture fixture) {
        fixtures.add(fixture);
    }

    public static void remove(Fixture fixture) {
        fixtures.remove(fixture);
    }

    public static void loadFixturesFromFile() {
        FileHandle fileHandle = Gdx.files.local("Lights/Fixtures/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle file : fileHandle.list())
                loadFixtureFromFile(file);
    }

    private static void loadFixtureFromFile(FileHandle fileHandle) {
        if (fileHandle.isDirectory()) return;

        String fileName = fileHandle.toString().replaceFirst("Lights/Fixtures/", "").replace(".txt", "");
        String name = null;
        Profile profile = null;
        int address = 0;
        int id = -1;
        Vector3 position = new Vector3();

        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("Name: ")) {
                name = line.split(": ")[1];
            } else if (line.startsWith("Profile: ")) {
                profile = Profile.profileByName(line.split(": ")[1]);
            } else if (line.startsWith("Address: ")) {
                address = Integer.parseInt(line.split(": ")[1]);
            } else if (line.startsWith("ID: ")) {
                id = Integer.parseInt(line.split(": ")[1]);

            } else if (line.startsWith("  X: ")) {
                position.set(Float.parseFloat(line.split(": ")[1]), position.y, position.z);
            } else if (line.startsWith("  Y: ")) {
                position.set(position.x, Float.parseFloat(line.split(": ")[1]), position.z);
            } else if (line.startsWith("  Z: ")) {
                position.set(position.x, position.y, Float.parseFloat(line.split(": ")[1]));
            }
        }

        if (name == null) return;
        if (profile == null) return;
        if (address == 0) return;
        if (id <= -1) return;

        add(new Fixture(name, profile, address, id, position, fileName));
    }

    public static void saveFixturesToFile() {
        for (Fixture fixture : fixtures()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Fixtures/" + fixture.getFileName() + ".txt");
            fileHandle.writeString("", false);

            fileHandle.writeString("Name: " + fixture.getName() + "\r\n", true);
            fileHandle.writeString("Profile: " + fixture.getProfile() + "\r\n", true);
            fileHandle.writeString("Address: " + fixture.getAddress() + "\r\n", true);
            fileHandle.writeString("ID: " + fixture.getID() + "\r\n", true);

            Vector3 position = fixture.getLocation();
            fileHandle.writeString("Position:\r\n", true);
            fileHandle.writeString("  X: " + position.x + "\r\n", true);
            fileHandle.writeString("  Y: " + position.y + "\r\n", true);
            fileHandle.writeString("  Z: " + position.z + "\r\n", true);
        }
    }

    public static int getFreeID() {
        int id = 0;
        while (fixtureByID(id) != null) id++;
        return id;
    }

    public static int count() {
        return fixtures.size();
    }

    public static Fixture fixtureByID(int id) {
        for (Fixture fixture : fixtures(ID))
            if (fixture.getID() == id)
                return fixture;

        return null;
    }

    public static Fixture fixtureByName(String name) {
        for (Fixture fixture : fixtures())
            if (fixture.getName().equalsIgnoreCase(name))
                return fixture;

        return null;
    }

    public static List<Fixture> fixtures(Sort... sort) {
        if (sort.length == 0) return new ArrayList<>(Fixture.fixtures);

        List<Fixture> fixtures = new ArrayList<>();
        for (Sortable sortable : Sortable.sort(new ArrayList<>(Fixture.fixtures), sort))
            fixtures.add((Fixture) sortable);
        return fixtures;
    }
}