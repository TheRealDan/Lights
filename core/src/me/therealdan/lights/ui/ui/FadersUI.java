package me.therealdan.lights.ui.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import me.therealdan.lights.Lights;
import me.therealdan.lights.controllers.Fader;
import me.therealdan.lights.controllers.FaderBank;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;
import me.therealdan.lights.ui.UIHandler;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FadersUI implements UI {

    private static FadersUI fadersUI;

    private static float HEIGHT = 250;

    private LinkedHashMap<Integer, Fader> faderMap = new LinkedHashMap<>();
    private LinkedHashMap<Integer, FaderBank> bankMap = new LinkedHashMap<>();

    private Fader faderToMove = null;
    private int bank = 1;

    public FadersUI() {
        fadersUI = this;

        FileHandle fileHandle = Gdx.files.local("Lights/Faders/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle child : fileHandle.list())
                loadFader(child);

        fileHandle = Gdx.files.local("Lights/FaderBanks/");
        if (fileHandle.exists() && fileHandle.isDirectory())
            for (FileHandle child : fileHandle.list())
                loadBank(child);
    }

    private void loadFader(FileHandle fileHandle) {
        Fader fader = new Fader(-1);

        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("ID: ")) {
                fader = new Fader(Integer.parseInt(line.split(": ")[1]));
            } else if (line.startsWith("Name: ")) {
                fader.rename(line.split(": ")[1]);
            } else if (line.startsWith("Type: ")) {
                fader.setType(Fader.Type.valueOf(line.split(": ")[1]));
            } else if (line.startsWith("Value: ")) {
                fader.setValue(Float.parseFloat(line.split(": ")[1]));
            } else if (line.startsWith("Sequence: ")) {
                fader.setSequence(SequencesUI.byName(line.split(": ")[1]));
            } else if (line.startsWith("  Red: ")) {
                fader.setRed(Float.parseFloat(line.split(": ")[1]));
            } else if (line.startsWith("  Green: ")) {
                fader.setGreen(Float.parseFloat(line.split(": ")[1]));
            } else if (line.startsWith("  Blue: ")) {
                fader.setBlue(Float.parseFloat(line.split(": ")[1]));
            }
        }

        if (fader.getID() == -1) return;
        add(fader);
    }

    private void loadBank(FileHandle fileHandle) {
        FaderBank bank = null;
        for (String line : fileHandle.readString().split("\\r?\\n")) {
            if (line.startsWith("ID: ")) {
                bank = getBank(Integer.parseInt(line.split(": ")[1]));
            } else if (line.startsWith("Faders:")) {
                // do nothing
            } else if (line.startsWith("  - ")) {
                bank.add(Fader.byID(Integer.parseInt(line.split("- ")[1])));
            }
        }
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        if (containsMouse()) UIHandler.setSection(UIHandler.Section.FADERS);
        boolean interacted = false;

        setHeight(FadersUI.HEIGHT);

        float x = getX();
        float y = getY();
        float faderWidth = 80;
        float cellHeight = 30;

        float height = getHeight() - cellHeight - cellHeight;

        Util.box(renderer, x, y, getWidth(), cellHeight, Lights.DARK_BLUE, setWidth(renderer, "Faders"), Task.TextPosition.CENTER);
        drag(x, y, getWidth(), cellHeight);
        y -= cellHeight;

        Util.box(renderer, x, y, getWidth(), cellHeight, Lights.medium(), setWidth(renderer, "Bank: " + getBank().getID()));
        if (Util.containsMouse(x, y, getWidth(), cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.leftMouseReady(1000)) {
                if (Util.containsMouse(x, y, getWidth() / 2, cellHeight)) {
                    setBank(getBank().getID() + 1);
                } else {
                    setBank(getBank().getID() - 1);
                }
            }
        }
        y -= cellHeight;

        for (Fader fader : getBank().faders()) {
            Util.box(renderer, x, y, faderWidth, height, Lights.medium(), Util.getPercentage(fader.getValue()), Task.TextPosition.CENTER);
            float fill = fader.getValue() * height;
            Util.box(renderer, x, y - height + fill, faderWidth, fill, fader.getColor());
            if (Util.containsMouse(x, y, faderWidth, height) && canInteract()) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    interacted = true;
                    float bottom = y - height + 20;
                    fader.setValue(Math.min(Math.max((Gdx.graphics.getHeight() - Gdx.input.getY() - bottom) / (y - 20 - bottom), 0), 1));
                } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    FaderEditUI.edit(fader);
                }
            }
            x += faderWidth;
        }

        setWidth(Math.max(faderWidth, x - getX()));
        return interacted;
    }

    public void setBank(int bank) {
        if (bank <= 0) return;
        this.bank = bank;
    }

    public FaderBank getBank() {
        return getBank(bank);
    }

    public static void move(Fader fader) {
        fadersUI.faderToMove = fader;
    }

    public static void remove(Fader fader) {
        fadersUI.faderMap.remove(fader.getID());
    }

    public static void add(Fader fader) {
        fadersUI.faderMap.put(fader.getID(), fader);
    }

    public static FaderBank getBank(int bank) {
        if (!fadersUI.bankMap.containsKey(bank)) fadersUI.bankMap.put(bank, new FaderBank(bank));
        return fadersUI.bankMap.get(bank);
    }

    public static List<Fader> faders() {
        return new ArrayList<>(fadersUI.faderMap.values());
    }

    public static List<FaderBank> banks() {
        return new ArrayList<>(fadersUI.bankMap.values());
    }
}