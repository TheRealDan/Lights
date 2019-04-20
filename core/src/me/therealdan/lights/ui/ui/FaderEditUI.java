package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.controllers.Fader;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.UIHandler;

import java.text.DecimalFormat;

public class FaderEditUI implements UI {

    private static FaderEditUI faderEditUI;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private Fader editing;
    private Section section;
    private Sequence scroll = null;

    public FaderEditUI() {
        faderEditUI = this;
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.FADER_EDIT);
        boolean interacted = false;
        boolean shift = Lights.keyboard.isShift();

        float x = getX();
        float y = getY();
        float uiWidth = getWidth();
        float width = 250;
        float cellHeight = 30;

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Fader Editor", Task.TextPosition.CENTER);
        drag(x, y, uiWidth, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, canEdit(Section.NAME) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Name: " + getFader().getName());
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit(Section.NAME);
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, canEdit(Section.SEQUENCE) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Sequence: " + getFader().getSequence().getName());
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit(Section.SEQUENCE);
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, "Red: " + getFader().getColor().r);
        renderer.box(x, y, getFader().getColor().r * width, cellHeight, canEdit(Section.RED) ? Lights.color.RED : getFader().getColor());
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit(Section.RED);
                float start = x + 20;
                getFader().getColor().r = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, "Green: " + getFader().getColor().g);
        renderer.box(x, y, getFader().getColor().g * width, cellHeight, canEdit(Section.GREEN) ? Lights.color.GREEN : getFader().getColor());
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit(Section.GREEN);
                float start = x + 20;
                getFader().getColor().g = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, "Blue: " + getFader().getColor().b);
        renderer.box(x, y, getFader().getColor().b * width, cellHeight, canEdit(Section.BLUE) ? Lights.color.BLUE : getFader().getColor());
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit(Section.BLUE);
                float start = x + 20;
                getFader().getColor().b = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, "Move");
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                FadersUI.move(getFader());
                return interacted;
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete");
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shift && Lights.mouse.leftReady(-1)) {
                FadersUI.remove(getFader());
                edit((Fader) null);
                return interacted;
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, "Close");
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit((Fader) null);
                return interacted;
            }
        }
        y -= cellHeight;

        setHeightBasedOnY(y);

        if (canEdit(Section.SEQUENCE)) {
            float sequencesWidth = renderer.getWidth("All Sequences") + 25;
            for (Sequence sequence : SequencesUI.sequences())
                sequencesWidth = Math.max(sequencesWidth, renderer.getWidth(sequence.getName()) + 25);

            x += width;
            y = getY();

            renderer.box(x, y, sequencesWidth, cellHeight, Lights.color.DARK_BLUE, "All Sequences", Task.TextPosition.CENTER);
            drag(x, y, sequencesWidth, cellHeight);
            y -= cellHeight;

            int i = 0;
            boolean display = false;
            for (Sequence sequence : SequencesUI.sequences(true)) {
                if (sequence.equals(getScroll())) display = true;
                if (display) {
                    renderer.box(x, y, sequencesWidth, cellHeight, sequence.equals(getFader().getSequence()) ? Lights.color.DARK_GREEN : Lights.color.MEDIUM, sequence.getName());
                    if (Lights.mouse.contains(x, y, sequencesWidth, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                            getFader().setSequence(sequence);
                        }
                    }
                    y -= cellHeight;
                    if (++i == 8) break;
                }
            }

            setWidth(width + sequencesWidth);
        } else {
            setWidth(width);
        }

        return interacted;
    }

    @Override
    public void scrolled(int amount) {
        if (!containsMouse()) return;

        if (canEdit(Section.SEQUENCE)) {
            if (amount > 0) {
                boolean next = false;
                int i = 0;
                for (Sequence sequence : SequencesUI.sequences(true)) {
                    if (i++ > SequencesUI.countSequences() - 8) return;
                    if (next) {
                        setScroll(sequence);
                        return;
                    }
                    if (sequence.equals(getScroll())) next = true;
                }
            } else {
                Sequence previous = null;
                for (Sequence sequence : SequencesUI.sequences(true)) {
                    if (sequence.equals(getScroll()) && previous != null) {
                        setScroll(previous);
                        return;
                    }
                    previous = sequence;
                }
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Lights.keyboard.isShift();

        if (canEdit(Section.NAME)) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getFader().getName().length() > 0)
                        getFader().rename(getFader().getName().substring(0, getFader().getName().length() - 1));
                    if (shift) getFader().rename("");
                    break;
                case Input.Keys.SPACE:
                    getFader().rename(getFader().getName() + " ");
                    break;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                        if (!shift) string = string.toLowerCase();
                        getFader().rename(getFader().getName() + string);
                    }
            }
        }

        return true;
    }

    @Override
    public boolean isVisible() {
        return isEditing();
    }

    @Override
    public boolean ignoreVisibilityUI() {
        return true;
    }

    private void setScroll(Sequence sequence) {
        this.scroll = sequence;
    }

    private Sequence getScroll() {
        if (scroll == null) setScroll(SequencesUI.sequences(true).get(0));
        return scroll;
    }

    private void edit(Section section) {
        this.section = section;
    }

    private boolean canEdit(Section section) {
        return section.equals(this.section);
    }

    public enum Section {
        NAME, SEQUENCE, RED, GREEN, BLUE;
    }

    public static void edit(Fader fader) {
        faderEditUI.editing = fader;
    }

    public static boolean isEditing() {
        return getFader() != null;
    }

    public static Fader getFader() {
        return faderEditUI.editing;
    }
}