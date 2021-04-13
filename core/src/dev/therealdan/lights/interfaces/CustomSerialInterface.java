package dev.therealdan.lights.interfaces;

import com.fazecast.jSerialComm.SerialPort;
import dev.therealdan.lights.dmx.DMX;
import dev.therealdan.lights.dmx.Output;
import dev.therealdan.lights.panels.panels.ConsolePanel;
import dev.therealdan.lights.panels.panels.TimingsPanel;
import dev.therealdan.lights.settings.Setting;
import dev.therealdan.lights.store.SettingsStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomSerialInterface implements DMXInterface {

    private SettingsStore _settingsStore;
    private Output _output;

    private Thread thread;

    private List<String> openPorts = new ArrayList<>();
    private SerialPort serialPort = null;
    private String activePort = "Not Connected";
    private boolean connected = false;

    private int BAUDRATE = 74880;

    private long lastConnect = System.currentTimeMillis();
    private long lastSend = System.currentTimeMillis();

    private HashMap<Integer, Integer> _lastSent = new HashMap<>();
    private ArrayList<Long> _channelsPerSecondCounter = new ArrayList<>();
    private int _next = 1;

    public CustomSerialInterface(SettingsStore settingsStore, Output output) {
        _settingsStore = settingsStore;
        _output = output;

        thread = new Thread("Output") {
            @Override
            public void run() {
                while (true) {
                    tick();
                }
            }
        };

        thread.start();
    }

    private void tick() {
        if (System.currentTimeMillis() - lastSend < _settingsStore.getByKey(Setting.Key.INTERVAL).getLong()) return;
        long timestamp = System.currentTimeMillis();
        lastSend = System.currentTimeMillis();

        if (this.serialPort != null && !this.serialPort.getSystemPortName().equals(activePort)) {
            this.serialPort = null;
            if (_settingsStore.getByKey(Setting.Key.SHOW_DMX_SEND_DEBUG).isTrue())
                ConsolePanel.log(ConsolePanel.ConsoleColor.YELLOW, "Port dropped");
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
                    this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, _settingsStore.getByKey(Setting.Key.NEW_READ_TIMEOUT).getInt(), _settingsStore.getByKey(Setting.Key.NEW_WRITE_TIMEOUT).getInt());
                    this.lastConnect = System.currentTimeMillis();
                    if (_settingsStore.getByKey(Setting.Key.SHOW_DMX_SEND_DEBUG).isTrue())
                        ConsolePanel.log(ConsolePanel.ConsoleColor.YELLOW, "Port Connected");
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
            if (_settingsStore.getByKey(Setting.Key.SHOW_DMX_SEND_DEBUG).isTrue())
                ConsolePanel.log(ConsolePanel.ConsoleColor.YELLOW, "Port Opened");
        }

        if (System.currentTimeMillis() - lastConnect < _settingsStore.getByKey(Setting.Key.CONNECTION_WAIT).getLong()) return;
        if (_output.isFrozen()) return;

        try {
            DMX dmx = _output.getDMXByLevel("LIVE");
            byte[] bytes = getNextBytes(dmx);
            if (bytes == null) return;
            serialPort.getOutputStream().write(bytes);
            if (_settingsStore.getByKey(Setting.Key.SHOW_DMX_SEND_DEBUG).isTrue())
                ConsolePanel.log(ConsolePanel.ConsoleColor.YELLOW, "DMX Sent");
        } catch (Exception e) {
            serialPort = null;
            ConsolePanel.log(ConsolePanel.ConsoleColor.RED, e.getMessage());
        }
        long timeTaken = System.currentTimeMillis() - timestamp;
        TimingsPanel.set("Lights.output.tick()", "Output tick(): %mms %zms %ams", timeTaken);
    }

    public byte[] getNextBytes(DMX dmx) {
        StringBuilder data = new StringBuilder();

        if (_settingsStore.getByKey(Setting.Key.CONTINUOUS).isTrue()) {
            for (int address = _next; address < _next + _settingsStore.getByKey(Setting.Key.CHANNELS_PER_SEND).getInt(); address++) {
                int value = dmx.get(address);
                if (value < 10) data.append("0");
                if (value < 100) data.append("0");
                data.append(Double.toString(value).replace(".0", ""));
                if (address < 10) data.append("0");
                if (address < 100) data.append("0");
                data.append(Double.toString(address).replace(".0", ""));
                data.append(" ");
            }

            _next += _settingsStore.getByKey(Setting.Key.CHANNELS_PER_SEND).getInt();
            if (_next > DMX.MAX_CHANNELS) _next = 1;
        } else {
            int currentValue = -1;
            int queued = 0;
            for (int address = 1; address <= DMX.MAX_CHANNELS; address++) {
                if (queued >= _settingsStore.getByKey(Setting.Key.CHANNELS_PER_SEND).getInt()) break;
                if (_channelsPerSecondCounter.size() > _settingsStore.getByKey(Setting.Key.CHANNELS_PER_TIME).getInt()) break;
                int value = dmx.get(address);
                if ((!_lastSent.containsKey(address) || value != _lastSent.get(address)) && (currentValue == -1 || value == currentValue)) {
                    if (currentValue == -1) {
                        if (value < 10) data.append("0");
                        if (value < 100) data.append("0");
                        data.append(Double.toString(value).replace(".0", ""));
                    }
                    currentValue = value;
                    _lastSent.put(address, value);
                    if (address < 10) data.append("0");
                    if (address < 100) data.append("0");
                    data.append(Double.toString(address).replace(".0", ""));
                    queued++;
                    _channelsPerSecondCounter.add(System.currentTimeMillis());
                }
            }
            data.append(" ");

            for (long timestamp : new ArrayList<>(_channelsPerSecondCounter)) {
                if (System.currentTimeMillis() - timestamp > 250) {
                    _channelsPerSecondCounter.remove(timestamp);
                }
            }
        }

        if (data.length() <= 1) return null;
        if (_settingsStore.getByKey(Setting.Key.SHOW_DMX_SEND_DEBUG).isTrue())
            ConsolePanel.log("Preparing to send: " + data.toString());
        try {
            return data.toString().getBytes("UTF-8");
        } catch (Exception e) {
            ConsolePanel.log("Unsupported encoding");
            return data.toString().getBytes();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        try {
            serialPort.closePort();
        } catch (Exception e) {
            ConsolePanel.log(ConsolePanel.ConsoleColor.RED, e.getMessage());
            e.printStackTrace();
        }
        serialPort = null;
        ConsolePanel.log(ConsolePanel.ConsoleColor.YELLOW, "Port Disconnected");
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