package dev.therealdan.lights.store;

public interface Store {

    void loadDefaults();

    void loadFromFile();

    void saveToFile();

    int count();
}