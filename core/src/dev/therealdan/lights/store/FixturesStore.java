package dev.therealdan.lights.store;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.fixtures.fixture.Profile;
import dev.therealdan.lights.util.sorting.Sortable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static dev.therealdan.lights.util.sorting.Sortable.Sort.ID;

public class FixturesStore implements Store {

    private ProfilesStore _profileByName;

    private HashSet<Fixture> _fixtures = new HashSet<>();

    public FixturesStore(ProfilesStore profilesStore) {
        _profileByName = profilesStore;
    }

    public void register(Fixture fixture) {
        _fixtures.add(fixture);
    }

    public void delete(Fixture fixture) {
        _fixtures.remove(fixture);
    }

    @Override
    public void loadFromFile() {
        FileHandle fileHandle = Gdx.files.local("Lights/Fixtures/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle file : fileHandle.list())
                loadFixtureFromFile(file);
    }

    private void loadFixtureFromFile(FileHandle fileHandle) {
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
                profile = _profileByName.getProfileByName(line.split(": ")[1]);
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

        register(new Fixture(name, profile, address, id, position, fileName));
    }

    @Override
    public void saveToFile() {
        for (Fixture fixture : getFixtures()) {
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

    @Override
    public int count() {
        return _fixtures.size();
    }

    public int getFreeID() {
        int id = 0;
        while (getFixtureByID(id) != null) id++;
        return id;
    }

    public Fixture getFixtureByID(int id) {
        for (Fixture fixture : getFixtures(ID))
            if (fixture.getID() == id)
                return fixture;

        return null;
    }

    public Fixture getFixtureByName(String name) {
        for (Fixture fixture : getFixtures())
            if (fixture.getName().equalsIgnoreCase(name))
                return fixture;

        return null;
    }

    public List<Fixture> getFixtures(Sortable.Sort... sort) {
        if (sort.length == 0) return new ArrayList<>(_fixtures);

        List<Fixture> fixtures = new ArrayList<>();
        for (Sortable sortable : Sortable.sort(new ArrayList<>(_fixtures), sort))
            fixtures.add((Fixture) sortable);
        return fixtures;
    }
}
