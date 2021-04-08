package dev.therealdan.lights.dmx;

import com.juanjo.openDmx.OpenDmx;
import dev.therealdan.lights.panels.panels.ConsolePanel;
import dev.therealdan.lights.panels.panels.TimingsPanel;
import dev.therealdan.lights.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public class Output {

    private boolean FROZEN = false;

    private Thread thread;

    private List<String> openPorts = new ArrayList<>();
    private String activePort = "Not Connected";
    private boolean connected = false;

    private int BAUDRATE = 74880;

    private long lastConnect = System.currentTimeMillis();
    private long lastSend = System.currentTimeMillis();

    public Output() {
        thread = new Thread("Output") {
            @Override
            public void run() {
                while (true) {
                    tick();
                }
            }
        };
    }

    public void start() {
        if (thread.isAlive()) return;
        thread.start();

        if (OpenDmx.connect(OpenDmx.OPENDMX_TX)) {
            this.connected = true;
            this.lastConnect = System.currentTimeMillis();
            ConsolePanel.log(ConsolePanel.ConsoleColor.YELLOW, "Dmx interface connected");
        } else {
            ConsolePanel.log(ConsolePanel.ConsoleColor.RED, "Error connecting to Dmx interface");
        }
    }

    private void tick() {
        if (System.currentTimeMillis() - lastSend < Setting.byName(Setting.Name.INTERVAL).getLong()) return;
        long timestamp = System.currentTimeMillis();
        lastSend = System.currentTimeMillis();

        if (!connected) return;

        if (System.currentTimeMillis() - lastConnect < Setting.byName(Setting.Name.CONNECTION_WAIT).getLong()) return;
        if (isFrozen()) return;

        try {
            for (int i = 0; i < 512; i++) {
                OpenDmx.setValue(i, DMX.get("OUTPUT").get(i + 1));
            }

            if (Setting.byName(Setting.Name.SHOW_DMX_SEND_DEBUG).isTrue())
                ConsolePanel.log(ConsolePanel.ConsoleColor.YELLOW, "DMX Sent");
        } catch (Exception e) {
            ConsolePanel.log(ConsolePanel.ConsoleColor.RED, e.getMessage());
        }
        long timeTaken = System.currentTimeMillis() - timestamp;
        TimingsPanel.set("Lights.output.tick()", "Output tick(): %mms %zms %ams", timeTaken);
    }

    public void freeze() {
        FROZEN = true;
    }

    public void unfreeze() {
        FROZEN = false;
    }

    public void toggleFreeze() {
        FROZEN = !FROZEN;
        ConsolePanel.log(ConsolePanel.ConsoleColor.CYAN, isFrozen() ?
                "DMX Output frozen." :
                "DMX Output unfrozen."
        );
    }

    public boolean isFrozen() {
        return FROZEN;
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        try {
            OpenDmx.disconnect();
        } catch (Exception e) {
            ConsolePanel.log(ConsolePanel.ConsoleColor.RED, e.getMessage());
            e.printStackTrace();
        }
        ConsolePanel.log(ConsolePanel.ConsoleColor.YELLOW, "Dmx interface disconnected");
        activePort = "Not Connected";
        connected = false;
    }

    public void setActivePort(String port) {
        activePort = port;
    }

    public String getActivePort() {
        return activePort;
    }

    public List<String> openPorts() {
        return new ArrayList<>(openPorts);
    }
}