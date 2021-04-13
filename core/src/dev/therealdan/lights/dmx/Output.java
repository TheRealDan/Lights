package dev.therealdan.lights.dmx;

import dev.therealdan.lights.interfaces.CustomSerialInterface;
import dev.therealdan.lights.interfaces.DMXInterface;
import dev.therealdan.lights.panels.panels.ConsolePanel;
import dev.therealdan.lights.store.SettingsStore;

import java.util.ArrayList;
import java.util.List;

public class Output {

    private List<DMX> _dmx = new ArrayList<>();
    private List<DMXInterface> _dmxInterfaces = new ArrayList<>();

    private boolean _frozen = false;

    public Output(SettingsStore settingsStore) {
        _dmx.add(new DMX("VISUALISER"));
        _dmx.add(new DMX("LIVE"));

        _dmxInterfaces.add(new CustomSerialInterface(settingsStore, this));
    }

    public void toggleFreeze() {
        _frozen = !_frozen;
        logFreeze();
    }

    public void freeze() {
        _frozen = true;
        logFreeze();
    }

    public void unfreeze() {
        _frozen = false;
        logFreeze();
    }

    public boolean isFrozen() {
        return _frozen;
    }

    private void logFreeze() {
        ConsolePanel.log(ConsolePanel.ConsoleColor.CYAN, isFrozen() ?
                "DMX Output frozen." :
                "DMX Output unfrozen."
        );
    }

    public DMX getDMXByLevel(String level) {
        for (DMX dmx : _dmx)
            if (dmx.getLevel().equalsIgnoreCase(level))
                return dmx;

        return null;
    }

    public List<DMX> getDMX() {
        return new ArrayList<>(_dmx);
    }

    public List<DMXInterface> getDMXInterfaces() {
        return new ArrayList<>(_dmxInterfaces);
    }
}