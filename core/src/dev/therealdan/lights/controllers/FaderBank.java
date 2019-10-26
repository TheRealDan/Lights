package dev.therealdan.lights.controllers;

import java.util.ArrayList;
import java.util.List;

public class FaderBank {

    private int id;
    private List<Integer> faders = new ArrayList<>();

    public FaderBank(int id) {
        this.id = id;
    }

    public void add(Fader... faders) {
        for (Fader fader : faders)
            this.faders.add(fader.getID());
    }

    public void remove(Fader fader) {
        set(getIndex(fader), null);
    }

    public void set(int index, Fader fader) {
        if (index == -1) return;
        if (fader != null) {
            faders.set(index, fader.getID());
        } else {
            faders.remove(index);
        }
    }

    public boolean contains(Fader fader) {
        return faders().contains(fader);
    }

    public int getIndex(Fader fader) {
        int index = 0;
        for (int id : faders) {
            if (getFader(index).equals(fader))
                return index;
            index++;
        }
        return -1;
    }

    public int getID() {
        return id;
    }

    public Fader getFader(int index) {
        return Fader.byID(faders.get(index));
    }

    public List<Fader> faders() {
        List<Fader> faders = new ArrayList<>();
        for (int id : this.faders)
            faders.add(Fader.byID(id));
        return faders;
    }
}