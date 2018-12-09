package me.therealdan.lights.fixtures;

import java.util.ArrayList;
import java.util.List;

public class Group {

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
}