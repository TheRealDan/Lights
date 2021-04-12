package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.graphics.Color;
import dev.therealdan.lights.controllers.Button;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.main.Theme;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.AddButtonIcon;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.PanelHandler;

public class ButtonsPanel implements Panel {

    private int buttonsPerRow = 8;

    public ButtonsPanel(Theme theme) {
        register(new CloseIcon());
        register(new AddButtonIcon(theme.MEDIUM));

        setWidth(800);
        setHeight(200);
    }

    @Override
    public boolean drawContent(Renderer renderer, float x, float y, float width, float height, boolean interacted) {
        ButtonEditorPanel buttonEditor = (ButtonEditorPanel) PanelHandler.byName("ButtonEditor");

        int topPositionToDisplay = Button.getTopPosition();
        while (topPositionToDisplay % getButtonsPerRow() != 0) topPositionToDisplay++;
        if (buttonEditor.isEditing()) topPositionToDisplay += getButtonsPerRow();

        float buttonWidth = width / getButtonsPerRow();
        float buttonHeight = height / (topPositionToDisplay / getButtonsPerRow());

        for (int position = 1; position <= topPositionToDisplay; position++) {
            Button button = Button.byPosition(position);
            if (button != null) {
                renderer.box(x, y, buttonWidth, buttonHeight, button.getColor(), button.getName(), Task.TextPosition.CENTER);
                if (Lights.mouse.contains(x, y, buttonWidth, buttonHeight) && canInteract()) {
                    interacted = true;
                    if (Lights.mouse.leftClicked(1000)) {
                        button.press();
                        renderer.box(x, y, buttonWidth, buttonHeight, new Color(button.getColor()).mul(1.5f));
                    } else if (Lights.mouse.rightClicked()) {
                        buttonEditor.edit(button);
                    }
                }
            } else {
                renderer.box(x, y, buttonWidth, buttonHeight, renderer.getTheme().DARK);
                if (Lights.mouse.contains(x, y, buttonWidth, buttonHeight) && canInteract() && buttonEditor.isEditing()) {
                    if (Lights.mouse.leftClicked()) {
                        buttonEditor.getEditing().setPosition(position);
                    }
                }
            }
            x += buttonWidth;

            if (x + buttonWidth > getX() + width) {
                x = getX();
                y -= buttonHeight;
            }
        }
        return interacted;
    }

    @Override
    public boolean isResizeable() {
        return true;
    }

    public void setButtonsPerRow(int buttonsPerRow) {
        this.buttonsPerRow = buttonsPerRow;
    }

    public int getButtonsPerRow() {
        return buttonsPerRow;
    }
}