package dev.therealdan.lights.dmx;

import com.fazecast.jSerialComm.SerialPort;
import dev.therealdan.lights.settings.Setting;
import dev.therealdan.lights.ui.ui.ConsoleUI;
import dev.therealdan.lights.ui.ui.TimingsUI;

import java.util.ArrayList;
import java.util.List;

public class Output {

    private boolean FROZEN = false;

    private Thread thread;

    private List<String> openPorts = new ArrayList<>();
    private SerialPort serialPort = null;
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
    }

    private void tick() {
        if (System.currentTimeMillis() - lastSend < Setting.byName(Setting.Name.INTERVAL).getLong()) return;
        long timestamp = System.currentTimeMillis();
        lastSend = System.currentTimeMillis();

        if (this.serialPort != null && !this.serialPort.getSystemPortName().equals(activePort)) {
            this.serialPort = null;
            if (Setting.byName(Setting.Name.SHOW_DMX_SEND_DEBUG).isTrue())
                ConsoleUI.log(ConsoleUI.ConsoleColor.YELLOW, "Port dropped");
        }

        boolean connected = false;
        List<String> openPorts = new ArrayList<>();
        for (SerialPort openPort : SerialPort.getCommPorts()) {
            openPorts.add(openPort.getSystemPortName());
            if (openPort.getSystemPortName().equals(activePort)) {
                if (this.serialPort == null) {
                    this.serialPort = openPort;
                    this.serialPort.openPort();
                    this.serialPort.setBaudRate(BAUDRATE);
                    this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, Setting.byName(Setting.Name.NEW_READ_TIMEOUT).getInt(), Setting.byName(Setting.Name.NEW_WRITE_TIMEOUT).getInt());
                    this.lastConnect = System.currentTimeMillis();
                    if (Setting.byName(Setting.Name.SHOW_DMX_SEND_DEBUG).isTrue())
                        ConsoleUI.log(ConsoleUI.ConsoleColor.YELLOW, "Port Connected");
                    break;
                } else if (this.serialPort != null) {
                    connected = true;
                }
            }
        }
        this.openPorts = openPorts;

        this.connected = connected;
        if (!connected) return;

        if (!serialPort.isOpen()) {
            serialPort.openPort();
            this.lastConnect = System.currentTimeMillis();
            if (Setting.byName(Setting.Name.SHOW_DMX_SEND_DEBUG).isTrue())
                ConsoleUI.log(ConsoleUI.ConsoleColor.YELLOW, "Port Opened");
        }

        if (System.currentTimeMillis() - lastConnect < Setting.byName(Setting.Name.CONNECTION_WAIT).getLong()) return;
        if (isFrozen()) return;

        try {
            byte[] bytes = DMX.get("OUTPUT").getNext();
            if (bytes == null) return;
            serialPort.getOutputStream().write(bytes);
            if (Setting.byName(Setting.Name.SHOW_DMX_SEND_DEBUG).isTrue())
                ConsoleUI.log(ConsoleUI.ConsoleColor.YELLOW, "DMX Sent");
        } catch (Exception e) {
            serialPort = null;
            ConsoleUI.log(ConsoleUI.ConsoleColor.RED, e.getMessage());
        }
        long timeTaken = System.currentTimeMillis() - timestamp;
        TimingsUI.set("Lights.output.tick()", "Output tick(): %mms %zms %ams", timeTaken);
    }

    public void freeze() {
        FROZEN = true;
    }

    public void unfreeze() {
        FROZEN = false;
    }

    public void toggleFreeze() {
        FROZEN = !FROZEN;
        ConsoleUI.log(ConsoleUI.ConsoleColor.CYAN, isFrozen() ?
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
            serialPort.closePort();
        } catch (Exception e) {
            ConsoleUI.log(ConsoleUI.ConsoleColor.RED, e.getMessage());
            e.printStackTrace();
        }
        serialPort = null;
        ConsoleUI.log(ConsoleUI.ConsoleColor.YELLOW, "Port Disconnected");
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