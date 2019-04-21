package me.therealdan.lights.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import me.therealdan.lights.fixtures.*;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProfileEditor implements Visual {

    // TODO - Finish Profile editor; Channels, Models and how they interact

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private PerspectiveCamera camera;
    private Environment environment;
    private ModelBatch modelBatch;
    private List<Model> models = new ArrayList<>();

    private Profile profile;
    private MutableProfile mutableProfile;

    private Section section;
    private ModelSetting modelSetting;

    private Channel channel;
    private ModelDesign modelDesign;

    private boolean mouseInPreviewArea = false;
    private float degreesPerPixel = 0.5f;
    private Vector3 tmp = new Vector3();

    public ProfileEditor() {
        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 10f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 1000f;
        camera.update();

        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, 1f, 0.8f, 0.2f));
    }

    @Override
    public boolean draw(Renderer renderer) {
        float X = 0;
        float Y = Gdx.graphics.getHeight();
        float WIDTH = Gdx.graphics.getWidth();
        float cellHeight = 30;

        float x = X;
        float y = Y;
        float width = WIDTH;

        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Profile Editor", Task.TextPosition.CENTER);
        y -= cellHeight;

        if (getMutableProfile() == null) return true;

        // PROFILE OPTIONS
        width = WIDTH / 2 / 3;
        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Profile Options", Task.TextPosition.CENTER);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.NAME) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Name: " + getMutableProfile().getName());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.NAME);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.PHYSICAL_CHANNELS) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Physical Channels: " + getMutableProfile().getPhysicalChannels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.PHYSICAL_CHANNELS);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.VIRTUAL_CHANNELS) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Virtual Channels: " + getMutableProfile().getVirtualChannels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.VIRTUAL_CHANNELS);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.MODEL) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Model: " + getMutableProfile().countModels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.MODEL);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Save Changes");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
            profile.update(getMutableProfile());
            return false;
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Clear Changes");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
            edit(profile);
            return false;
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Close Editor");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
            Lights.openMainView();
            return false;
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete Profile");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.keyboard.isShift() && Lights.mouse.leftClicked()) {
            Profile.delete(getMutableProfile());
            edit((Profile) null);
            Lights.openMainView();
            return false;
        }
        y -= cellHeight;

        x += width;
        y = Y - cellHeight;

        // PHYSICAL CHANNELS
        if (isEditing(Section.PHYSICAL_CHANNELS)) {
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Physical Channels: " + getMutableProfile().getPhysicalChannels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (int offset = 0; offset < getMutableProfile().getPhysicalChannels(); offset++) {
                StringBuilder channels = new StringBuilder();
                for (Channel channel : getMutableProfile().channels()) {
                    for (int addressOffset : channel.addressOffsets()) {
                        if (addressOffset == offset) {
                            channels.append(", ").append(channel.getType().getName());
                        }
                    }
                }
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, offset + " - " + channels.toString().replaceFirst(", ", ""));
                y -= cellHeight;
            }
            x += width;
            y = Y - cellHeight;
        }

        // VIRTUAL CHANNELS
        if (isEditing(Section.VIRTUAL_CHANNELS)) {
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Virtual Channels: " + getMutableProfile().getVirtualChannels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (Channel channel : getMutableProfile().channels()) {
                renderer.box(x, y, width, cellHeight, channel.equals(getSelectedChannel()) ? Lights.color.DARK_RED : Lights.color.MEDIUM, channel.getType().getName() + " - " + channel.addressOffsetsAsString());
                if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
                    select(channel);
                }
                y -= cellHeight;
            }

            if (hasChannelSelected()) {
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Change Type");
                if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked(500)) {
                    getMutableProfile().changeType(getSelectedChannel());
                }
                y -= cellHeight;
            }

            renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.GREEN, "Add Channel");
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
                getMutableProfile().addChannel(Channel.DEFAULT_TYPE);
            }
            y -= cellHeight;

            if (hasChannelSelected()) {
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete Channel");
                if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.keyboard.isShift() && Lights.mouse.leftClicked()) {
                    getMutableProfile().removeChannel(getSelectedChannel());
                    select((Channel) null);
                }
                y -= cellHeight;
            }

            x += width;
            y = Y - cellHeight;
        }

        // MODELS
        if (isEditing(Section.MODEL)) {
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Models: " + getMutableProfile().countModels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (ModelDesign modelDesign : getMutableProfile().getModelDesigns()) {
                renderer.box(x, y, width, cellHeight, modelDesign.equals(getSelectedModelDesign()) ? Lights.color.DARK_RED : Lights.color.MEDIUM, modelDesign.toString());
                if (Lights.mouse.contains(x, y, width, cellHeight)) {
                    if (Lights.mouse.leftClicked()) {
                        select(modelDesign);
                    }
                }
                y -= cellHeight;
            }

            renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Update Models");
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked(500)) {
                rebuildModelInstances();
            }
            y -= cellHeight;

            renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.GREEN, "Add Model");
            if (Lights.mouse.contains(x, y, width, cellHeight)) {
                if (Lights.mouse.leftClicked(500)) {
                    getMutableProfile().addModelDesign(new ModelDesign(1));
                }
            }
            y -= cellHeight;

            if (hasModelDesignSelected()) {
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete Model");
                if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.keyboard.isShift() && Lights.mouse.leftClicked(500)) {
                    getMutableProfile().removeModelDesign(getSelectedModelDesign());
                    select((ModelDesign) null);
                }
            }
            y -= cellHeight;

            x += width;
            y = Y - cellHeight;
        }

        // MODEL SETTINGS
        if (hasModelDesignSelected()) {
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Model Settings", Task.TextPosition.CENTER);
            y -= cellHeight;

            renderer.box(x, y, width, cellHeight, isEditing(ModelSetting.WIDTH) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Width: " + getSelectedModelDesign().getDimensions().x);
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(ModelSetting.WIDTH);
            y -= cellHeight;

            renderer.box(x, y, width, cellHeight, isEditing(ModelSetting.HEIGHT) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Height: " + getSelectedModelDesign().getDimensions().y);
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(ModelSetting.HEIGHT);
            y -= cellHeight;

            renderer.box(x, y, width, cellHeight, isEditing(ModelSetting.DEPTH) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Depth: " + getSelectedModelDesign().getDimensions().z);
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(ModelSetting.DEPTH);
            y -= cellHeight;

            renderer.box(x, y, width, cellHeight, isEditing(ModelSetting.X_OFFSET) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "X Offset: " + getSelectedModelDesign().getOffset().x);
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(ModelSetting.X_OFFSET);
            y -= cellHeight;

            renderer.box(x, y, width, cellHeight, isEditing(ModelSetting.Y_OFFSET) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Y Offset: " + getSelectedModelDesign().getOffset().y);
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(ModelSetting.Y_OFFSET);
            y -= cellHeight;

            renderer.box(x, y, width, cellHeight, isEditing(ModelSetting.Z_OFFSET) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Z Offset: " + getSelectedModelDesign().getOffset().z);
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(ModelSetting.Z_OFFSET);
            y -= cellHeight;

            x += width;
            y = Y - cellHeight;
        }

        // MODEL PREVIEW
        width = WIDTH - x;
        mouseInPreviewArea = Lights.mouse.contains(x, y, width, Gdx.graphics.getHeight());
        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Model Preview", Task.TextPosition.CENTER);

        resize((int) width, Gdx.graphics.getHeight());
        Gdx.gl.glViewport((int) x, 0, (int) width, Gdx.graphics.getHeight());

        camera.update();
        modelBatch.begin(camera);
        for (Model model : getModels())
            modelBatch.render(model.getModelInstance(), environment);
        modelBatch.end();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        return true;
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (mouseInPreviewArea) {
            float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
            float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
            camera.direction.rotate(camera.up, deltaX);
            tmp.set(camera.direction).crs(camera.up).nor();
            camera.direction.rotate(tmp, deltaY);
        }
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Lights.keyboard.isShift();

        switch (keycode) {
            case Input.Keys.ESCAPE:
                Lights.openMainView();
                return false;
        }

        if (isEditing(Section.NAME)) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getMutableProfile().getName().length() > 0)
                        getMutableProfile().rename(getMutableProfile().getName().substring(0, getMutableProfile().getName().length() - 1));
                    if (shift) getMutableProfile().rename("");
                    return false;
                case Input.Keys.SPACE:
                    getMutableProfile().rename(getMutableProfile().getName() + " ");
                    return false;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                        if (!shift) string = string.toLowerCase();
                        getMutableProfile().rename(getMutableProfile().getName() + string);
                    }
                    return false;
            }
        }

        if (isEditing(Section.VIRTUAL_CHANNELS) && hasChannelSelected()) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getSelectedChannel().countAddressOffsets() == 0) return false;
                    if (Integer.toString(getSelectedChannel().getLastAddressOffset()).length() == 1) {
                        getMutableProfile().removeLastOffset(getSelectedChannel());
                    } else {
                        String lastOffset = Integer.toString(getSelectedChannel().getLastAddressOffset());
                        getMutableProfile().removeLastOffset(getSelectedChannel());
                        getMutableProfile().addOffset(getSelectedChannel(), Integer.parseInt(lastOffset.substring(0, lastOffset.length() - 1)));
                    }
                    return false;
                case Input.Keys.ENTER:
                    getMutableProfile().addOffset(getSelectedChannel(), 0);
                    return false;
                default:
                    if (getSelectedChannel().countAddressOffsets() == 0) return false;
                    String string = Input.Keys.toString(keycode);
                    if ("1234567890".contains(string)) {
                        string = getSelectedChannel().getLastAddressOffset() + string;
                        getMutableProfile().removeLastOffset(getSelectedChannel());
                        getMutableProfile().addOffset(getSelectedChannel(), Integer.parseInt(string));

                        if (getSelectedChannel().getLastAddressOffset() > 511) {
                            getMutableProfile().removeLastOffset(getSelectedChannel());
                            getMutableProfile().addOffset(getSelectedChannel(), 511);
                        }
                    }
                    return false;
            }
        }

        if (hasModelDesignSelected()) {
            switch (keycode) {
                case Input.Keys.MINUS:
                    setModelSettingValue(getModelSettingValue().contains("-") ? getModelSettingValue().replace("-", "") : "-" + getModelSettingValue());
                    return false;
                case Input.Keys.ENTER:
                    rebuildModelInstances();
                    return false;
                case Input.Keys.BACKSPACE:
                    String value = getModelSettingValue();
                    value = value.substring(0, value.length() - 1);
                    setModelSettingValue(value);
                    return false;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("1234567890.".contains(string)) {
                        setModelSettingValue(getModelSettingValue() + string);
                    }
                    return false;
            }
        }

        return true;
    }

    private void rebuildModelInstances() {
        models.clear();

        for (ModelDesign modelDesign : getMutableProfile().getModelDesigns()) {
            Vector3 position = new Vector3(
                    modelDesign.getOffset().x,
                    modelDesign.getOffset().y,
                    modelDesign.getOffset().z
            );
            models.add(new Model(modelDesign, position));
        }
    }

    private void select(Channel channel) {
        this.channel = channel;
    }

    private void select(ModelDesign modelDesign) {
        this.modelDesign = modelDesign;
    }

    public void edit(Profile profile) {
        this.profile = profile;
        this.mutableProfile = profile != null ? new MutableProfile(profile) : null;
        this.channel = null;
    }

    private void edit(Section section) {
        if (!section.equals(Section.MODEL)) this.modelDesign = null;
        this.section = section;
    }

    private void edit(ModelSetting modelSetting) {
        this.modelSetting = modelSetting;
    }

    private boolean isEditing(Section section) {
        return section.equals(this.section);
    }

    private boolean isEditing(ModelSetting modelSetting) {
        return modelSetting.equals(this.modelSetting);
    }

    private void setModelSettingValue(String value) {
        if (value.length() == 0) value = "0";
        if (value.equals("-")) value = "0";
        switch (modelSetting) {
            case WIDTH:
                getSelectedModelDesign().setDimensions(Float.parseFloat(value), getSelectedModelDesign().getDimensions().y, getSelectedModelDesign().getDimensions().z);
                break;
            case HEIGHT:
                getSelectedModelDesign().setDimensions(getSelectedModelDesign().getDimensions().x, Float.parseFloat(value), getSelectedModelDesign().getDimensions().z);
                break;
            case DEPTH:
                getSelectedModelDesign().setDimensions(getSelectedModelDesign().getDimensions().x, getSelectedModelDesign().getDimensions().y, Float.parseFloat(value));
                break;
            case X_OFFSET:
                getSelectedModelDesign().setXOffset(Float.parseFloat(value));
                break;
            case Y_OFFSET:
                getSelectedModelDesign().setYOffset(Float.parseFloat(value));
                break;
            case Z_OFFSET:
                getSelectedModelDesign().setZOffset(Float.parseFloat(value));
                break;
        }
    }

    private String getModelSettingValue() {
        switch (modelSetting) {
            case WIDTH:
                return decimalFormat.format(getSelectedModelDesign().getDimensions().x);
            case HEIGHT:
                return decimalFormat.format(getSelectedModelDesign().getDimensions().y);
            case DEPTH:
                return decimalFormat.format(getSelectedModelDesign().getDimensions().z);
            case X_OFFSET:
                return decimalFormat.format(getSelectedModelDesign().getOffset().x);
            case Y_OFFSET:
                return decimalFormat.format(getSelectedModelDesign().getOffset().y);
            case Z_OFFSET:
                return decimalFormat.format(getSelectedModelDesign().getOffset().z);
        }
        return "0";
    }

    public MutableProfile getMutableProfile() {
        return mutableProfile;
    }

    private boolean hasChannelSelected() {
        return getSelectedChannel() != null;
    }

    private Channel getSelectedChannel() {
        return channel;
    }

    private boolean hasModelDesignSelected() {
        return getSelectedModelDesign() != null;
    }

    private ModelDesign getSelectedModelDesign() {
        return modelDesign;
    }

    private List<Model> getModels() {
        return new ArrayList<>(models);
    }

    public enum Section {
        NAME, PHYSICAL_CHANNELS, VIRTUAL_CHANNELS, MODEL,
    }

    public enum ModelSetting {
        WIDTH, HEIGHT, DEPTH, X_OFFSET, Y_OFFSET, Z_OFFSET,
    }
}