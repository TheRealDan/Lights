package me.therealdan.lights.fixtures;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

public class ModelDesign {

    private Model model;
    private Vector3 offset, dimensions;

    public ModelDesign(Model model, Vector3 offset, Vector3 dimensions) {
        this.model = model;
        this.offset = offset;
        this.dimensions = dimensions;
    }

    public Model getModel() {
        return model;
    }

    public Vector3 getOffset() {
        return offset;
    }

    public Vector3 getDimensions() {
        return dimensions;
    }
}