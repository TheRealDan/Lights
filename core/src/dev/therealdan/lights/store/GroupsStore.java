package dev.therealdan.lights.store;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.fixtures.Group;
import dev.therealdan.lights.util.sorting.Sortable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static dev.therealdan.lights.util.sorting.Sortable.Sort.NAME;

public class GroupsStore implements Store {

    private FixturesStore _fixtureStore;

    private HashSet<Group> _groups = new HashSet<>();

    public GroupsStore(FixturesStore fixturesStore) {
        _fixtureStore = fixturesStore;
    }

    public void register(Group group) {
        _groups.add(group);
    }

    public void delete(Group group) {
        _groups.remove(group);
    }

    @Override
    public void loadFromFile() {
        FileHandle fileHandle = Gdx.files.local("Lights/Groups/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle file : fileHandle.list())
                loadGroupFromFile(file);
    }

    private void loadGroupFromFile(FileHandle fileHandle) {
        if (fileHandle.name().startsWith(".")) return;
        String name = fileHandle.name().replace(".txt", "");
        Group group = new Group(name);

        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("Name: ")) {
                group.rename(line.split(": ")[1]);
            } else if (line.startsWith("Fixtures:")) {
                // do nothing
            } else if (line.startsWith("  - ")) {
                int id = Integer.parseInt(line.replaceFirst("  - ", ""));
                Fixture fixture = _fixtureStore.getFixtureByID(id);
                if (fixture != null) group.add(fixture);
            }
        }

        register(group);
    }

    @Override
    public void saveToFile() {
        for (Group group : getGroups()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Groups/" + group.getName() + ".txt");
            fileHandle.writeString("", false);

            fileHandle.writeString("Name: " + group.getName() + "\r\n", true);

            fileHandle.writeString("Fixtures:\r\n", true);
            for (Fixture fixture : group.fixtures())
                fileHandle.writeString("  - " + fixture.getID() + "\r\n", true);
        }
    }

    @Override
    public int count() {
        return _groups.size();
    }

    public Group getGroupByName(String name) {
        for (Group group : getGroups(NAME))
            if (group.getName().equalsIgnoreCase(name))
                return group;

        return null;
    }

    public List<Group> getGroups(Sortable.Sort... sort) {
        if (sort.length == 0) return new ArrayList<>(_groups);

        List<Group> groups = new ArrayList<>();
        for (Sortable sortable : Sortable.sort(new ArrayList<>(_groups), sort))
            groups.add((Group) sortable);
        return groups;
    }
}
