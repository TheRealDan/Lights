package me.therealdan.lights.ui.views.live.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.dmx.DMX;
import me.therealdan.lights.programmer.Frame;
import me.therealdan.lights.programmer.Programmer;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.views.Live;
import me.therealdan.lights.util.Util;

public class SequenceProgrammerUI implements UI {

    private static Selected selected = Selected.NONE;

    private final long delay = 1000;

    private String frameMilliseconds = "";
    private long frameTimestamp = System.currentTimeMillis();

    private String fadeMilliseconds = "";
    private long fadeTimestamp = System.currentTimeMillis();

    public SequenceProgrammerUI() {
        setLocation(330, 200);
    }

    @Override
    public void keyDown(int keycode) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

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
                        Programmer.getSequence().getActiveFrame().setFrameTime(0);
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
                        Programmer.getSequence().getActiveFrame().setFadeTime(0);
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
    }

    private void update() {
        if (Programmer.getSequence() == null) return;

        if (System.currentTimeMillis() - frameTimestamp > delay && frameMilliseconds.length() > 0) {
            Programmer.getSequence().getActiveFrame().setFrameTime(Long.parseLong(frameMilliseconds));
            frameMilliseconds = "";
        }

        if (System.currentTimeMillis() - fadeTimestamp > delay && fadeMilliseconds.length() > 0) {
            Programmer.getSequence().getActiveFrame().setFadeTime(Long.parseLong(fadeMilliseconds));
            fadeMilliseconds = "";
        }
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        update();

        if (containsMouse()) Live.setSection(Live.Section.SEQUENCE_PROGRAMMER);
        boolean interacted = false;

        Sequence sequence = Programmer.getSequence();

        float cellHeight = 30;

        float x = getX();
        float y = Gdx.graphics.getHeight() - getY();
        float width = getWidth();

        Util.box(renderer, x, y, getWidth(), cellHeight, LightsCore.DARK_BLUE, setWidth(renderer, "Sequence Programmer"));
        y -= cellHeight;

        Util.box(renderer, x, y, getWidth(), cellHeight, selected.equals(Selected.NAME) ? LightsCore.DARK_RED : LightsCore.medium(), setWidth(renderer, sequence.getName()));
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                setSelected(Selected.NAME);
        }
        y -= cellHeight;

        x = getX();
        width = getWidth() / 2f;
        Util.box(renderer, x, y, width, cellHeight, selected.equals(Selected.FRAME) ? LightsCore.DARK_RED : LightsCore.medium(), setWidth(renderer, "Frame time: " + Frame.format(sequence.getActiveFrame().getFrameTime()), 2));
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                setSelected(Selected.FRAME);
        }
        x += width;
        Util.box(renderer, x, y, width, cellHeight, selected.equals(Selected.FADE) ? LightsCore.DARK_RED : LightsCore.medium(), setWidth(renderer, "Fade time: " + Frame.format(sequence.getActiveFrame().getFadeTime()), 2));
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
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
            Util.box(renderer, x, y, width, cellHeight, highlight ? LightsCore.DARK_RED : LightsCore.medium(), button.getName());
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                    button.press();
            }
            x += width;
        }
        y -= cellHeight;

        x = getX();
        width = getWidth();
        int index = 0;
        for (Frame frame : sequence.frames()) {
            Util.box(renderer, x, y, width, cellHeight, sequence.getCurrentFrame() == index ? LightsCore.DARK_RED : LightsCore.medium(), setWidth(renderer, frame.getInfo()));
            if (DMX.DRAW_DMX) frame.draw(renderer, x, y, width, cellHeight, sequence.getCurrentFrame() == index ? LightsCore.RED : LightsCore.light());
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                interacted = true;
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                    sequence.set(index);
            }
            y -= cellHeight;
            index++;
        }

        setHeight((Gdx.graphics.getHeight() - getY()) - y);
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
                    Programmer.getSequence().last();
                    break;

                case CLONE:
                    Programmer.getSequence().add(Programmer.getSequence().getActiveFrame().clone());
                    Programmer.getSequence().last();
                    break;

                case REMOVE:
                    Programmer.getSequence().delete(Programmer.getSequence().getCurrentFrame());
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
        SequenceProgrammerUI.selected = selected;
    }

    public static Selected getSelected() {
        return selected;
    }
}