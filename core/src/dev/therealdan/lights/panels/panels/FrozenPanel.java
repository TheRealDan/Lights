package dev.therealdan.lights.panels.panels;

import dev.therealdan.lights.dmx.Output;
import dev.therealdan.lights.main.Mouse;
import dev.therealdan.lights.panels.MenuIcon;
import dev.therealdan.lights.panels.Panel;
import dev.therealdan.lights.panels.menuicons.CloseIcon;
import dev.therealdan.lights.renderer.Renderer;
import dev.therealdan.lights.renderer.Task;

public class FrozenPanel implements Panel {

    private Output _output;

    public FrozenPanel(Output output) {
        _output = output;

        setHeight(Panel.CELL_HEIGHT * 2);

        register(new CloseIcon());
    }

    @Override
    public boolean drawMenuBar(Renderer renderer, float x, float y, float width, float height, boolean interacted) {
        boolean flash = System.currentTimeMillis() % 1000 > 500;
        renderer.box(x, y, width, height,
                flash ? renderer.getTheme().DARK_BLUE : renderer.getTheme().RED,
                flash ? renderer.getTheme().TEXT : renderer.getTheme().BLACK,
                "OUTPUT FROZEN", Task.TextPosition.CENTER);
        return interacted;
    }

    @Override
    public boolean drawMenuIcons(Mouse mouse, Renderer renderer, float x, float y, float width, float height, float menuIconWidth, float menuIconHeight, float spacing, boolean interacted) {
        interacted = Panel.super.drawMenuIcons(mouse, renderer, x, y, width, height, menuIconWidth, menuIconHeight, spacing, interacted);

        setWidth(renderer.getWidth("OUTPUT FROZEN") + menuIconWidth * 4, true);

        return interacted;
    }

    @Override
    public boolean drawContent(Mouse mouse, Renderer renderer, float x, float y, float width, float height, boolean interacted) {
        renderer.box(x, y, width, height, renderer.getTheme().MEDIUM, setWidth(renderer, "Press Escape to unfreeze"), Task.TextPosition.CENTER);
        drag(mouse, x, y, width, height);
        return interacted;
    }

    @Override
    public boolean click(MenuIcon menuIcon) {
        if (menuIcon instanceof CloseIcon) {
            _output.unfreeze();
        }
        return false;
    }

    @Override
    public boolean ignoreVisibilityUI() {
        return true;
    }

    @Override
    public boolean isVisible() {
        return _output.isFrozen();
    }
}