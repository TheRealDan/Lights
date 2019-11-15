package dev.therealdan.lights.panels.panels;

import dev.therealdan.lights.main.Lights;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.programmer.Sequence;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;
import dev.therealdan.lights.ui.PanelHandler;

public class ActiveSequencesPanel implements Panel {

    private float rowHeight;

    public ActiveSequencesPanel() {
        register(new CloseIcon());

        setMinimumWidth(300);
    }

    @Override
    public boolean drawContent(Renderer renderer, float x, float y, float width, float height, boolean interacted) {
        if (PanelHandler.getSequences().size() == 0) {
            renderer.box(x, y, width, height, Lights.color.MEDIUM, "No Sequences Active", Task.TextPosition.CENTER);
            return interacted;
        }

        float rowHeight = getRowHeight();
        for (int priority = 0; priority <= PanelHandler.getTopPriority(); priority++) {
            Sequence sequence = PanelHandler.getSequence(priority);
            if (sequence == null) continue;

            renderer.box(x, y, width, rowHeight, Lights.color.MEDIUM, sequence.getName() + " - " + priority);
            if (Lights.mouse.contains(x, y, width, rowHeight) && canInteract(interacted)) {
                interacted = true;
                if (Lights.mouse.leftClicked(1000)) {
                    PanelHandler.clearSequence(priority);
                }
            }
            y -= rowHeight;
        }

        return interacted;
    }

    @Override
    public boolean isResizeable() {
        return true;
    }

    public float getRowHeight() {
        if (rowHeight <= 0 || getTargetRowHeight() < rowHeight) {
            rowHeight = getTargetRowHeight();
        } else if (getTargetRowHeight() > rowHeight) {
            rowHeight++;
        }
        return rowHeight;
    }

    public float getTargetRowHeight() {
        if (PanelHandler.countSeqeunces() == 0) return 0;
        return (getHeight() - Panel.MENU_HEIGHT) / PanelHandler.countSeqeunces();
    }
}