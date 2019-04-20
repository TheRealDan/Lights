package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.controllers.Button;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.UIHandler;

import java.text.DecimalFormat;

public class ButtonEditUI implements UI {

    private static ButtonEditUI buttonEditUI;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private Button editing;
    private Section section;
    private Sequence scroll = null;

    public ButtonEditUI() {
        buttonEditUI = this;
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.BUTTON_EDIT);
        boolean interacted = false;
        boolean shift = Lights.keyboard.isShift();

        float x = getX();
        float y = getY();
        float uiWidth = getWidth();
        float width = 250;
        float cellHeight = 30;

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Button Editor", Task.TextPosition.CENTER);
        drag(x, y, uiWidth, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, canEdit(Section.NAME) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Name: " + getButton().getName());
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit(Section.NAME);
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, canEdit(Section.SEQUENCE) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Sequences: " + getButton().sequences().size());
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit(Section.SEQUENCE);
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, "Red: " + getButton().getColor().r);
        renderer.box(x, y, getButton().getColor().r * width, cellHeight, canEdit(Section.RED) ? Lights.color.RED : getButton().getColor());
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit(Section.RED);
                float start = x + 20;
                getButton().getColor().r = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, "Green: " + getButton().getColor().g);
        renderer.box(x, y, getButton().getColor().g * width, cellHeight, canEdit(Section.GREEN) ? Lights.color.GREEN : getButton().getColor());
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit(Section.GREEN);
                float start = x + 20;
                getButton().getColor().g = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, "Blue: " + getButton().getColor().b);
        renderer.box(x, y, getButton().getColor().b * width, cellHeight, canEdit(Section.BLUE) ? Lights.color.BLUE : getButton().getColor());
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit(Section.BLUE);
                float start = x + 20;
                getButton().getColor().b = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, "Move");
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                ButtonsUI.move(getButton());
                return interacted;
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete");
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shift && Lights.mouse.leftReady(-1)) {
                ButtonsUI.remove(getButton());
                edit((Button) null);
                return interacted;
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, "Close");
        if (Lights.mouse.contains(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(-1)) {
                edit((Button) null);
                return interacted;
            }
        }
        y -= cellHeight;

        setHeightBasedOnY(y);

        if (canEdit(Section.SEQUENCE)) {
            float sequencesWidth = renderer.getWidth("Selected Sequences") + 25;
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
                    renderer.box(x, y, sequencesWidth, cellHeight, getButton().contains(sequence) ? Lights.color.DARK_GREEN : Lights.color.MEDIUM, sequence.getName());
                    if (Lights.mouse.contains(x, y, sequencesWidth, cellHeight) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                            if (getButton().contains(sequence)) {
                                getButton().remove(sequence);
                            } else {
                                getButton().set(sequence, 1);
                            }
                        }
                    }
                    y -= cellHeight;
                    if (++i == 8) break;
                }
            }

            x += sequencesWidth;
            y = getY();

            renderer.box(x, y, sequencesWidth, cellHeight, Lights.color.DARK_BLUE, "Selected Sequences", Task.TextPosition.CENTER);
            drag(x, y, sequencesWidth, cellHeight);
            y -= cellHeight;

            for (Sequence sequence : getButton().sequences(true)) {
                renderer.box(x, y, sequencesWidth, cellHeight, Lights.color.MEDIUM, sequence.getName() + ": " + getButton().getPriority(sequence));
                if (Lights.mouse.contains(x, y, sequencesWidth, cellHeight) && canInteract()) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(500)) {
                        if (Lights.mouse.contains(x, y, sequencesWidth / 2, cellHeight)) {
                            getButton().set(sequence, getButton().getPriority(sequence) + 1);
                        } else {
                            getButton().set(sequence, Math.max(getButton().getPriority(sequence) - 1, 1));
                            if (shift) getButton().remove(sequence);
                        }
                    }
                }
                y -= cellHeight;
            }

            setWidth(width + sequencesWidth + sequencesWidth);
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
                    if (getButton().getName().length() > 0)
                        getButton().rename(getButton().getName().substring(0, getButton().getName().length() - 1));
                    if (shift) getButton().rename("");
                    break;
                case Input.Keys.SPACE:
                    getButton().rename(getButton().getName() + " ");
                    break;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                        if (!shift) string = string.toLowerCase();
                        getButton().rename(getButton().getName() + string);
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

    public static void edit(Button button) {
        buttonEditUI.editing = button;
    }

    public static boolean isEditing() {
        return getButton() != null;
    }

    public static Button getButton() {
        return buttonEditUI.editing;
    }
}