package me.therealdan.lights.ui.view;

import me.therealdan.lights.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public class View implements Viewable {

    private String name;
    private Split split;

    private Viewable A = null;
    private Viewable B = null;

    public View(String name, Split split, Viewable A) {
        split(name, split, A);
    }

    public View(String name, Split split, Viewable A, Viewable B) {
        split(name, split, A, B);
    }

    public void split(String name, Split split, Viewable A) {
        split(name, split, A, null);
    }

    public void split(String name, Split split, Viewable A, Viewable B) {
        this.name = name;
        this.split = split;
        this.A = A;
        this.B = B;
    }

    @Override
    public void draw(Renderer renderer, float x, float y, float width, float height) {
        switch (getSplit()) {
            case NONE:
                if (getA() != null) getA().draw(renderer, x, y, width, height);
                return;
            case VERTICAL:
                if (getA() != null) getA().draw(renderer, x, y, width / 2f, height);
                if (getB() != null) getB().draw(renderer, x + width / 2f, y, width / 2f, height);
                return;
            case HORIZONTAL:
                if (getA() != null) getA().draw(renderer, x, y + height / 2f, width, height / 2f);
                if (getB() != null) getB().draw(renderer, x, y, width, height / 2f);
                return;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Split getSplit() {
        return split;
    }

    public Viewable getA() {
        return A;
    }

    public Viewable getB() {
        return B;
    }

    public static List<View> values() {
        return new ArrayList<>();
    }
}