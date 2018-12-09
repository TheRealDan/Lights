package me.therealdan.lights.ui.views.live.ui;

import me.therealdan.lights.renderer.Renderer;

public class UITemplate implements UI {

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        UI.super.draw(renderer, X, Y, WIDTH, HEIGHT);

        return true;
    }
}