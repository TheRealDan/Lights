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

    public ButtonsUI() {
        setLocation(460, 500);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) Live.setSection(Live.Section.BUTTONS);
        boolean interacted = false;

        setWidth(ButtonsUI.WIDTH);

        float cellHeight = 30;

        float size = getWidth() / Buttons.PER_ROW;

        float x = getX();
        float y = getY();

        Util.box(renderer, x, y, getWidth(), cellHeight, LightsCore.DARK_BLUE, "Buttons");
        drag(x, y, getWidth(), cellHeight);
        y -= cellHeight;

        for (int position = 1; position <= Buttons.getTopPosition(); position++) {
            Button button = Buttons.getButton(position);
            if (button != null) {
                Util.box(renderer, x, y, size, size, button.getColor());
                renderer.queue(new Task(x, y - size / 2).text(button.getName(), Task.TextPosition.LEFT_CENTER).setColor(LightsCore.text()));
                if (Util.containsMouse(x, y, size, size) && canInteract()) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                        button.press();
                        Util.box(renderer, x, y, size, size, new Color(button.getColor()).mul(1.2f));
                    }
                }
            } else {
                Util.box(renderer, x, y, size, size, LightsCore.dark());
            }
            x += size;

            if (x + size > getX() + getWidth()) {
                x = getX();
                y -= size;
            }
        }

        setHeightBasedOnY(y);
        return interacted;
    }
}