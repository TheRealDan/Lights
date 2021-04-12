package dev.therealdan.lights.panels;

import dev.therealdan.lights.renderer.Renderer;

public interface MenuIcon {

    float SIZE = 20;

    // Return whether click caused an action
    default boolean click(Panel panel) {
        System.out.println("Clicked " + getFriendlyName() + " on " + panel.getFriendlyName());
        return true;
    }

    default boolean draw(Renderer renderer, float x, float y, float width, float height, int index, boolean hover, boolean click) {
        renderer.box(x, y, width / 2, height / 2, renderer.getTheme().MAGENTA);
        renderer.box(x + width / 2, y, width / 2, height / 2, renderer.getTheme().BLACK);
        renderer.box(x, y - height / 2, width / 2, height / 2, renderer.getTheme().BLACK);
        renderer.box(x + width / 2, y - height / 2, width / 2, height / 2, renderer.getTheme().MAGENTA);
        return false;
    }

    default String getFriendlyName() {
        StringBuilder stringBuilder = new StringBuilder();
        for (char letter : getName().replace("DMX", "[dmx]").toCharArray()) {
            if (Character.isUpperCase(letter) && stringBuilder.length() > 0) stringBuilder.append(" ");
            stringBuilder.append(letter);
        }
        return stringBuilder.toString().replace("[dmx]", "DMX");
    }

    default String getName() {
        return getClass().getSimpleName();
    }

    default boolean isVisible() {
        return true;
    }

    static float getSpacing() {
        return (Panel.MENU_HEIGHT - SIZE) / 2;
    }
}