package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.controllers.Button;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.programmer.Sequence;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.PanelHandler;
import dev.therealdan.lights.util.Util;

import java.text.DecimalFormat;

public class ButtonEditorPanel implements Panel {

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private Button editing;
    private Section section;
    private Sequence scroll = null;

    public ButtonEditorPanel() {
        register(new CloseIcon());
    }

    @Override
    public boolean draw(Mouse mouse, Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        boolean interacted = false;
        boolean shift = Util.isShiftHeld();

        float x = getX();
        float y = getY();
        float uiWidth = getWidth();
        float width = 250;

        renderer.box(x, y, width, Panel.CELL_HEIGHT, renderer.getTheme().DARK_BLUE, getFriendlyName(), Task.TextPosition.CENTER);
        drag(mouse, x, y, uiWidth, Panel.CELL_HEIGHT);
        y -= Panel.CELL_HEIGHT;

        renderer.box(x, y, width, Panel.CELL_HEIGHT, canEdit(Section.NAME) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, "Name: " + getEditing().getName());
        if (mouse.within(x, y, width, Panel.CELL_HEIGHT) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit(Section.NAME);
            }
        }
        y -= Panel.CELL_HEIGHT;

        renderer.box(x, y, width, Panel.CELL_HEIGHT, canEdit(Section.SEQUENCE) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, "Sequences: " + getEditing().sequences().size());
        if (mouse.within(x, y, width, Panel.CELL_HEIGHT) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit(Section.SEQUENCE);
            }
        }
        y -= Panel.CELL_HEIGHT;

        renderer.box(x, y, width, Panel.CELL_HEIGHT, renderer.getTheme().MEDIUM, "Red: " + getEditing().getColor().r);
        renderer.box(x, y, getEditing().getColor().r * width, Panel.CELL_HEIGHT, canEdit(Section.RED) ? renderer.getTheme().RED : getEditing().getColor());
        if (mouse.within(x, y, width, Panel.CELL_HEIGHT) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit(Section.RED);
                float start = x + 20;
                getEditing().getColor().r = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= Panel.CELL_HEIGHT;

        renderer.box(x, y, width, Panel.CELL_HEIGHT, renderer.getTheme().MEDIUM, "Green: " + getEditing().getColor().g);
        renderer.box(x, y, getEditing().getColor().g * width, Panel.CELL_HEIGHT, canEdit(Section.GREEN) ? renderer.getTheme().GREEN : getEditing().getColor());
        if (mouse.within(x, y, width, Panel.CELL_HEIGHT) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit(Section.GREEN);
                float start = x + 20;
                getEditing().getColor().g = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= Panel.CELL_HEIGHT;

        renderer.box(x, y, width, Panel.CELL_HEIGHT, renderer.getTheme().MEDIUM, "Blue: " + getEditing().getColor().b);
        renderer.box(x, y, getEditing().getColor().b * width, Panel.CELL_HEIGHT, canEdit(Section.BLUE) ? renderer.getTheme().BLUE : getEditing().getColor());
        if (mouse.within(x, y, width, Panel.CELL_HEIGHT) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit(Section.BLUE);
                float start = x + 20;
                getEditing().getColor().b = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= Panel.CELL_HEIGHT;

        renderer.box(x, y, width, Panel.CELL_HEIGHT, renderer.getTheme().MEDIUM, renderer.getTheme().RED, "Delete");
        if (mouse.within(x, y, width, Panel.CELL_HEIGHT) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shift && mouse.leftReady(-1)) {
                Button.remove(getEditing());
                edit((Button) null);
                return interacted;
            }
        }
        y -= Panel.CELL_HEIGHT;

        renderer.box(x, y, width, Panel.CELL_HEIGHT, renderer.getTheme().MEDIUM, "Close");
        if (mouse.within(x, y, width, Panel.CELL_HEIGHT) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit((Button) null);
                return interacted;
            }
        }
        y -= Panel.CELL_HEIGHT;

        setHeightBasedOnY(y);

        if (canEdit(Section.SEQUENCE)) {
            float sequencesWidth = renderer.getWidth("Selected Sequences") + 25;
            for (Sequence sequence : SequencesPanel.sequences())
                sequencesWidth = Math.max(sequencesWidth, renderer.getWidth(sequence.getName()) + 25);

            x += width;
            y = getY();

            renderer.box(x, y, sequencesWidth, Panel.CELL_HEIGHT, renderer.getTheme().DARK_BLUE, "All Sequences", Task.TextPosition.CENTER);
            drag(mouse, x, y, sequencesWidth, Panel.CELL_HEIGHT);
            y -= Panel.CELL_HEIGHT;

            int i = 0;
            boolean display = false;
            for (Sequence sequence : SequencesPanel.sequences(true)) {
                if (sequence.equals(getScroll())) display = true;
                if (display) {
                    renderer.box(x, y, sequencesWidth, Panel.CELL_HEIGHT, getEditing().contains(sequence) ? renderer.getTheme().DARK_GREEN : renderer.getTheme().MEDIUM, sequence.getName());
                    if (mouse.within(x, y, sequencesWidth, Panel.CELL_HEIGHT) && canInteract()) {
                        interacted = true;
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(500)) {
                            if (getEditing().contains(sequence)) {
                                getEditing().remove(sequence);
                            } else {
                                getEditing().set(sequence, 1);
                            }
                        }
                    }
                    y -= Panel.CELL_HEIGHT;
                    if (++i == 8) break;
                }
            }

            x += sequencesWidth;
            y = getY();

            renderer.box(x, y, sequencesWidth, Panel.CELL_HEIGHT, renderer.getTheme().DARK_BLUE, "Selected Sequences", Task.TextPosition.CENTER);
            drag(mouse, x, y, sequencesWidth, Panel.CELL_HEIGHT);
            y -= Panel.CELL_HEIGHT;

            for (Sequence sequence : getEditing().sequences(true)) {
                renderer.box(x, y, sequencesWidth, Panel.CELL_HEIGHT, renderer.getTheme().MEDIUM, sequence.getName() + ": " + getEditing().getPriority(sequence));
                if (mouse.within(x, y, sequencesWidth, Panel.CELL_HEIGHT) && canInteract()) {
                    interacted = true;
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(500)) {
                        if (mouse.within(x, y, sequencesWidth / 2, Panel.CELL_HEIGHT)) {
                            getEditing().set(sequence, getEditing().getPriority(sequence) + 1);
                        } else {
                            getEditing().set(sequence, Math.max(getEditing().getPriority(sequence) - 1, 1));
                            if (shift) getEditing().remove(sequence);
                        }
                    }
                }
                y -= Panel.CELL_HEIGHT;
            }

            setWidth(width + sequencesWidth + sequencesWidth);
        } else {
            setWidth(width);
        }

        return interacted;
    }

    @Override
    public void scrolled(Mouse mouse, int amount) {
        if (!mouse.within(this)) return;

        if (canEdit(Section.SEQUENCE)) {
            if (amount > 0) {
                boolean next = false;
                int i = 0;
                for (Sequence sequence : SequencesPanel.sequences(true)) {
                    if (i++ > SequencesPanel.countSequences() - 8) return;
                    if (next) {
                        setScroll(sequence);
                        return;
                    }
                    if (sequence.equals(getScroll())) next = true;
                }
            } else {
                Sequence previous = null;
                for (Sequence sequence : SequencesPanel.sequences(true)) {
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
        boolean shift = Util.isShiftHeld();

        if (canEdit(Section.NAME)) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getEditing().getName().length() > 0)
                        getEditing().rename(getEditing().getName().substring(0, getEditing().getName().length() - 1));
                    if (shift) getEditing().rename("");
                    break;
                case Input.Keys.SPACE:
                    getEditing().rename(getEditing().getName() + " ");
                    break;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                        if (!shift) string = string.toLowerCase();
                        getEditing().rename(getEditing().getName() + string);
                    }
            }
        }

        return true;
    }

    @Override
    public boolean isVisible() {
        return PanelHandler.byName("Buttons").isVisible() && isEditing();
    }

    @Override
    public boolean ignoreVisibilityUI() {
        return true;
    }

    private void setScroll(Sequence sequence) {
        this.scroll = sequence;
    }

    private Sequence getScroll() {
        if (scroll == null) setScroll(SequencesPanel.sequences(true).get(0));
        return scroll;
    }

    public void edit(Button button) {
        editing = button;
    }

    public boolean isEditing() {
        return getEditing() != null;
    }

    public Button getEditing() {
        return editing;
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
}