package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.controllers.Fader;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.programmer.Sequence;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.util.Util;

import java.text.DecimalFormat;

public class FaderEditorPanel implements Panel {

    private static FaderEditorPanel faderEditor;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private Fader editing;
    private Section section;
    private Sequence scroll = null;

    public FaderEditorPanel() {
        faderEditor = this;

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
        float cellHeight = 30;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().DARK_BLUE, getFriendlyName(), Task.TextPosition.CENTER);
        drag(mouse, x, y, uiWidth, cellHeight);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, canEdit(Section.NAME) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, "Name: " + getFader().getName());
        if (mouse.within(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit(Section.NAME);
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, canEdit(Section.SEQUENCE) ? renderer.getTheme().DARK_RED : renderer.getTheme().MEDIUM, "Sequence: " + getFader().getSequence().getName());
        if (mouse.within(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit(Section.SEQUENCE);
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, "Red: " + getFader().getColor().r);
        renderer.box(x, y, getFader().getColor().r * width, cellHeight, canEdit(Section.RED) ? renderer.getTheme().RED : getFader().getColor());
        if (mouse.within(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit(Section.RED);
                float start = x + 20;
                getFader().getColor().r = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, "Green: " + getFader().getColor().g);
        renderer.box(x, y, getFader().getColor().g * width, cellHeight, canEdit(Section.GREEN) ? renderer.getTheme().GREEN : getFader().getColor());
        if (mouse.within(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit(Section.GREEN);
                float start = x + 20;
                getFader().getColor().g = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, "Blue: " + getFader().getColor().b);
        renderer.box(x, y, getFader().getColor().b * width, cellHeight, canEdit(Section.BLUE) ? renderer.getTheme().BLUE : getFader().getColor());
        if (mouse.within(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit(Section.BLUE);
                float start = x + 20;
                getFader().getColor().b = Float.parseFloat(decimalFormat.format(Math.min(Math.max((Gdx.input.getX() - start) / ((x + width - 20) - start), 0), 1)));
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, "Move");
        if (mouse.within(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                FadersPanel.move(getFader());
                return interacted;
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, renderer.getTheme().RED, "Delete");
        if (mouse.within(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shift && mouse.leftReady(-1)) {
                FadersPanel.remove(getFader());
                edit((Fader) null);
                return interacted;
            }
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, renderer.getTheme().MEDIUM, "Close");
        if (mouse.within(x, y, width, cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && mouse.leftReady(-1)) {
                edit((Fader) null);
                return interacted;
            }
        }
        y -= cellHeight;

        setHeightBasedOnY(y);

        if (canEdit(Section.SEQUENCE)) {
            float sequencesWidth = renderer.getWidth("All Sequences") + 25;
            for (Sequence sequence : SequencesPanel.sequences())
                sequencesWidth = Math.max(sequencesWidth, renderer.getWidth(sequence.getName()) + 25);

            x += width;
            y = getY();

            renderer.box(x, y, sequencesWidth, cellHeight, renderer.getTheme().DARK_BLUE, "All Sequences", Task.TextPosition.CENTER);
            drag(mouse, x, y, sequencesWidth, cellHeight);
            y -= cellHeight;

            int i = 0;
            boolean display = false;
            for (Sequence sequence : SequencesPanel.sequences(true)) {
                if (sequence.equals(getScroll())) display = true;
                if (display) {
                    renderer.box(x, y, sequencesWidth, cellHeight, sequence.equals(getFader().getSequence()) ? renderer.getTheme().DARK_GREEN : renderer.getTheme().MEDIUM, sequence.getName());
                    if (mouse.within(x, y, sequencesWidth, cellHeight) && canInteract()) {
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
        if (scroll == null) setScroll(SequencesPanel.sequences(true).get(0));
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
        faderEditor.editing = fader;
    }

    public static boolean isEditing() {
        return getFader() != null;
    }

    public static Fader getFader() {
        return faderEditor.editing;
    }
}