package dev.therealdan.lights.panels.panels;

import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class TimingsPanel implements Panel {

    private static TimingsPanel timingsUI;

    private LinkedHashMap<String, String> timings = new LinkedHashMap<>();
    private HashMap<String, Long> max = new HashMap<>();
    private HashMap<String, Long> nonZero = new HashMap<>();
    private HashMap<String, Long> average = new HashMap<>();

    public TimingsPanel() {
        timingsUI = this;
        register(new CloseIcon());
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        boolean interacted = false;

        float x = getX();
        float y = getY();
        float width = getWidth();
        float cellHeight = 30;

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, setWidth(renderer, getFriendlyName()), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (String id : timings.keySet()) {
            renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, setWidth(renderer, timings.get(id)
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