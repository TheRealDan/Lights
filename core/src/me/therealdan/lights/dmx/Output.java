package me.therealdan.lights.dmx;

import com.fazecast.jSerialComm.SerialPort;
import me.therealdan.lights.ui.views.Console;

import java.util.ArrayList;
import java.util.List;

public class Output {

    private static Output output;

    private static boolean FROZEN = false;

    private Thread thread;

    private List<String> openPorts = new ArrayList<>();
    private SerialPort serialPort = null;
    private String activePort = "Not Connected";
    private boolean connected = false;

    public static long INTERVAL = 100; // milliseconds between data send
    public static long CONNECTION_WAIT = 2000; // milliseconds since last connection before data is allowed to send
    public static int BAUDRATE = 74880;
    public static int NEW_READ_TIMEOUT = 0;
    public static int NEW_WRITE_TIMEOUT = 0;
    public static int CHANNELS_PER_SEND = 512;
    public static boolean SHOW_DMX_SEND_DEBUG = true;
    public static boolean CONTINUOUS = false;

    private long lastConnect = System.currentTimeMillis();
    private long lastSend = System.currentTimeMillis();

    public Output() {
        output = this;

        thread = new Thread("Output") {
            @Override
            public void run() {
                while (true)
                    tick();
            }
        };
        thread.start();
    }

    private void tick() {
        if (System.currentTimeMillis() - lastSend < INTERVAL) return;
        lastSend = System.currentTimeMillis();

        if (this.serialPort != null && !this.serialPort.getSystemPortName().equals(activePort)) {
            this.serialPort = null;
            if (SHOW_DMX_SEND_DEBUG) Console.log(Console.ConsoleColor.YELLOW, "Port dropped");
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
                    this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, NEW_READ_TIMEOUT, NEW_WRITE_TIMEOUT);
                    this.lastConnect = System.currentTimeMillis();
                    if (SHOW_DMX_SEND_DEBUG) Console.log(Console.ConsoleColor.YELLOW, "Port Connected");
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
            if (SHOW_DMX_SEND_DEBUG) Console.log(Console.ConsoleColor.YELLOW, "Port Opened");
        }

        if (System.currentTimeMillis() - lastConnect < CONNECTION_WAIT) return;
        if (isFrozen()) return;

        try {
            byte[] bytes = DMX.get("OUTPUT").getNext();
            if (bytes == null) return;
            serialPort.getOutputStream().write(bytes);
            serialPort.getOutputStream().flush();
            if (SHOW_DMX_SEND_DEBUG) Console.log(Console.ConsoleColor.YELLOW, "DMX Sent");
        } catch (Exception e) {
            serialPort = null;
            Console.log(Console.ConsoleColor.RED, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void freeze() {
        FROZEN = true;
    }

    public static void unfreeze() {
        FROZEN = false;
    }

    public static void toggleFreeze() {
        FROZEN = !FROZEN;
        Console.log(Console.ConsoleColor.CYAN, Output.isFrozen() ?
                "DMX Output frozen." :
                "DMX Output unfrozen."
        );
    }

    public static boolean isFrozen() {
        return FROZEN;
    }

    public static boolean isConnected() {
        return output.connected;
    }

    public static void disconnect() {
        try {
            output.serialPort.closePort();
        } catch (Exception e) {
            Console.log(Console.ConsoleColor.RED, e.getMessage());
            e.printStackTrace();
        }
        output.serialPort = null;
        Console.log(Console.ConsoleColor.YELLOW, "Port Disconnected");
        output.activePort = "Not Connected";
        output.connected = false;
    }

    public static void setActivePort(String port) {
        output.activePort = port;
    }

    public static String getActivePort() {
        return output.activePort;
    }

    public static List<String> openPorts() {
        return new ArrayList<>(output.openPorts);
    }
}