package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.util.Util;

public class DMXOutput implements Tab {

    @Override
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        float cellSize = 30;

        float x = LightsCore.edge();
        float y = Gdx.graphics.getHeight() - Y - LightsCore.edge();

        float width = WIDTH - LightsCore.edge() * 2f;

        y = outputCells(renderer, x, y, width, cellSize);
        y -= cellSize;
        y -= cellSize;

        outputActive(renderer, x, y, 150, cellSize);
    }

    private void outputActive(Renderer renderer, float x, float Y, float width, float cellHeight) {
        DMX dmx = DMX.get("VISUALISER");

        float y = Y;
        for (int address : dmx.active()) {
            if (dmx.get(address) > 0) {
                Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), "Address " + address + " - " + dmx.get(address));
                y -= cellHeight;
            }

            if (y - cellHeight - LightsCore.edge() <= 0) {
                y = Y;
                x += width + LightsCore.edge();
            }
        }
    }

    private float outputCells(Renderer renderer, float X, float y, float width, float cellSize) {
        DMX dmx = DMX.get("VISUALISER");

        float x = X;
        for (int address : dmx.active()) {
            Util.box(renderer, x, y, cellSize, cellSize, LightsCore.medium(), Integer.toString(address));
            float fill = cellSize * dmx.get(address) / 255f;
            Util.box(renderer, x, y - cellSize + fill, cellSize, fill, LightsCore.DARK_RED);
            x += cellSize;

            if (x > width) {
                x = X;
                y -= cellSize;
            }
        }

        return y;
    }
}