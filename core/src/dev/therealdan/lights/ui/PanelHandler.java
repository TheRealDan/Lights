package dev.therealdan.lights.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dev.therealdan.lights.controllers.Button;
import dev.therealdan.lights.controllers.Fader;
import dev.therealdan.lights.dmx.DMX;
import dev.therealdan.lights.fixtures.Fixture;
import dev.therealdan.lights.fixtures.Group;
import dev.therealdan.lights.fixtures.fixture.Profile;
import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.main.Theme;
import dev.therealdan.lights.panels.MenuIcon;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.ResizeIcon;
import dev.therealdan.lights.panels.panels.*;
import dev.therealdan.lights.programmer.CondensedFrame;
import dev.therealdan.lights.programmer.Frame;
import dev.therealdan.lights.programmer.Programmer;
import dev.therealdan.lights.programmer.Sequence;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.settings.Control;
import dev.therealdan.lights.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PanelHandler implements Visual {

    private Mouse _mouse;

    private static PanelHandler panelHandler;

    private ResizeIcon resizeIcon;

    private List<Panel> panels = new ArrayList<>();
    private Panel lastResize, resizing, lastInteract, lastDragged, dragging;
    private float xDifference, yDifference;

    private float master = 1.0f;

    private Action currentAction = Action.OTHER;
    private long lastActionUpdate = System.currentTimeMillis();

    private HashMap<Integer, Sequence> sequenceStack = new HashMap<>();
    private HashMap<Sequence, Long> clear = new HashMap<>();

    private long lastTempo = System.currentTimeMillis();
    private long tempo = 1000;

    private CondensedFrame targetCondensedFrame, currentCondensedFrame, previousCondensedFrame;
    private long condensedFrameTimestamp = System.currentTimeMillis();

    public PanelHandler(Mouse mouse, Theme theme) {
        _mouse = mouse;

        panelHandler = this;

        resizeIcon = new ResizeIcon();

        // Menu
        panels.add(new PanelVisibilityPanel());

        // Settings
        panels.add(new SettingsPanel());
        panels.add(new ControlsPanel());
        panels.add(new DMXInterfacePanel());

        // Setup
        panels.add(new ProfilesPanel());

        // TODO - Move elsewhere
        Profile.loadProfilesFromFile();

        panels.add(new PatchPanel());

        // TODO - Move elsewhere
        Fixture.loadFixturesFromFile();
        Group.loadGroupsFromFile();

        panels.add(new SequencesPanel());
        panels.add(new FaderEditorPanel());
        panels.add(new ButtonEditorPanel());

        // Util
        panels.add(new ConsolePanel(theme));
        panels.add(new DMXOutputPanel());

        // Programmer
        panels.add(new SequenceProgrammerPanel());
        panels.add(new NewSequenceProgrammerPanel());
        panels.add(new FixturesPanel());
        panels.add(new GroupsPanel());
        panels.add(new ParametersPanel());

        // Info
        panels.add(new FrozenPanel());
        panels.add(new ActiveSequencesPanel());
        panels.add(new TimingsPanel());

        // Panels
        panels.add(new MasterPanel());
        panels.add(new FadersPanel());
        panels.add(new ButtonsPanel(theme));

        // TODO - Move elsewhere
        Button.loadButtonsFromFile();

        for (Panel panel : UIs())
            panel.load();
    }

    public void save() {
        for (Panel panel : UIs())
            panel.save();

        // TODO - Move elsewhere?
        Fixture.saveFixturesToFile();
        Group.saveGroupsToFile();

        Profile.saveProfilesToFile();

        Button.saveButtonsToFile();
    }

    public void update() {
        DMX output = DMX.get("OUTPUT");
        DMX visualiser = DMX.get("VISUALISER");

        for (Sequence sequence : getSequences()) {
            if (!sequence.isPlaying())
                sequence.play();

            if (sequence.useTempo()) {
                for (Frame frame : sequence.frames()) {
                    frame.setFrameTime(getTempo());
                    frame.setFadeTime(getTempo());
                }
            }

            boolean skip = false;
            if (sequence.doesClear() && sequence.onLastFrame()) {
                if (!clear.containsKey(sequence)) {
                    clear.put(sequence, System.currentTimeMillis());
                } else if (System.currentTimeMillis() - clear.get(sequence) > sequence.getActiveFrame().getFrameTime()) {
                    for (Button button : Button.buttons()) {
                        Control control = Control.byButton(button);
                        if (control != null && Gdx.input.isKeyPressed(control.getKeycode())) {
                            skip = true;
                            continue;
                        }
                    }
                    if (skip) continue;

                    clear.remove(sequence);
                    clearSequence(getPriority(sequence));
                    sequence.stop();
                    sequence.first();
                }
            }
        }

        CondensedFrame newCondensedFrame = new CondensedFrame();
        for (int priority = 0; priority <= getTopPriority(); priority++)
            if (contains(priority))
                newCondensedFrame.merge(getSequence(priority));
        for (Fader fader : FadersPanel.faders())
            newCondensedFrame.merge(fader);

        if (targetCondensedFrame == null) {
            targetCondensedFrame = newCondensedFrame.clone();
            currentCondensedFrame = newCondensedFrame.clone();
            previousCondensedFrame = newCondensedFrame.clone();
        }

        if (!newCondensedFrame.equals(targetCondensedFrame)) {
            targetCondensedFrame = newCondensedFrame.clone();
            previousCondensedFrame = currentCondensedFrame.clone();
            condensedFrameTimestamp = System.currentTimeMillis();
        }

        if (!currentCondensedFrame.equals(targetCondensedFrame)) {
            currentCondensedFrame.calculate(targetCondensedFrame, previousCondensedFrame, condensedFrameTimestamp);
        }

        for (int address = 1; address <= DMX.MAX_CHANNELS; address++) {
            if (Programmer.activeFrameHasValueFor(address)) {
                visualiser.set(address, (int) Programmer.getActiveFrameValueFor(address));
            } else {
                visualiser.set(address, currentCondensedFrame.getValue(address));
            }
        }

        if (!Lights.output.isFrozen()) output.copy(visualiser);
    }

    @Override
    public boolean draw(Mouse mouse, Renderer renderer) {
        Panel allowInteract = null;
        for (Panel panel : UIs()) {
            panel.setAllowInteract(false);
            if (panel.isVisible() && _mouse.within(panel) && !isDragging())
                allowInteract = panel;
        }
        if (allowInteract != null) allowInteract.setAllowInteract(true);

        for (Panel panel : UIs()) {
            if (panel.isVisible()) {
                long timestamp = System.currentTimeMillis();

                boolean hoverResize = panel.drawBackground(_mouse, renderer, panel.getX(), panel.getY(), panel.getWidth(), panel.getHeight());
                boolean interacted = hoverResize;
                interacted = panel.drawMenuBar(renderer, panel.getX(), panel.getY(), panel.getWidth(), Panel.MENU_HEIGHT, interacted);
                interacted = panel.drawMenuIcons(_mouse, renderer, panel.getX(), panel.getY(), panel.getWidth(), Panel.MENU_HEIGHT, MenuIcon.SIZE, MenuIcon.SIZE, (Panel.MENU_HEIGHT - MenuIcon.SIZE) / 2, interacted);
                interacted = panel.drawContent(mouse, renderer, panel.getX(), panel.getY() - Panel.MENU_HEIGHT, panel.getWidth(), panel.getHeight() - Panel.MENU_HEIGHT, interacted);

                if (panel.draw(_mouse, renderer, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight())) interacted = true;

                if (hoverResize) {
                    setAction(Action.PANEL_RESIZE);
                    lastResize = panel;

                    boolean clicked = _mouse.leftClicked(1000);
                    resizeIcon.draw(renderer, panel.getX() + panel.getWidth() - 20, panel.getY() - panel.getHeight() + 20, 20, 20, -1, true, clicked);
                    if (clicked) resize(panel);
                } else if (_mouse.within(panel) && interacted) {
                    setAction(Action.PANEL_INTERACT);
                    lastInteract = panel;
                }

                renderer.draw();

                TimingsPanel.set(panel.getName(), panel.getName() + " draw(): %mms %zms %ams", System.currentTimeMillis() - timestamp);
            } else {
                TimingsPanel.clear(panel.getName());
            }
        }

        if (isDragging()) {
            getDragging().setLocation(Gdx.input.getX() - xDifference, Gdx.input.getY() - yDifference);
            if (!_mouse.within(getDragging())) drag(null);
        }

        if (isResizing()) {
            getResizing().setWidth(Gdx.input.getX() - getResizing().getX() + 10);
            getResizing().setHeight(Gdx.input.getY() - getResizing().getYString() + 10);
            if (!_mouse.within(getResizing())) resize(null);
        }

        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        for (Panel panel : UIs())
            panel.scrolled(_mouse, amount);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        drag(null);
        resize(null);

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Util.isShiftHeld();

        if (keycode == Input.Keys.SPACE) {
            tempo = System.currentTimeMillis() - lastTempo;
            lastTempo = System.currentTimeMillis();
        }

        if (Input.Keys.ESCAPE == keycode) Lights.output.toggleFreeze();

        for (Panel panel : UIs()) {
            if (panel.isVisible() && _mouse.within(panel) && panel.canInteract()) {
                if (!panel.keyDown(keycode)) return false;
            }
        }

        for (Button button : Button.buttons()) {
            Control control = Control.byButton(button);
            if (control != null && control.getKeycode() == keycode) {
                button.press();
            }
        }

        switch (keycode) {
            case Input.Keys.MINUS:
                MasterPanel.fadeToZero();
                break;
            case Input.Keys.EQUALS:
                MasterPanel.fadeToMax();
                break;
        }

        Fader fader = Fader.byID(shift ? 2 : 1);
        if (fader != null) {
            switch (keycode) {
                case Input.Keys.NUM_1:
                    fader.setValue(0.1f);
                    break;
                case Input.Keys.NUM_2:
                    fader.setValue(0.2f);
                    break;
                case Input.Keys.NUM_3:
                    fader.setValue(0.3f);
                    break;
                case Input.Keys.NUM_4:
                    fader.setValue(0.4f);
                    break;
                case Input.Keys.NUM_5:
                    fader.setValue(0.5f);
                    break;
                case Input.Keys.NUM_6:
                    fader.setValue(0.6f);
                    break;
                case Input.Keys.NUM_7:
                    fader.setValue(0.7f);
                    break;
                case Input.Keys.NUM_8:
                    fader.setValue(0.8f);
                    break;
                case Input.Keys.NUM_9:
                    fader.setValue(1.0f);
                    break;
                case Input.Keys.NUM_0:
                    fader.setValue(0.0f);
                    break;
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (Panel panel : UIs())
            if (!panel.keyUp(keycode)) return false;

        return true;
    }

    public enum Action {
        PANEL_INTERACT, PANEL_DRAG, PANEL_RESIZE, OTHER;
    }

    public static void resize(Panel panel) {
        if (panel == null) {
            panelHandler.lastResize = panelHandler.resizing;
        } else if (panelHandler.resizing == null) {
            moveToTop(panel);
        }

        panelHandler.resizing = panel;
    }

    public static void drag(Panel panel) {
        if (panel == null) {
            panelHandler.lastDragged = panelHandler.dragging;
        } else if (panelHandler.dragging == null) {
            panelHandler.xDifference = Math.abs(panel.getX() - Gdx.input.getX());
            panelHandler.yDifference = Math.abs(panel.getYString() - Gdx.input.getY());
            moveToTop(panel);
        }

        panelHandler.dragging = panel;
    }

    public static boolean isDragging() {
        return getDragging() != null;
    }

    public static boolean isResizing() {
        return getResizing() != null;
    }

    public static Panel getLastResize() {
        return panelHandler.lastResize;
    }

    public static Panel getResizing() {
        return panelHandler.resizing;
    }

    public static Panel getLastInteract() {
        return panelHandler.lastInteract;
    }

    public static Panel getLastDragged() {
        return panelHandler.lastDragged;
    }

    public static Panel getDragging() {
        return panelHandler.dragging;
    }

    public static Panel byName(String name) {
        for (Panel panel : UIs())
            if (panel.getName().equals(name) || panel.getFriendlyName().equalsIgnoreCase(name))
                return panel;

        return null;
    }

    public static void clearSequence(int priority) {
        panelHandler.sequenceStack.remove(priority);
    }

    public static void setSequence(int priority, Sequence sequence) {
        panelHandler.sequenceStack.put(priority, sequence);
    }

    public static boolean contains(int priority) {
        return panelHandler.sequenceStack.containsKey(priority);
    }

    public static Sequence getSequence(int priority) {
        return panelHandler.sequenceStack.getOrDefault(priority, null);
    }

    public static int getPriority(Sequence sequence) {
        for (int priority : panelHandler.sequenceStack.keySet())
            if (sequence.equals(getSequence(priority)))
                return priority;

        return -1;
    }

    public static int countSeqeunces() {
        return panelHandler.sequenceStack.size();
    }

    public static List<Sequence> getSequences() {
        List<Sequence> sequences = new ArrayList<>();
        for (int priority = 0; priority <= getTopPriority(); priority++)
            if (contains(priority))
                sequences.add(getSequence(priority));
        return sequences;
    }

    public static void moveToTop(Panel panel) {
        PanelHandler.panelHandler.panels.remove(panel);
        PanelHandler.panelHandler.panels.add(panel);
    }

    public static List<Panel> UIs() {
        return new ArrayList<>(panelHandler.panels);
    }

    public static long getTempo() {
        return panelHandler.tempo;
    }

    public static int getTopPriority() {
        int highest = 0;
        for (int priority : panelHandler.sequenceStack.keySet())
            if (priority > highest) highest = priority;
        return highest;
    }

    public static void setMaster(float master) {
        panelHandler.master = Math.min(Math.max(master, 0), 1);
    }

    public static float getMaster() {
        return panelHandler.master;
    }

    public static void setAction(Action action) {
        panelHandler.lastActionUpdate = System.currentTimeMillis();
        panelHandler.currentAction = action;
    }

    public static Action getCurrentAction() {
        if (isDragging()) return Action.PANEL_DRAG;
        if (System.currentTimeMillis() - panelHandler.lastActionUpdate > 500)
            panelHandler.currentAction = Action.OTHER;
        return panelHandler.currentAction;
    }
}