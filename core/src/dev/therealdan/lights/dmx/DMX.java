package dev.therealdan.lights.dmx;

import dev.therealdan.lights.ui.PanelHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DMX {

    public static final float MAX_CHANNELS = 512;

    private HashMap<Integer, Integer> _channels = new HashMap<>();
    private String _level;

    public DMX(String level) {
        _level = level;
    }

    public void copy(DMX dmx) {
        for (int channel = 1; channel <= MAX_CHANNELS; channel++)
            set(channel, dmx.get(channel));
    }

    public void set(int channel, int value) {
        _channels.put(
                Math.min(Math.max(channel, 1), (int) MAX_CHANNELS),
                Math.min(Math.max(value, 0), 255)
        );
    }

    public int get(int channel) {
        float value = _channels.getOrDefault(channel, 0);
        return (int) (value * PanelHandler.getMaster());
    }

    public List<Integer> active() {
        return new ArrayList<>(_channels.keySet());
    }

    public String getLevel() {
        return _level;
    }
}