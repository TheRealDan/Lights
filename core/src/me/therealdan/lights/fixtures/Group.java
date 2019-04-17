package me.therealdan.lights.fixtures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static me.therealdan.lights.fixtures.Fixture.fixtureByID;

public class Group {

    private static HashSet<Group> groups = new HashSet<>();

    private List<Fixture> fixtures = new ArrayList<>();

    private String name;

    public Group(String name) {
        this.name = name;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void add(Fixture fixture) {
        fixtures.add(fixture);
    }

    public void remove(Fixture fixture) {
        fixtures.remove(fixture);
    }

    public void clear() {
        fixtures.clear();
    }

    public boolean contains(Fixture fixture) {
        return fixtures.contains(fixture);
    }

    public int size() {
        return fixtures.size();
    }

    public String getName() {
        return name;
    }

    public List<Fixture> fixtures() {
        return new ArrayList<>(fixtures);
    }

    public static void loadGroupsFromFile() {
        FileHandle fileHandle = Gdx.files.local("Lights/Groups/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle file : fileHandle.list())
                loadGroupFromFile(file);
    }

    private static void loadGroupFromFile(FileHandle fileHandle) {
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
                Fixture fixture = fixtureByID(id);
                if (fixture != null) group.add(fixture);
            }
        }

        add(group);
    }

    public static void saveGroupsToFile() {
        for (Group group : groups()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Groups/" + group.getName() + ".txt");
            fileHandle.writeString("", false);

            fileHandle.writeString("Name: " + group.getName() + "\r\n", true);

            fileHandle.writeString("Fixtures:\r\n", true);
            for (Fixture fixture : group.fixtures())
                fileHandle.writeString("  - " + fixture.getID() + "\r\n", true);
        }
    }

    public static void add(Group group) {
        groups.add(group);
    }

    public static Group groupByName(String name) {
        for (Group group : groups(SortBy.NAME))
            if (group.getName().equalsIgnoreCase(name))
                return group;

        return null;
    }

    public static List<Group> groups(SortBy... sortBy) {
        List<Group> groups = new ArrayList<>(Group.groups);
        if (sortBy.length == 0) return groups;

        List<Group> sorted = new ArrayList<>();

        while (groups.size() > 0) {
            Group next = null;
            for (Group group : groups) {
                if (next == null) {
                    next = group;
                } else {
                    sort:
                    for (Group.SortBy each : sortBy) {
                        switch (each) {
                            case NAME:
                                if (group.getName().compareTo(next.getName()) == 0) break;
                                if (group.getName().compareTo(next.getName()) > 0) next = group;
                                break sort;
                        }
                    }
                }
            }
            sorted.add(next);
            groups.remove(next);
        }

        return sorted;
    }

    // TODO - Add more sorting options
    public enum SortBy {
        NAME
    }
}