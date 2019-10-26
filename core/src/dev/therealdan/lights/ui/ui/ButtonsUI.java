package dev.therealdan.lights.ui.ui;

import com.badlogic.gdx.graphics.Color;
import dev.therealdan.lights.controllers.Button;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.UIHandler;

public class ButtonsUI implements UI {

    public static float WIDTH = 800;
    public static final int PER_ROW = 10;

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.BUTTONS);
        boolean interacted = false;

        setWidth(ButtonsUI.WIDTH);

        float cellHeight = 30;

        float size = getWidth() / ButtonsUI.PER_ROW;

        float x = getX();
        float y = getY();

        renderer.box(x, y, getWidth(), cellHeight, Lights.color.DARK_BLUE, "Buttons", Task.TextPosition.CENTER);
        drag(x, y, getWidth(), cellHeight);
        y -= cellHeight;

        ButtonEditUI buttonEditUI = (ButtonEditUI) UIHandler.byName("ButtonEdit");

        int topPositionToDisplay = Button.getTopPosition();
        if (buttonEditUI.isEditing()) {
            while (topPositionToDisplay % PER_ROW != 0) topPositionToDisplay++;
            topPositionToDisplay += PER_ROW;
        }

        for (int position = 1; position <= topPositionToDisplay; position++) {
            Button button = Button.byPosition(position);
            if (button != null) {
                renderer.box(x, y, size, size, button.getColor(), button.getName(), Task.TextPosition.CENTER);
                if (Lights.mouse.contains(x, y, size, size) && canInteract()) {
                    interacted = true;
                    if (Lights.mouse.leftClicked(1000)) {
                        button.press();
                        renderer.box(x, y, size, size, new Color(button.getColor()).mul(1.5f));
                    } else if (Lights.mouse.rightClicked()) {
                        buttonEditUI.edit(button);
                    }
                }
            } else {
                renderer.box(x, y, size, size, Lights.color.DARK);
                if (Lights.mouse.contains(x, y, size, size) && canInteract() && buttonEditUI.isEditing()) {
                    if (Lights.mouse.leftClicked()) {
                        buttonEditUI.getEditing().setPosition(position);
                    }
                }
            }
            x += size;

            if (x + size > getX() + getWidth()) {
                x = getX();
                y -= size;
            }
        }

        renderer.box(x, y, size, size, Lights.color.DARK, Lights.color.GREEN, "Add New", Task.TextPosition.CENTER);
        if (Lights.mouse.contains(x, y, size, size) && canInteract()) {
            interacted = true;
            if (Lights.mouse.leftClicked(1000)) {
                Button.add(new Button("New Button", Lights.color.MEDIUM));
            }
        }
        y -= size;

        setHeightBasedOnY(y);
        return interacted;
    }
}