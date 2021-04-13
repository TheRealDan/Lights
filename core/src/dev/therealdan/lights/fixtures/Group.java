package dev.therealdan.lights.fixtures;

import dev.therealdan.lights.util.sorting.Sortable;

import java.util.ArrayList;
import java.util.List;

public class Group implements Sortable {

    private String _name;
    private List<Fixture> _fixtures = new ArrayList<>();

    public Group(String name) {
        _name = name;
    }

    public void rename(String name) {
        _name = name;
    }

    public void add(Fixture fixture) {
        _fixtures.add(fixture);
    }

    public void remove(Fixture fixture) {
        _fixtures.remove(fixture);
    }

    public void clear() {
        _fixtures.clear();
    }

    public boolean contains(Fixture fixture) {
        return _fixtures.contains(fixture);
    }

    public int size() {
        return _fixtures.size();
    }

    @Override
    public String getName() {
        return _name;
    }

    public List<Fixture> fixtures() {
        return new ArrayList<>(_fixtures);
    }
}