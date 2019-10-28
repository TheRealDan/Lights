package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import dev.therealdan.lights.dmx.DMX;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.programmer.Frame;
import dev.therealdan.lights.programmer.Programmer;
import dev.therealdan.lights.programmer.Sequence;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.ui.UIHandler;

import java.util.ArrayList;
import java.util.List;

public class SequenceProgrammerPanel implements Panel {

    private static Selected selected = Selected.NONE;

    private final long delay = 1000;

    private String frameMilliseconds = "";
    private long frameTimestamp = System.currentTimeMillis();

    private String fadeMilliseconds = "";
    private long fadeTimestamp = System.currentTimeMillis();

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Lights.keyboard.isShift();

        switch (getSelected()) {
            case NAME:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        if (Programmer.getSequence().getName().length() > 0)
                            Programmer.getSequence().rename(Programmer.getSequence().getName().substring(0, Programmer.getSequence().getName().length() - 1));
                        if (shift) Programmer.getSequence().rename("");
                        break;
                    case Input.Keys.SPACE:
                        Programmer.getSequence().rename(Programmer.getSequence().getName() + " ");
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                            if (!shift) string = string.toLowerCase();
                            Programmer.getSequence().rename(Programmer.getSequence().getName() + string);
                        }
                }
                break;

            case FRAME:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        for (Frame frame : Programmer.getSelectedFrames())
                            frame.setFrameTime(0);
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("1234567890".contains(string)) {
                            frameMilliseconds += string;
                            frameTimestamp = System.currentTimeMillis();
                        }
                }
                break;

            case FADE:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        for (Frame frame : Programmer.getSelectedFrames())
                            frame.setFadeTime(0);
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("1234567890".contains(string)) {
                            fadeMilliseconds += string;
                            fadeTimestamp = System.currentTimeMillis();
                        }
                }
                break;
        }

        return true;
    }

    private void update() {
        if (Programmer.getSequence() == null) return;

        if (System.currentTimeMillis() - frameTimestamp > delay && frameMilliseconds.length() > 0) {
            for (Frame frame : Programmer.getSelectedFrames())
                frame.setFrameTime(Long.parseLong(frameMilliseconds));
            frameMilliseconds = "";
        }

        if (System.currentTimeMillis() - fadeTimestamp > delay && fadeMilliseconds.length() > 0) {
            for (Frame frame : Programmer.getSelectedFrames())
                frame.setFadeTime(Long.parseLong(fadeMilliseconds));
            fadeMilliseconds = "";
        }
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        update();

        if (containsMouse()) UIHandler.setSection(UIHandler.Section.SEQUENCE_PROGRAMMER);
        boolean interacted = false;
        boolean shift = Lights.keyboard.isShift();

        Sequence sequence = Programmer.getSequence();
        Frame firstSelectedFrame = Programmer.getFirstSelectedFrame();

        float cellHeight = 30;

        float x = getX();
        float y = getY();
        float width = getWidth();

        renderer.box(x, y, getWidth(), cellHeight, Lights.color.DARK_BLUE, setWidth(renderer, getFriendlyName()), Task.TextPosition.CENTER);
        drag(x, y, getWidth(), cellHeight);
        y -= cellHeight;

        renderer.box(x, y, getWidth(), cellHeight, selected.equals(Selected.NAME) ? Lights.color.DARK_RED : Lights.color.MEDIUM, setWidth(renderer, sequence.getName()), Task.TextPosition.CENTER);
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                setSelected(Selected.NAME);
        }
        y -= cellHeight;

        x = getX();
        width = getWidth() / 2f;
        renderer.box(x, y, width, cellHeight, selected.equals(Selected.FRAME) ? Lights.color.DARK_RED : Lights.color.MEDIUM, setWidth(renderer,
                firstSelectedFrame != null ?
                        "Frame time: " + Frame.format(firstSelectedFrame.getFrameTime()) :
                        "N/A"
                , 2), Task.TextPosition.CENTER);
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                setSelected(Selected.FRAME);
        }
        x += width;
        renderer.box(x, y, width, cellHeight, selected.equals(Selected.FADE) ? Lights.color.DARK_RED : Lights.color.MEDIUM, setWidth(renderer,
                firstSelectedFrame != null ?
                        "Fade time: " + Frame.format(firstSelectedFrame.getFadeTime()) :
                        "N/A"
                , 2), Task.TextPosition.CENTER);
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                setSelected(Selected.FADE);
        }
        y -= cellHeight;

        x = getX();
        width = getWidth() / Button.values().length;
        for (Button button : Button.values()) {
            boolean highlight = false;
            if (button.equals(Button.LOOP)) highlight = sequence.doesLoop();
            setWidth((renderer.getWidth(button.getName()) + 10) * Button.values().length, true);
            renderer.box(x, y, width, cellHeight, highlight ? Lights.color.DARK_GREEN : Lights.color.MEDIUM, button.getName(), Task.TextPosition.CENTER);
            if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500))
                    button.press();
            }
            x += width;
        }
        y -= cellHeight;

        x = getX();
        width = getWidth();
        int index = 0;
        for (Frame frame : sequence.frames()) {
            Color color = Lights.color.MEDIUM;
            if (Programmer.isSelected(frame)) color = Lights.color.DARK_RED;
            if (sequence.getCurrentFrame() == index) {
                color = Lights.color.DARK_GREEN;
                if (Programmer.isSelected(frame)) color = Lights.color.DARK_YELLOW;
            }
            renderer.box(x, y, width, cellHeight, color, setWidth(renderer, frame.getInfo()));
            if (DMX.DRAW_DMX) {
                if (color == Lights.color.MEDIUM) color = Lights.color.LIGHT;
                frame.draw(renderer, x, y, width, cellHeight, color);
            }
            if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    if (shift) {
                        sequence.set(index);
                    } else {
                        if (Lights.mouse.leftReady(400)) {
                            if (Programmer.isSelected(frame)) {
                                Programmer.deselect(frame);
                            } else {
                                Programmer.select(frame);
                            }
                        }
                    }
                }
            }
            y -= cellHeight;
            index++;
        }

        setHeightBasedOnY(y);
        return interacted;
    }

    public enum Button {
        PLAY, STOP,
        LOOP,
        ADD, CLONE, REMOVE,
        SAVE, CLEAR;

        public void press() {
            switch (this) {
                case PLAY:
                    Programmer.getSequence().play();
                    break;

                case STOP:
                    Programmer.getSequence().stop();
                    break;

                case LOOP:
                    Programmer.getSequence().toggleLoop();
                    break;

                case ADD:
                    Programmer.getSequence().add(new Frame());
                    break;

                case CLONE:
                    List<Frame> newFrames = new ArrayList<>();
                    for (Frame frame : Programmer.getSelectedFrames()) {
                        Frame newFrame = frame.clone();
                        Programmer.getSequence().add(newFrame);
                        newFrames.add(newFrame);
                    }
                    Programmer.deselectAllFrames();
                    for (Frame frame : newFrames)
                        Programmer.select(frame);
                    break;

                case REMOVE:
                    for (Frame frame : Programmer.getSelectedFrames())
                        Programmer.getSequence().delete(frame);
                    Programmer.deselectAllFrames();
                    break;

                case SAVE:
                    Programmer.save();
                    break;

                case CLEAR:
                    Programmer.clear();
                    break;
            }
        }

        public String getName() {
            return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
        }
    }

    public enum Selected {
        NONE,
        NAME,
        FRAME, FADE;
    }

    public static void setSelected(Selected selected) {
        SequenceProgrammerPanel.selected = selected;
    }

    public static Selected getSelected() {
        return selected;
    }
}