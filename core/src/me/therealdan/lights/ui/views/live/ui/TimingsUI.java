package me.therealdan.lights.ui.views.live.ui;

import me.therealdan.lights.LightsCore;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

import java.util.LinkedHashMap;

public class TimingsUI implements UI {

    private static TimingsUI timingsUI;

    private LinkedHashMap<Integer, String> timings = new LinkedHashMap<>();

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

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Timings"));
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (int line : timings.keySet()) {
            Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), setWidth(renderer, timings.get(line)));
            drag(x, y, width, cellHeight);
            y -= cellHeight;
        }

        setHeightBasedOnY(y);
        return interacted;
    }

    public static void set(int line, String text) {
        timingsUI.timings.put(line, text);
    }
}