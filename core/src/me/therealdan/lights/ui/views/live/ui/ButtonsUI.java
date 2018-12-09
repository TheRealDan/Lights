package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.controllers.Button;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.views.Buttons;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

public class ButtonsUI implements UI {

    public static float WIDTH = 800;
    public static float HEIGHT = 800;

    public ButtonsUI() {
        setLocation(1700, 20);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.BUTTONS);
        boolean interacted = false;

        setWidth(ButtonsUI.WIDTH);
        setHeight(ButtonsUI.HEIGHT);

        float cellHeight = 30;

        float buttonWidth = getWidth() / Buttons.ROWS;
        float buttonHeight = getHeight() / Buttons.COLUMNS;

        float x = getX();
        float y = Gdx.graphics.getHeight() - getY();

        Util.box(renderer, x, y, getWidth(), cellHeight, LightsCore.DARK_BLUE, "Buttons");
        y -= cellHeight;

        for (int position = 1; position <= Buttons.ROWS * Buttons.COLUMNS; position++) {
            Button button = Buttons.getButton(position);
            if (button != null) {
                Util.box(renderer, x, y, buttonWidth, buttonHeight, button.getColor());
                renderer.queue(new Task(x, y - buttonHeight / 2).text(button.getName(), Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));
                if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, buttonWidth, buttonHeight)) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                        button.press();
                        Util.box(renderer, x, y, buttonWidth, buttonHeight, new Color(button.getColor()).mul(1.2f));
                    }
                }
            } else {
                Util.box(renderer, x, y, buttonWidth, buttonHeight, LightsCore.dark());
            }
            x += buttonWidth;

            if (x + buttonWidth > getX() + getWidth()) {
                x = getX();
                y -= buttonHeight;
            }
        }

        return interacted;
    }
}