package me.therealdan.lights.ui;

import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.ui.view.Viewable;

import java.util.ArrayList;
import java.util.List;

public class ViewBar {

    private List<Viewable> views = new ArrayList<>();
    private Viewable activeTab;

    public ViewBar() {
    }

    public ViewBar(List<Tab> tabs) {
        for (Tab tab : tabs)
            register(tab);

        if (tabs.size() > 0)
            this.activeTab = tabs.get(0);
    }

    public void register(Viewable tab) {
        views.add(tab);
    }

    public void unregister(Viewable tab) {
        views.remove(tab);
    }

    public void setActiveTab(Viewable activeTab) {
        this.activeTab = activeTab;
    }

    public void nextTab(boolean previous) {
        if (previous) {
            previousTab();
        } else {
            nextTab();
        }
    }

    public void nextTab() {
        if (count() == 0) return;

        boolean next = false;
        for (Viewable tab : getViews()) {
            if (next) {
                setActiveTab(tab);
                return;
            }
            if (tab.equals(getActiveTab())) next = true;
        }

        setActiveTab(getFirstTab());
    }

    public void previousTab() {
        if (getViews().size() == 0) return;

        if (getFirstTab().equals(getActiveTab())) {
            setActiveTab(getLastTab());
            return;
        }

        if (getLastTab().equals(getActiveTab())) {
            setActiveTab(getViews().get(getViews().size() - 2));
            return;
        }

        boolean previous = false;
        Viewable last = null;
        for (Viewable tab : getViews()) {
            if (previous) {
                setActiveTab(last);
                return;
            }
            if (tab.equals(getActiveTab())) {
                previous = true;
            } else {
                last = tab;
            }
        }

        setActiveTab(getLastTab());
    }

    public Viewable getActiveTab() {
        return activeTab;
    }

    public Viewable getFirstTab() {
        if (count() == 0) return null;
        return getViews().get(0);
    }

    public Viewable getLastTab() {
        if (count() == 0) return null;
        return getViews().get(count() - 1);
    }

    public int count() {
        return views.size();
    }

    public List<Viewable> getViews() {
        return new ArrayList<>(views);
    }
}