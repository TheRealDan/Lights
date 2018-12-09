package me.therealdan.lights.ui.view;

import java.util.ArrayList;
import java.util.List;

public interface Tab extends Viewable {

    List<Tab> tabs = new ArrayList<>();

    default void update() {

    }

    default boolean equals(Tab tab) {
        if (tab == null) return false;
        return getName().equals(tab.getName());
    }

    static void register(Tab tab) {
        tabs.add(tab);
    }

    static int getEdge() {
        return 10;
    }

    static Tab byName(String name) {
        for (Tab tab : values())
            if (tab.getName().equalsIgnoreCase(name))
                return tab;

        return null;
    }

    static List<Tab> values() {
        return new ArrayList<>(tabs);
    }
}