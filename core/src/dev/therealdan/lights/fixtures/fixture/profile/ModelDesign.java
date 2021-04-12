package dev.therealdan.lights.fixtures.fixture.profile;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import dev.therealdan.lights.main.Lights;

public class ModelDesign {

    private static ModelBuilder modelBuilder = new ModelBuilder();

    private Model model;
    private Vector3 dimensions, offset;

    public ModelDesign(float cube) {
        build(cube, cube, cube, 0, 0, 0);
    }

    public ModelDesign(float cube, float xOffset, float yOffset, float zOffset) {
        build(cube, cube, cube, xOffset, yOffset, zOffset);
    }

    public ModelDesign(float width, float height, float depth) {
        build(width, height, depth, 0, 0, 0);
    }

    public ModelDesign(float width, float height, float depth, float xOffset, float yOffset, float zOffset) {
        build(width, height, depth, xOffset, yOffset, zOffset);
    }

    private void build(float width, float height, float depth, float xOffset, float yOffset, float zOffset) {
        this.model = modelBuilder.createBox(width, height, depth, new Material(ColorAttribute.createDiffuse(Lights.theme.BLACK)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        this.dimensions = new Vector3(width, height, depth);
        this.offset = new Vector3(xOffset, yOffset, zOffset);
    }

    public void setDimensions(float width, float height, float depth) {
        build(width, height, depth, getOffset().x, getOffset().y, getOffset().z);
    }

    public void setXOffset(float x) {
        this.offset.x = x;
    }

    public void setYOffset(float y) {
        this.offset.y = y;
    }

    public void setZOffset(float z) {
        this.offset.z = z;
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

    @Override
    public ModelDesign clone() {
        return new ModelDesign(dimensions.x, dimensions.y, dimensions.z, offset.x, offset.y, offset.z);
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