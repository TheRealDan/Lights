package me.therealdan.lights.fixtures;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import me.therealdan.lights.Lights;

public class ModelDesign {

    private static ModelBuilder modelBuilder = new ModelBuilder();

    private Model model;
    private Vector3 dimensions, offset;

    public ModelDesign(float cube) {
        this(cube, 0, 0, 0);
    }

    public ModelDesign(float cube, float xOffset, float yOffset, float zOffset) {
        this(cube, cube, cube, xOffset, yOffset, zOffset);
    }

    public ModelDesign(float width, float height, float depth) {
        this(width, height, depth, 0, 0, 0);
    }

    public ModelDesign(float width, float height, float depth, float xOffset, float yOffset, float zOffset) {
        this.model = modelBuilder.createBox(width, height, depth, new Material(ColorAttribute.createDiffuse(Lights.BLACK)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        this.dimensions = new Vector3(width, height, depth);
        this.offset = new Vector3(xOffset, yOffset, zOffset);
    }

    public Model getModel() {
        return model;
    }

    public Vector3 getDimensions() {
        return dimensions;
    }

    public Vector3 getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return getDimensions().x + ";" + getDimensions().y + ";" + getDimensions().z + ";" + getOffset().x + ";" + getOffset().y + ";" + getOffset().z;
    }

    public static ModelDesign fromString(String string) {
        String[] args = string.split(";");
        return new ModelDesign(
                Float.parseFloat(args[0]),
                Float.parseFloat(args[1]),
                Float.parseFloat(args[2]),
                Float.parseFloat(args[3]),
                Float.parseFloat(args[4]),
                Float.parseFloat(args[5])
        );
    }
}