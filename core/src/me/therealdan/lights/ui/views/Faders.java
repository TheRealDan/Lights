package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.controllers.Fader;
import me.therealdan.lights.controllers.FaderBank;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Faders implements Tab {

    private static Faders faders;

    private LinkedHashMap<Integer, Fader> faderMap = new LinkedHashMap<>();
    private LinkedHashMap<Integer, FaderBank> bankMap = new LinkedHashMap<>();

    private Section section = Section.NONE;
    private Edit edit = Edit.NONE;
    private FaderBank faderBank = null;
    private Fader fader = null;

    public Faders() {
        faders = this;

        Fader stage = new Fader(1, "Stage", LightsCore.ORANGE);
        stage.setSequence(Sequences.byName("Stage"));
        add(stage);

        Fader house = new Fader(2, "House", LightsCore.ORANGE);
        house.setSequence(Sequences.byName("House"));
        add(house);

        Fader sideCans = new Fader(3, "Side Cans", LightsCore.DARK_BLUE);
        sideCans.setSequence(Sequences.byName("Side Cans"));
        add(sideCans);

        Fader stageCans = new Fader(4, "Stage Cans", LightsCore.WHITE);
        stageCans.setType(Fader.Type.AMBIENT);
        stageCans.setSequence(Sequences.byName("Stage Cans"));
        add(stageCans);

        getBank(1).add(stage, house, sideCans, stageCans);
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        switch (keycode) {
            case Input.Keys.ESCAPE:
                setFader(null);
                setBank(null);
                break;
        }

        if (getFader() != null) {
            switch (getEdit()) {
                case FADER_NAME:
                    switch (keycode) {
                        case Input.Keys.BACKSPACE:
                            if (shift) getFader().rename("");
                            if (getFader().getName().length() > 0) getFader().rename(getFader().getName().substring(0, getFader().getName().length() - 1));
                            break;
                        case Input.Keys.SPACE:
                            getFader().rename(getFader().getName() + " ");
                            break;
                        default:
                            String string = Input.Keys.toString(keycode);
                            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ123456790".contains(string)) {
                                string = string.toLowerCase();
                                if (shift) string = string.toUpperCase();
                                getFader().rename(getFader().getName() + string);
                            }
                    }
                    break;
            }
        }

        return true;
    }

    @Override
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        float cellHeight = 30;

        float x = X + LightsCore.edge();
        float y = HEIGHT - Y - LightsCore.edge();

        float width = (WIDTH - (LightsCore.edge() * 5f)) / 4;

        banks(renderer, x, y, width, cellHeight);
        faders(renderer, x + width + LightsCore.edge(), y, width, cellHeight);
        fader(renderer, x + width * 2f + LightsCore.edge() * 2f, y, width, cellHeight);
        sequences(renderer, x + width * 3f + LightsCore.edge() * 3f, y, width, cellHeight);
    }

    private void banks(Renderer renderer, float x, float y, float width, float cellHeight) {
        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Banks");
        y -= cellHeight;

        for (FaderBank bank : banks()) {
            Util.box(renderer, x, y, width, cellHeight, bank.equals(getBank()) ? LightsCore.DARK_RED : LightsCore.medium(), "Bank " + bank.getID());
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                setSection(Section.FADER_BANKS);
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                    setBank(bank);
            }
            y -= cellHeight;
        }

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Create New Bank");
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                getBank(banks().size() + 1);
            }
        }
    }

    private void faders(Renderer renderer, float x, float y, float width, float cellHeight) {
        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Faders");
        y -= cellHeight;

        for (Fader fader : faders()) {
            boolean highlight = fader.equals(getFader());
            if (getBank() != null) highlight = getBank().contains(fader);
            Util.box(renderer, x, y, width, cellHeight, highlight ? LightsCore.DARK_RED : LightsCore.medium(), fader.getName());
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                setSection(Section.FADERS);
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    if (getBank() == null) {
                        setFader(fader);
                    } else if (LightsCore.actionReady(500)) {
                        if (getBank().contains(fader)) {
                            getBank().remove(fader);
                        } else {
                            getBank().add(fader);
                        }
                    }
                }
            }
            y -= cellHeight;
        }

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Create New Fader");
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                add(new Fader(Fader.getFreeID()));
            }
        }
    }

    private void fader(Renderer renderer, float x, float y, float width, float cellHeight) {
        Fader fader = getFader();
        if (fader == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Fader");
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, getEdit().equals(Edit.FADER_NAME) ? LightsCore.DARK_RED : LightsCore.medium(), "Name: " + fader.getName());
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                setEdit(Edit.FADER_NAME);
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), "Type: " + fader.getType().getName());
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                fader.toggleType();
            }
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), "Description: " + fader.getType().getDescription());
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.medium(), "Current Value: " + fader.getValue());
        y -= cellHeight;
    }

    private void sequences(Renderer renderer, float x, float y, float width, float cellHeight) {
        Fader fader = getFader();
        if (fader == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Sequences");
        y -= cellHeight;

        for (Sequence sequence : Sequences.sequences()) {
            Util.box(renderer, x, y, width, cellHeight, fader.getSequence() != null && fader.getSequence().equals(sequence) ? LightsCore.DARK_RED : LightsCore.medium(), sequence.getName());
            if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                setSection(Section.SEQUENCES);
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500)) {
                    fader.setSequence(sequence);
                }
            }
            y -= cellHeight;
        }
    }

    public void add(Fader fader) {
        faderMap.put(fader.getID(), fader);
    }

    public void setBank(FaderBank faderBank) {
        this.faderBank = faderBank;
        this.fader = null;
    }

    public void setFader(Fader fader) {
        this.fader = fader;
    }

    public FaderBank getBank() {
        return faderBank;
    }

    public Fader getFader() {
        return fader;
    }

    public void setEdit(Edit edit) {
        this.edit = edit;
    }

    public Edit getEdit() {
        return edit;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Section getSection() {
        return section;
    }

    public enum Section {
        NONE,

        FADERS,

        FADER,
        SEQUENCES,

        FADER_BANKS,
    }

    public enum Edit {
        NONE,

        FADER_NAME;
    }

    public static FaderBank getBank(int bank) {
        if (!faders.bankMap.containsKey(bank)) faders.bankMap.put(bank, new FaderBank(bank));
        return faders.bankMap.get(bank);
    }

    public static List<Fader> faders() {
        return new ArrayList<>(faders.faderMap.values());
    }

    public static List<FaderBank> banks() {
        return new ArrayList<>(faders.bankMap.values());
    }
}