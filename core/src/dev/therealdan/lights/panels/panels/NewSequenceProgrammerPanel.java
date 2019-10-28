package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.graphics.Color;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.programmer.Programmer;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.UIHandler;

public class NewSequenceProgrammerPanel implements Panel {

    public NewSequenceProgrammerPanel() {
        setWidth(800);
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.NEW_SEQUENCE_PROGRAMMER);
        boolean interacted = false;

        float optionsWidth = getOptionsWidth(renderer);
        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        renderer.box(x, y, getWidth(), getHeight(), Lights.color.DARK);
        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, getFriendlyName(), Task.TextPosition.CENTER);
        drag(x, y, width, cellHeight);
        y -= cellHeight;

        for (Option option : Option.values()) {
            if (Lights.mouse.contains(x, y, optionsWidth, cellHeight)) {
                if (Lights.mouse.leftClicked(500)) option.leftClick();
                if (Lights.mouse.rightClicked(500)) option.rightClick();
            }
            renderer.box(x, y, optionsWidth, cellHeight, option.getBackground(), option.getText(), option.getName(), Task.TextPosition.CENTER);
            y -= cellHeight;
        }
        setHeightBasedOnY(y);
        x += optionsWidth;
        y = getY() - cellHeight;
        width -= optionsWidth;

        // TODO - Text can be frame & fade time. Background color can be divided into sections based on virtual channels and colors.

        return interacted;
    }

    private float getOptionsWidth(Renderer renderer) {
        float optionsWidth = 0;
        for (Option option : Option.values())
            if (optionsWidth < renderer.getWidth(option.getName()))
                optionsWidth = renderer.getWidth(option.getName());
        return optionsWidth + 10;
    }

    public enum Option {
        NAME, PAUSE_PLAY, ENABLE_LOOP, ADD_FRAME, CLONE_FRAME, SAVE_SEQUENCE, DELETE_SELECTED, CLEAR_ALL;

        public void leftClick() {

        }

        public void rightClick() {

        }

        public String getName() {
            switch (this) {
                case NAME:
                    return "Name: " + Programmer.getSequence().getName();
                case PAUSE_PLAY:
                    return Programmer.getSequence().isPlaying() ? "Pause" : "Play";
                default:
                    StringBuilder stringBuilder = new StringBuilder();
                    boolean capitalize = true;
                    for (String letter : this.toString().split("")) {
                        if (capitalize) {
                            stringBuilder.append(letter.toUpperCase());
                            capitalize = false;
                        } else if (letter.equals("_")) {
                            stringBuilder.append(" ");
                            capitalize = true;
                        } else {
                            stringBuilder.append(letter.toLowerCase());
                        }
                    }
                    return stringBuilder.toString();
            }
        }

        public Color getBackground() {
            switch (this) {
                case NAME:
                    // TODO - Check if name is selected
                    return false ? Lights.color.DARK_RED : Lights.color.MEDIUM;
                case ENABLE_LOOP:
                    return Programmer.getSequence().doesLoop() ? Lights.color.DARK_GREEN : Lights.color.MEDIUM;
                default:
                    return Lights.color.MEDIUM;
            }
        }

        public Color getText() {
            switch (this) {
                default:
                    return Lights.color.TEXT;
                case ADD_FRAME:
                case CLONE_FRAME:
                    return Lights.color.YELLOW;
                case SAVE_SEQUENCE:
                    return Lights.color.GREEN;
                case DELETE_SELECTED:
                case CLEAR_ALL:
                    return Lights.color.RED;
            }
        }
    }
}