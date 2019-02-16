package me.therealdan.lights.ui.views.live.ui;

import me.therealdan.lights.LightsCore;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class TimingsUI implements UI {

    private static TimingsUI timingsUI;

    private LinkedHashMap<String, String> timings = new LinkedHashMap<>();
    private HashMap<String, Long> max = new HashMap<>();
    private HashMap<String, Long> nonZero = new HashMap<>();
    private HashMap<String, Long> average = new HashMap<>();

    public TimingsUI() {
        timingsUI = this;
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        Live.setSection(Live.Section.TIMINGS);
        boolean interacted = false;

        float x = getX();
        float y = getY();
        float width = getWidth();
        float cellHeight = 30;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Timings"), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (String id : timings.keySet()) {
            Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), setWidth(renderer, timings.get(id)
                    .replace("%m", Long.toString(max.getOrDefault(id, 0L)))
                    .replace("%z", Long.toString(nonZero.getOrDefault(id, 0L)))
                    .replace("%a", Long.toString(average.getOrDefault(id, 0L)))
            ));
            drag(x, y, width, cellHeight);
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        return interacted;
    }

    public static void set(String id, String text, long value) {
        set(id, text);

        if (!timingsUI.max.containsKey(id)) timingsUI.max.put(id, 0L);
        if (value > timingsUI.max.get(id)) timingsUI.max.put(id, value);

        if (!timingsUI.nonZero.containsKey(id)) timingsUI.nonZero.put(id, 0L);
        if (value > 0) timingsUI.nonZero.put(id, value);

        if (!timingsUI.average.containsKey(id)) timingsUI.average.put(id, 0L);
        if (value > timingsUI.average.get(id) && value != timingsUI.max.get(id)) timingsUI.average.put(id, value);
    }

    public static void set(String id, String text) {
        timingsUI.timings.put(id, text);
    }

    public static void clear(String id) {
        timingsUI.timings.remove(id);
    }

    public static void clear() {
        timingsUI.timings.clear();
    }
}