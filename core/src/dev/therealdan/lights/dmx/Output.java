package dev.therealdan.lights.dmx;

import dev.therealdan.lights.interfaces.CustomSerialInterface;
import dev.therealdan.lights.interfaces.DMXInterface;
import dev.therealdan.lights.panels.panels.ConsolePanel;
import dev.therealdan.lights.settings.SettingsStore;

import java.util.ArrayList;
import java.util.List;

public class Output {

    private List<DMXInterface> _dmxInterfaces = new ArrayList<>();

    private boolean _frozen = false;

    public Output(SettingsStore settingsStore) {
        _dmxInterfaces.add(new CustomSerialInterface(settingsStore, this));
    }

    public void toggleFreeze() {
        _frozen = !_frozen;
        log();
    }

    public void freeze() {
        _frozen = true;
        log();
    }

    public void unfreeze() {
        _frozen = false;
        log();
    }

    public boolean isFrozen() {
        return _frozen;
    }

    private void log() {
        ConsolePanel.log(ConsolePanel.ConsoleColor.CYAN, isFrozen() ?
                "DMX Output frozen." :
                "DMX Output unfrozen."
        );
    }

    public List<DMXInterface> getDMXInterfaces() {
        return new ArrayList<>(_dmxInterfaces);
    }
}