package me.therealdan.lights.fixtures;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

public class Model {

    private ModelInstance modelInstance;
    private Vector3 position, offset, dimensions;

    public Model(ModelInstance modelInstance, Vector3 position, Vector3 offset, Vector3 dimensions) {
        this.modelInstance = modelInstance;
        this.position = position;
        this.offset = offset;
        this.dimensions = dimensions;
    }

    public void setColor(Color color) {
        getModelInstance().materials.get(0).set(ColorAttribute.createDiffuse(color));
    }

    public void move(float x, float y, float z) {
        getPosition().add(x, y, z);
        getModelInstance().transform.setTranslation(getPosition());
    }

    public void teleport(float x, float y, float z, boolean offset) {
        if (offset) {
            x += getOffset().x;
            y += getOffset().y;
            z += getOffset().z;
        }
        getModelInstance().transform.setTranslation(position.set(x, y, z));
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getOffset() {
        return offset;
    }

    public Vector3 getDimensions() {
        return dimensions;
    }
}