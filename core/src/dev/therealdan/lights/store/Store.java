package dev.therealdan.lights.store;

public interface Store {

    default void loadDefaults() {
    }

    void loadFromFile();

    void saveToFile();

    int count();

    default String getPath() {
        String className = getClass().getSimpleName().replace("Store", "");
        return "Lights/" + className + "/" + className + ".txt";
    }
}