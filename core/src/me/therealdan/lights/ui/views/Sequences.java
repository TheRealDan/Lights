package me.therealdan.lights.ui.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.LightsCore;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.Fixture;
import me.therealdan.lights.fixtures.Group;
import me.therealdan.lights.programmer.Frame;
import me.therealdan.lights.programmer.Sequence;
import me.therealdan.lights.programmer.Task;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.ui.view.Tab;
import me.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sequences implements Tab {

    private static Sequences instance;

    private List<Sequence> sequences = new ArrayList<>();
    private Sequence sequence = null;
    private Frame frame = null;

    private Section section = Section.NONE;
    private Edit edit = Edit.NONE;
    private int sequenceStart = 0, frameStart = 0, taskStart = 0;
    private int maxRows = 100;

    private final long delay = 1000;

    private String frameMilliseconds = "";
    private long frameTimestamp = System.currentTimeMillis();

    private String fadeMilliseconds = "";
    private long fadeTimestamp = System.currentTimeMillis();

    public Sequences() {
        instance = this;

        Random random = new Random();

        Sequence stage = new Sequence("Stage", false, true, true);
        Frame stageFrame = new Frame(1000, 1000);
        stageFrame.set(Patch.fixtureByName("Dimmer"), Channel.Type.INTENSITY, 255f, 1, 2, 3, 4);
        stage.add(stageFrame);
        add(stage);

        Sequence house = new Sequence("House", false, true, true);
        Frame houseFrame = new Frame(1000, 1000);
        houseFrame.set(Patch.fixtureByName("Dimmer"), Channel.Type.INTENSITY, 255f, 5, 6, 7, 8);
        house.add(houseFrame);
        add(house);

        Sequence sideCans = new Sequence("Side Cans", false, true, true);
        Frame sideCansFrame = new Frame(1000, 1000);
        sideCansFrame.set(Patch.fixtureByName("Dimmer"), Channel.Type.INTENSITY, 128f, 12);
        sideCans.add(sideCansFrame);
        add(sideCans);

        Sequence stageCans = new Sequence("Stage Cans", false, true, true);
        Frame stageCansFrame = new Frame(100, 0);
        for (Fixture fixture : Patch.groupByName("Cans").fixtures()) {
            stageCansFrame.set(fixture, Channel.Type.INTENSITY, 255f, 1);
        }
        stageCans.add(stageCansFrame);
        add(stageCans);

        Sequence red = new Sequence("Red", false, true, true);
        Frame redFrame = new Frame(1000, 1000);
        for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
            redFrame.set(fixture, Channel.Type.RED, 255f, 1, 2, 3);
            redFrame.set(fixture, Channel.Type.GREEN, 0f, 1, 2, 3);
            redFrame.set(fixture, Channel.Type.BLUE, 0f, 1, 2, 3);
        }
        red.add(redFrame);
        add(red);

        Sequence green = new Sequence("Green", false, true, true);
        Frame greenFrame = new Frame(1000, 1000);
        for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
            greenFrame.set(fixture, Channel.Type.RED, 0f, 1, 2, 3);
            greenFrame.set(fixture, Channel.Type.GREEN, 255f, 1, 2, 3);
            greenFrame.set(fixture, Channel.Type.BLUE, 0f, 1, 2, 3);
        }
        green.add(greenFrame);
        add(green);

        Sequence blue = new Sequence("Blue", false, true, true);
        Frame blueFrame = new Frame(1000, 1000);
        for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
            blueFrame.set(fixture, Channel.Type.RED, 0f, 1, 2, 3);
            blueFrame.set(fixture, Channel.Type.GREEN, 0f, 1, 2, 3);
            blueFrame.set(fixture, Channel.Type.BLUE, 255f, 1, 2, 3);
        }
        blue.add(blueFrame);
        add(blue);

        Sequence magenta = new Sequence("Magenta", false, true, true);
        Frame magentaFrame = new Frame(1000, 1000);
        for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
            magentaFrame.set(fixture, Channel.Type.RED, 255f, 1, 2, 3);
            magentaFrame.set(fixture, Channel.Type.GREEN, 0f, 1, 2, 3);
            magentaFrame.set(fixture, Channel.Type.BLUE, 255f, 1, 2, 3);
        }
        magenta.add(magentaFrame);
        add(magenta);

        Sequence yellow = new Sequence("Yellow", false, true, true);
        Frame yellowFrame = new Frame(1000, 1000);
        for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
            yellowFrame.set(fixture, Channel.Type.RED, 255f, 1, 2, 3);
            yellowFrame.set(fixture, Channel.Type.GREEN, 255f, 1, 2, 3);
            yellowFrame.set(fixture, Channel.Type.BLUE, 0f, 1, 2, 3);
        }
        yellow.add(yellowFrame);
        add(yellow);

        Sequence cyan = new Sequence("Cyan", false, true, true);
        Frame cyanFrame = new Frame(1000, 1000);
        for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
            cyanFrame.set(fixture, Channel.Type.RED, 0f, 1, 2, 3);
            cyanFrame.set(fixture, Channel.Type.GREEN, 255f, 1, 2, 3);
            cyanFrame.set(fixture, Channel.Type.BLUE, 255f, 1, 2, 3);
        }
        cyan.add(cyanFrame);
        add(cyan);

        Sequence purple = new Sequence("Purple", false, true, true);
        Frame purpleFrame = new Frame(1000, 1000);
        for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
            purpleFrame.set(fixture, Channel.Type.RED, 128f, 1, 2, 3);
            purpleFrame.set(fixture, Channel.Type.GREEN, 0f, 1, 2, 3);
            purpleFrame.set(fixture, Channel.Type.BLUE, 255f, 1, 2, 3);
        }
        purple.add(purpleFrame);
        add(purple);

        Sequence orange = new Sequence("Orange", false, true, true);
        Frame orangeFrame = new Frame(1000, 1000);
        for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
            orangeFrame.set(fixture, Channel.Type.RED, 255f, 1, 2, 3);
            orangeFrame.set(fixture, Channel.Type.GREEN, 128f, 1, 2, 3);
            orangeFrame.set(fixture, Channel.Type.BLUE, 0f, 1, 2, 3);
        }
        orange.add(orangeFrame);
        add(orange);

        Sequence white = new Sequence("White", false, true, true);
        Frame whiteFrame = new Frame(1000, 1000);
        for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
            whiteFrame.set(fixture, Channel.Type.RED, 255f, 1, 2, 3);
            whiteFrame.set(fixture, Channel.Type.GREEN, 255f, 1, 2, 3);
            whiteFrame.set(fixture, Channel.Type.BLUE, 255f, 1, 2, 3);
        }
        white.add(whiteFrame);
        add(white);

        Sequence black = new Sequence("Black", false, true, true);
        Frame blackFrame = new Frame(1000, 1000);
        for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
            blackFrame.set(fixture, Channel.Type.RED, 0f, 1, 2, 3);
            blackFrame.set(fixture, Channel.Type.GREEN, 0f, 1, 2, 3);
            blackFrame.set(fixture, Channel.Type.BLUE, 0f, 1, 2, 3);
        }
        black.add(blackFrame);
        add(black);

        Sequence solid = new Sequence("Solid", false, true, true);
        for (int i = 0; i < 16; i++) {
            Frame frame = new Frame(1000, 1000);
            for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
                frame.set(fixture, Channel.Type.INTENSITY, 255f, 1, 2, 3);
            }
            solid.add(frame);
        }
        add(solid);

        Sequence flashySubtle = new Sequence("Flashy Subtle");
        for (int i = 0; i < 16; i++) {
            Frame frame = new Frame(100, 0);
            for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
                frame.set(fixture, Channel.Type.INTENSITY, 255f, 1);
            }
            for (Fixture fixture : Patch.groupByName("Mings").fixtures()) {
                frame.set(fixture, Channel.Type.INTENSITY, random.nextBoolean() ? 255f : 0f, 1);
                frame.set(fixture, Channel.Type.INTENSITY, random.nextBoolean() ? 255f : 0f, 2);
                frame.set(fixture, Channel.Type.INTENSITY, random.nextBoolean() ? 255f : 0f, 3);
            }
            flashySubtle.add(frame);
        }
        add(flashySubtle);

        Sequence flashyx3 = new Sequence("Flashy x3");
        for (int i = 0; i < 16; i++) {
            Frame frame = new Frame(100, 0);
            for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
                frame.set(fixture, Channel.Type.INTENSITY, random.nextBoolean() ? 255f : 0f, 1);
                frame.set(fixture, Channel.Type.INTENSITY, random.nextBoolean() ? 255f : 0f, 2);
                frame.set(fixture, Channel.Type.INTENSITY, random.nextBoolean() ? 255f : 0f, 3);
            }
            flashyx3.add(frame);
        }
        add(flashyx3);

        Sequence flashyx1 = new Sequence("Flashy x1");
        for (int i = 0; i < 16; i++) {
            Frame frame = new Frame(100, 0);
            for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
                frame.set(fixture, Channel.Type.INTENSITY, random.nextBoolean() ? 255f : 0f, 1, 2, 3);
            }
            flashyx1.add(frame);
        }
        add(flashyx1);

        Sequence strobe = new Sequence("Strobe");
        Frame strobeFrameOn = new Frame(100, 0);
        Frame strobeFrameOff = new Frame(100, 0);
        for (Fixture fixture : Patch.groupByName("Fluros").fixtures()) {
            strobeFrameOn.set(fixture, Channel.Type.INTENSITY, 0f, 1, 2, 3);
            strobeFrameOff.set(fixture, Channel.Type.INTENSITY, 255f, 1, 2, 3);
        }
        strobe.add(strobeFrameOn);
        strobe.add(strobeFrameOff);
        add(strobe);

        Sequence ledsFlash = new Sequence("LEDs Flashy");
        Frame ledsFlashFrame1 = new Frame(100, 0);
        Frame ledsFlashFrame2 = new Frame(100, 0);
        boolean alternate = true;
        for (Fixture fixture : Patch.groupByName("LEDs").fixtures()) {
            ledsFlashFrame1.set(fixture, Channel.Type.INTENSITY, alternate ? 255f : 0f, 1);
            ledsFlashFrame2.set(fixture, Channel.Type.INTENSITY, alternate ? 0f : 255f, 1);
            alternate = !alternate;
        }
        ledsFlash.add(ledsFlashFrame1);
        ledsFlash.add(ledsFlashFrame2);
        add(ledsFlash);

        Sequence magentaOverlay = new Sequence("M Overlay");
        Frame magentaOverlayFrame1 = new Frame(1000, 1000);
        Frame magentaOverlayFrame2 = new Frame(1000, 1000);
        Frame magentaOverlayFrame3 = new Frame(1000, 1000);
        Frame magentaOverlayFrame4 = new Frame(1000, 1000);
        Frame magentaOverlayFrame5 = new Frame(1000, 1000);
        Frame magentaOverlayFrame6 = new Frame(1000, 1000);
        Group group = Patch.groupByName("Mings");
        magentaOverlayFrame1.set(group.fixtures().get(0), Channel.Type.RED, 255f, 1, 2, 3);
        magentaOverlayFrame1.set(group.fixtures().get(0), Channel.Type.GREEN, 0f, 1, 2, 3);
        magentaOverlayFrame1.set(group.fixtures().get(0), Channel.Type.BLUE, 255f, 1, 2, 3);
        magentaOverlayFrame1.set(group.fixtures().get(6), Channel.Type.RED, 255f, 1, 2, 3);
        magentaOverlayFrame1.set(group.fixtures().get(6), Channel.Type.GREEN, 0f, 1, 2, 3);
        magentaOverlayFrame1.set(group.fixtures().get(6), Channel.Type.BLUE, 255f, 1, 2, 3);
        magentaOverlayFrame2.set(group.fixtures().get(1), Channel.Type.RED, 255f, 1, 2, 3);
        magentaOverlayFrame2.set(group.fixtures().get(1), Channel.Type.GREEN, 0f, 1, 2, 3);
        magentaOverlayFrame2.set(group.fixtures().get(1), Channel.Type.BLUE, 255f, 1, 2, 3);
        magentaOverlayFrame2.set(group.fixtures().get(7), Channel.Type.RED, 255f, 1, 2, 3);
        magentaOverlayFrame2.set(group.fixtures().get(7), Channel.Type.GREEN, 0f, 1, 2, 3);
        magentaOverlayFrame2.set(group.fixtures().get(7), Channel.Type.BLUE, 255f, 1, 2, 3);
        magentaOverlayFrame3.set(group.fixtures().get(2), Channel.Type.RED, 255f, 1, 2, 3);
        magentaOverlayFrame3.set(group.fixtures().get(2), Channel.Type.GREEN, 0f, 1, 2, 3);
        magentaOverlayFrame3.set(group.fixtures().get(2), Channel.Type.BLUE, 255f, 1, 2, 3);
        magentaOverlayFrame4.set(group.fixtures().get(3), Channel.Type.RED, 255f, 1, 2, 3);
        magentaOverlayFrame4.set(group.fixtures().get(3), Channel.Type.GREEN, 0f, 1, 2, 3);
        magentaOverlayFrame4.set(group.fixtures().get(3), Channel.Type.BLUE, 255f, 1, 2, 3);
        magentaOverlayFrame5.set(group.fixtures().get(4), Channel.Type.RED, 255f, 1, 2, 3);
        magentaOverlayFrame5.set(group.fixtures().get(4), Channel.Type.GREEN, 0f, 1, 2, 3);
        magentaOverlayFrame5.set(group.fixtures().get(4), Channel.Type.BLUE, 255f, 1, 2, 3);
        magentaOverlayFrame5.set(group.fixtures().get(8), Channel.Type.RED, 255f, 1, 2, 3);
        magentaOverlayFrame5.set(group.fixtures().get(8), Channel.Type.GREEN, 0f, 1, 2, 3);
        magentaOverlayFrame5.set(group.fixtures().get(8), Channel.Type.BLUE, 255f, 1, 2, 3);
        magentaOverlayFrame6.set(group.fixtures().get(5), Channel.Type.RED, 255f, 1, 2, 3);
        magentaOverlayFrame6.set(group.fixtures().get(5), Channel.Type.GREEN, 0f, 1, 2, 3);
        magentaOverlayFrame6.set(group.fixtures().get(5), Channel.Type.BLUE, 255f, 1, 2, 3);
        magentaOverlayFrame6.set(group.fixtures().get(9), Channel.Type.RED, 255f, 1, 2, 3);
        magentaOverlayFrame6.set(group.fixtures().get(9), Channel.Type.GREEN, 0f, 1, 2, 3);
        magentaOverlayFrame6.set(group.fixtures().get(9), Channel.Type.BLUE, 255f, 1, 2, 3);
        magentaOverlay.add(magentaOverlayFrame1);
        magentaOverlay.add(magentaOverlayFrame2);
        magentaOverlay.add(magentaOverlayFrame3);
        magentaOverlay.add(magentaOverlayFrame4);
        magentaOverlay.add(magentaOverlayFrame5);
        magentaOverlay.add(magentaOverlayFrame6);
        add(magentaOverlay);
    }

    @Override
    public boolean scrolled(int amount) {
        if (getSection() == null) return true;

        switch (getSection()) {
            case SEQUENCES:
                sequenceStart += amount;
                if (sequenceStart < 0) sequenceStart = 0;
                if (sequenceStart > sequences().size() - maxRows) sequenceStart = sequences().size() - maxRows;
                break;

            case FRAMES:
                frameStart += amount;
                if (frameStart < 0) frameStart = 0;
                if (frameStart > getSequence().frames().size() - maxRows) frameStart = getSequence().frames().size() - maxRows;
                break;

        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        switch (getEdit()) {
            case SEQUENCE_NAME:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        if (getSequence().getName().length() > 0)
                            getSequence().rename(getSequence().getName().substring(0, getSequence().getName().length() - 1));
                        if (shift) getSequence().rename("");
                        break;
                    case Input.Keys.SPACE:
                        getSequence().rename(getSequence().getName() + " ");
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                            if (!shift) string = string.toLowerCase();
                            getSequence().rename(getSequence().getName() + string);
                        }
                }
                break;

            case FRAME_TIME:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        if (getFrame() != null) getFrame().setFrameTime(0);
                        break;
                    default:
                        String string = Input.Keys.toString(keycode);
                        if ("1234567890".contains(string)) {
                            frameMilliseconds += string;
                            frameTimestamp = System.currentTimeMillis();
                        }
                }
                break;

            case FADE_TIME:
                switch (keycode) {
                    case Input.Keys.BACKSPACE:
                        if (getFrame() != null) getFrame().setFadeTime(0);
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

    @Override
    public void update() {
        Sequence sequence = getSequence();
        Frame frame = getFrame();

        if (frame != null) {
            if (System.currentTimeMillis() - frameTimestamp > delay && frameMilliseconds.length() > 0) {
                frame.setFrameTime(Long.parseLong(frameMilliseconds));
                frameMilliseconds = "";
            }

            if (System.currentTimeMillis() - fadeTimestamp > delay && fadeMilliseconds.length() > 0) {
                frame.setFadeTime(Long.parseLong(fadeMilliseconds));
                fadeMilliseconds = "";
            }
        }


        if (sequence != null && frame != null) {
            if (sequence.globalFrameTime()) {
                long frameTime = frame.getFrameTime();
                for (Frame each : sequence.frames())
                    if (each.getFrameTime() != frameTime)
                        each.setFrameTime(frameTime);
            }

            if (sequence.globalFadeTime()) {
                long fadeTime = frame.getFadeTime();
                for (Frame each : sequence.frames())
                    if (each.getFadeTime() != fadeTime)
                        each.setFadeTime(fadeTime);
            }
        }
    }

    @Override
    public void draw(Renderer renderer, float X, float Y, float WIDTH, float HEIGHT) {
        float cellHeight = 30;

        float x = X + LightsCore.edge();
        float y = Y + HEIGHT - LightsCore.edge();

        float width = (WIDTH - LightsCore.edge() * 6f) / 5f;

        sequences(renderer, x, y, width, cellHeight);
        sequence(renderer, x + width + LightsCore.edge(), y, width, cellHeight);
        frames(renderer, x + width * 2f + LightsCore.edge() * 2f, y, width, cellHeight);
        frame(renderer, x + width * 3f + LightsCore.edge() * 3f, y, width, cellHeight);
        tasks(renderer, x + width * 4f + LightsCore.edge() * 4f, y, width, cellHeight);
    }

    private void sequences(Renderer renderer, float x, float y, float width, float cellHeight) {
        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Sequences");
        y -= cellHeight;

        int current = 0;
        boolean alternate = true;
        for (Sequence sequence : sequences()) {
            if (current - sequenceStart >= maxRows) break;
            if (current >= sequenceStart) {
                Util.box(renderer, x, y, width, cellHeight, sequence.equals(getSequence()) ? LightsCore.DARK_RED : alternate ? LightsCore.medium() : LightsCore.dark(), sequence.getName());
                if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                    setSection(Section.SEQUENCES);
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                        select(sequence);
                }
                y -= cellHeight;
                alternate = !alternate;
            }
            current++;
        }
    }

    private void sequence(Renderer renderer, float x, float y, float width, float cellHeight) {
        Sequence sequence = getSequence();
        if (sequence == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Sequence");
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, getEdit().equals(Edit.SEQUENCE_NAME) ? LightsCore.DARK_RED : LightsCore.medium(), "Name: " + sequence.getName());
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            setSection(Section.SEQUENCE);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                setEdit(Edit.SEQUENCE_NAME);
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, sequence.doesLoop() ? LightsCore.DARK_RED : LightsCore.medium(), "Loop: " + (sequence.doesLoop() ? "Enabled" : "Disabled"));
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            setSection(Section.SEQUENCE);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                sequence.toggleLoop();
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, sequence.useTempo() ? LightsCore.DARK_RED : LightsCore.medium(), "Use Global Tempo: " + (sequence.useTempo() ? "Enabled" : "Disabled"));
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            setSection(Section.SEQUENCE);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                sequence.toggleUseTempo();
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, sequence.globalFrameTime() ? LightsCore.DARK_RED : LightsCore.medium(), "Global Frame Time: " + (sequence.globalFrameTime() ? "Enabled" : "Disabled"));
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            setSection(Section.SEQUENCE);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                sequence.toggleGlobalFrameTime();
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, sequence.globalFadeTime() ? LightsCore.DARK_RED : LightsCore.medium(), "Global Fade Time: " + (sequence.globalFadeTime() ? "Enabled" : "Disabled"));
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            setSection(Section.SEQUENCE);
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && LightsCore.actionReady(500))
                sequence.toggleGlobalFadeTime();
        }
        y -= cellHeight;
    }

    private void frames(Renderer renderer, float x, float y, float width, float cellHeight) {
        Sequence sequence = getSequence();
        if (sequence == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, sequence.frames().size() == 1 ? "1 Frame" : sequence.frames().size() + " Frames");
        y -= cellHeight;

        int current = 0;
        boolean alternate = true;
        for (Frame frame : sequence.frames()) {
            if (current - frameStart >= maxRows) break;
            if (current >= frameStart) {
                Util.box(renderer, x, y, width, cellHeight, frame.equals(getFrame()) ? LightsCore.DARK_RED : alternate ? LightsCore.medium() : LightsCore.dark(), frame.getInfo());
                if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                    setSection(Section.FRAMES);
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                        select(frame);
                }
                y -= cellHeight;
                alternate = !alternate;
            }
            current++;
        }
    }

    private void frame(Renderer renderer, float x, float y, float width, float cellHeight) {
        Sequence sequence = getSequence();
        if (sequence == null) return;
        Frame frame = getFrame();
        if (frame == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, "Frame");
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, getEdit().equals(Edit.FRAME_TIME) ? LightsCore.DARK_RED : LightsCore.medium(), "Frame Time: " + Frame.format(frame.getFrameTime()));
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                setEdit(Edit.FRAME_TIME);
        }
        y -= cellHeight;

        Util.box(renderer, x, y, width, cellHeight, getEdit().equals(Edit.FADE_TIME) ? LightsCore.DARK_RED : LightsCore.medium(), "Fade Time: " + Frame.format(frame.getFadeTime()));
        if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
                setEdit(Edit.FADE_TIME);
        }
        y -= cellHeight;
    }

    private void tasks(Renderer renderer, float x, float y, float width, float cellHeight) {
        Sequence sequence = getSequence();
        if (sequence == null) return;
        Frame frame = getFrame();
        if (frame == null) return;

        Util.box(renderer, x, y, width, cellHeight, LightsCore.DARK_BLUE, frame.tasks().size() == 1 ? "1 Task" : frame.tasks().size() + " Tasks");
        y -= cellHeight;

        int current = 0;
        boolean alternate = true;
        for (Task task : frame.tasks()) {
            if (current - taskStart >= maxRows) break;
            if (current >= taskStart) {
                Util.box(renderer, x, y, width, cellHeight, alternate ? LightsCore.medium() : LightsCore.dark(), task.getInfo());
                if (Util.containsMouse(x, Gdx.graphics.getHeight() - y, width, cellHeight)) {
                    setSection(Section.TASKS);
                }
                y -= cellHeight;
                alternate = !alternate;
            }
            current++;
        }
    }

    private void select(Frame frame) {
        this.frame = frame;
    }

    private Frame getFrame() {
        return frame;
    }

    private void select(Sequence sequence) {
        this.sequence = sequence;
        this.frame = null;
    }

    private Sequence getSequence() {
        return sequence;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    private Section getSection() {
        return section;
    }

    private void setEdit(Edit edit) {
        this.edit = edit;
    }

    private Edit getEdit() {
        return edit;
    }

    public enum Section {
        NONE,

        SEQUENCES,

        SEQUENCE,

        FRAMES,

        TASKS;
    }

    public enum Edit {
        NONE,

        SEQUENCE_NAME,

        FRAME_TIME,
        FADE_TIME,
    }

    public static void remove(String name) {
        for (Sequence sequence : sequences()) {
            if (sequence.getName().equalsIgnoreCase(name)) {
                remove(sequence);
            }
        }
    }

    public static void remove(Sequence sequence) {
        instance.sequences.remove(sequence);
    }

    public static void add(Sequence sequence) {
        instance.sequences.add(sequence);
    }

    public static Sequence byName(String name) {
        for (Sequence sequence : sequences())
            if (sequence.getName().equalsIgnoreCase(name))
                return sequence;

        return null;
    }

    public static List<Sequence> sequences() {
        return new ArrayList<>(instance.sequences);
    }
}