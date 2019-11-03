package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.graphics.Color;
import dev.therealdan.lights.controllers.Button;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.PanelHandler;

public class ButtonsPanel implements Panel {

    public static float WIDTH = 800;
    public static final int PER_ROW = 10;

    public ButtonsPanel() {
        register(new CloseIcon());
    }

    @Override
    public boolean drawContent(Renderer renderer, float x, float y, float width, float height) {
        if (containsMouse()) PanelHandler.setSection(PanelHandler.Section.BUTTONS);
        boolean interacted = false;

        setWidth(ButtonsPanel.WIDTH);

        float size = getWidth() / ButtonsPanel.PER_ROW;

        ButtonEditorPanel buttonEditor = (ButtonEditorPanel) PanelHandler.byName("ButtonEditor");

        int topPositionToDisplay = Button.getTopPosition();
        if (buttonEditor.isEditing()) {
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
                        buttonEditor.edit(button);
                    }
                }
            } else {
                renderer.box(x, y, size, size, Lights.color.DARK);
                if (Lights.mouse.contains(x, y, size, size) && canInteract() && buttonEditor.isEditing()) {
                    if (Lights.mouse.leftClicked()) {
                        buttonEditor.getEditing().setPosition(position);
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