package me.therealdan.lights.dmx;

import com.badlogic.gdx.graphics.Color;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.fixtures.Group;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.settings.Setting;
import me.therealdan.lights.ui.Live;
import me.therealdan.lights.ui.ui.ConsoleUI;
import me.therealdan.lights.ui.ui.PatchUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DMX {

    public static final float MAX_CHANNELS = 512;
    public static boolean DRAW_DMX = false;
    public static boolean LIMIT_LED_STRIPS = true;

    private static HashMap<String, DMX> dmx = new HashMap<>();

    private HashMap<Integer, Integer> channels = new HashMap<>();
    private HashMap<Integer, Integer> lastSent = new HashMap<>();

    private ArrayList<Long> channelsPerSecondCounter = new ArrayList<>();

    private String level;

    private int next = 1;

    private DMX(String level) {
        this.level = level;
    }

    public void flood(int value) {
        for (int channel = 1; channel <= MAX_CHANNELS; channel++)
            set(channel, value);
    }

    public void zero() {
        flood(0);
    }

    public void clear() {
        channels.clear();
    }

    public void copy(DMX dmx) {
        for (int channel = 1; channel <= MAX_CHANNELS; channel++)
            set(channel, dmx.get(channel));
    }

    public void pull(DMX dmx) {
        for (int channel : active())
            set(channel, dmx.get(channel));
    }

    public void set(int channel, int value) {
        channels.put(
                Math.min(Math.max(channel, 1), (int) MAX_CHANNELS),
                Math.min(Math.max(value, 0), 255)
        );
    }

    public int get(int channel) {
        float value = pget(channel);
        return (int) (value * Live.getMaster());
    }

    private int pget(int channel) {
        if (DMX.LIMIT_LED_STRIPS) {
            Group group = PatchUI.groupByName("LEDs");
            if (group != null) {
                for (Fixture fixture : group.fixtures()) {
                    for (int address : fixture.getAddresses()) {
                        if (address == channel) {
                            return Math.min(channels.getOrDefault(channel, 0), 20);
                        }
                    }
                }
            }
        }

        return channels.getOrDefault(channel, 0);
    }

    public List<Integer> active() {
        return new ArrayList<>(channels.keySet());
    }

    public byte[] getNext() {
        StringBuilder data = new StringBuilder();

        if (Setting.byName(Setting.Name.CONTINUOUS).isTrue()) {
            for (int address = next; address < next + Setting.byName(Setting.Name.CHANNELS_PER_SEND).getInt(); address++) {
                int value = get(address);
                if (value < 10) data.append("0");
                if (value < 100) data.append("0");
                data.append(Double.toString(value).replace(".0", ""));
                if (address < 10) data.append("0");
                if (address < 100) data.append("0");
                data.append(Double.toString(address).replace(".0", ""));
                data.append(" ");
            }

            next += Setting.byName(Setting.Name.CHANNELS_PER_SEND).getInt();
            if (next > MAX_CHANNELS) next = 1;
        } else {
            int currentValue = -1;
            int queued = 0;
            for (int address = 1; address <= MAX_CHANNELS; address++) {
                if (queued >= Setting.byName(Setting.Name.CHANNELS_PER_SEND).getInt()) break;
                if (channelsPerSecondCounter.size() > Setting.byName(Setting.Name.CHANNELS_PER_TIME).getInt()) break;
                int value = channels.get(address);
                if ((!lastSent.containsKey(address) || value != lastSent.get(address)) && (currentValue == -1 || value == currentValue)) {
                    if (currentValue == -1) {
                        if (value < 10) data.append("0");
                        if (value < 100) data.append("0");
                        data.append(Double.toString(value).replace(".0", ""));
                    }
                    currentValue = value;
                    lastSent.put(address, value);
                    if (address < 10) data.append("0");
                    if (address < 100) data.append("0");
                    data.append(Double.toString(address).replace(".0", ""));
                    queued++;
                    channelsPerSecondCounter.add(System.currentTimeMillis());
                }
            }
            data.append(" ");

            for (long timestamp : new ArrayList<>(channelsPerSecondCounter)) {
                if (System.currentTimeMillis() - timestamp > 250) {
                    channelsPerSecondCounter.remove(timestamp);
                }
            }
        }

        if (data.length() <= 1) return null;
        if (Setting.byName(Setting.Name.SHOW_DMX_SEND_DEBUG).isTrue()) ConsoleUI.log("Preparing to send: " + data.toString());
        try {
            return data.toString().getBytes("UTF-8");
        } catch (Exception e) {
            ConsoleUI.log("Unsupported encoding");
            return data.toString().getBytes();
        }
    }

    public String getLevel() {
        return level;
    }

    public static DMX get(String level) {
        if (!dmx.containsKey(level)) dmx.put(level, new DMX(level));
        return dmx.get(level);
    }

    public static List<String> levels() {
        return new ArrayList<>(dmx.keySet());
    }

    public static void draw(Renderer renderer, float x, float y, float width, float height, Color color, List<Integer> originalValues) {
        long timestamp = System.currentTimeMillis();
        int overflow = (int) (DMX.MAX_CHANNELS - width);
        List<Integer> values = new ArrayList<>();
        int previousValue = -1;
        for (int address = 1; address <= DMX.MAX_CHANNELS; address++) {
            int value = originalValues.get(address - 1);
            if (previousValue != -1 && overflow > 0) {
                if (previousValue == value) {
                    overflow--;
                    continue;
                }
            }
            values.add(value);
            previousValue = value;
        }

        for (int value : values) {
            renderer.queue(new Task(x, y - height).line(x, y - height + (height * value / 255f)).setColor(color));
            x++;
        }

        long timepassed = System.currentTimeMillis() - timestamp;
        if (timepassed > 1) System.out.println(timepassed + "ms");
    }
}