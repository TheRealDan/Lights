package dev.therealdan.lights.panels.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import dev.therealdan.lights.controllers.Fader;
import dev.therealdan.lights.controllers.FaderBank;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FadersPanel implements Panel {

    private static FadersPanel fadersUI;

    private static float HEIGHT = 250;

    private LinkedHashMap<Integer, Fader> faderMap = new LinkedHashMap<>();
    private LinkedHashMap<Integer, FaderBank> bankMap = new LinkedHashMap<>();

    private Fader faderToMove = null;
    private int bank = 1;

    public FadersPanel() {
        fadersUI = this;

        register(new CloseIcon());

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
        Fader fader = new Fader(-1, fileHandle.name());

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
                fader.setSequence(SequencesPanel.byName(line.split(": ")[1]));
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
    public void save() {
        for (Fader fader : faders()) {
            FileHandle fileHandle = Gdx.files.local("Lights/Faders/" + fader.getFileName() + ".txt");
            fileHandle.writeString("", false);

            fileHandle.writeString("ID: " + fader.getID() + "\r\n", true);
            fileHandle.writeString("Name: " + fader.getName() + "\r\n", true);
            fileHandle.writeString("Type: " + fader.getType().toString() + "\r\n", true);
            fileHandle.writeString("Value: " + fader.getValue() + "\r\n", true);
            fileHandle.writeString("Sequence: " + fader.getSequence().getName() + "\r\n", true);
            fileHandle.writeString("  Red: " + fader.getColor().r + "\r\n", true);
            fileHandle.writeString("  Green: " + fader.getColor().g + "\r\n", true);
            fileHandle.writeString("  Blue: " + fader.getColor().b + "\r\n", true);
        }
    }

    @Override
    public boolean draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        boolean interacted = false;

        setHeight(FadersPanel.HEIGHT);

        float x = getX();
        float y = getY();
        float faderWidth = 80;
        float cellHeight = 30;

        float height = getHeight() - cellHeight - cellHeight;

        renderer.box(x, y, getWidth(), cellHeight, Lights.theme.DARK_BLUE, setWidth(renderer, getFriendlyName()), Task.TextPosition.CENTER);
        drag(x, y, getWidth(), cellHeight);
        y -= cellHeight;

        renderer.box(x, y, getWidth(), cellHeight, Lights.theme.MEDIUM, setWidth(renderer, "Bank: " + getBank().getID()));
        if (Lights.mouse.contains(x, y, getWidth(), cellHeight) && canInteract()) {
            interacted = true;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Lights.mouse.leftReady(1000)) {
                if (Lights.mouse.contains(x, y, getWidth() / 2, cellHeight)) {
                    setBank(getBank().getID() + 1);
                } else {
                    setBank(getBank().getID() - 1);
                }
            }
        }
        y -= cellHeight;

        for (Fader fader : getBank().faders()) {
            renderer.box(x, y, faderWidth, height, Lights.theme.MEDIUM, Util.getPercentage(fader.getValue()), Task.TextPosition.CENTER);
            float fill = fader.getValue() * height;
            renderer.box(x, y - height + fill, faderWidth, fill, fader.getColor());
            if (Lights.mouse.contains(x, y, faderWidth, height) && canInteract()) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    interacted = true;
                    float bottom = y - height + 20;
                    fader.setValue(Math.min(Math.max((Gdx.graphics.getHeight() - Gdx.input.getY() - bottom) / (y - 20 - bottom), 0), 1));
                } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    FaderEditorPanel.edit(fader);
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