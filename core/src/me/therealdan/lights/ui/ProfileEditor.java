package me.therealdan.lights.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.ModelDesign;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;

public class ProfileEditor implements Visual {

    // TODO - Finish Profile editor; Channels, Models and how they interact

    private Profile profile;
    private Section section;

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

        if (getProfile() == null) return true;

        // PROFILE OPTIONS
        width = WIDTH / 4;
        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Profile Options", Task.TextPosition.CENTER);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.NAME) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Name: " + getProfile().getName());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.NAME);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.PHYSICAL_CHANNELS) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Physical Channels: " + getProfile().getPhysicalChannels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.PHYSICAL_CHANNELS);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.VIRTUAL_CHANNELS) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Virtual Channels: " + getProfile().getVirtualChannels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.VIRTUAL_CHANNELS);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, isEditing(Section.MODEL) ? Lights.color.DARK_RED : Lights.color.MEDIUM, "Model: " + getProfile().countModels());
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) edit(Section.MODEL);
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.YELLOW, "Save & Close");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
            Lights.openMainView();
            return false;
        }
        y -= cellHeight;

        renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.RED, "Delete");
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
            Profile.delete(getProfile());
            edit((Profile) null);
            Lights.openMainView();
            return false;
        }
        y -= cellHeight;

        x += width;
        y = Y - cellHeight;

        // PHYSICAL CHANNELS
        if (isEditing(Section.PHYSICAL_CHANNELS)) {
            width = WIDTH / 4;
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Physical Channels: " + getProfile().getPhysicalChannels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (int offset = 0; offset < getProfile().getPhysicalChannels(); offset++) {
                StringBuilder channels = new StringBuilder();
                for (Channel channel : getProfile().channels()) {
                    for (int addressOffset : channel.addressOffsets()) {
                        if (addressOffset == offset) {
                            channels.append(", ").append(channel.getType().getName());
                        }
                    }
                }
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, offset + " - " + channels.toString().replaceFirst(", ", ""));
                y -= cellHeight;
            }
            y = Y - cellHeight;
        }

        // VIRTUAL CHANNELS
        if (isEditing(Section.VIRTUAL_CHANNELS)) {
            width = WIDTH / 4;
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Virtual Channels: " + getProfile().getVirtualChannels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (Channel channel : getProfile().channels()) {
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, channel.getType().getName() + " - " + channel.addressOffsetsAsString());
                y -= cellHeight;
            }

            renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.GREEN, "Add Channel");
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
                // TODO - Profile isn't mutable!
            }
            y -= cellHeight;

            x += width;
            y = Y - cellHeight;
        }

        // MODEL
        if (isEditing(Section.MODEL)) {
            width = WIDTH / 4;
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Model: " + getProfile().countModels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (ModelDesign modelDesign : getProfile().getModelDesigns()) {
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, modelDesign.toString());
                y -= cellHeight;
            }
            x += width;
            y = Y - cellHeight;
        }

        // MODEL PREVIEW
        width = WIDTH - x;
        renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Model Preview", Task.TextPosition.CENTER);

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        switch (keycode) {
            case Input.Keys.ESCAPE:
                Lights.openMainView();
                return false;
        }

        if (isEditing(Section.NAME)) {
            switch (keycode) {
                case Input.Keys.BACKSPACE:
                    if (getProfile().getName().length() > 0)
                        getProfile().rename(getProfile().getName().substring(0, getProfile().getName().length() - 1));
                    if (shift) getProfile().rename("");
                    break;
                case Input.Keys.SPACE:
                    getProfile().rename(getProfile().getName() + " ");
                    break;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                        if (!shift) string = string.toLowerCase();
                        getProfile().rename(getProfile().getName() + string);
                    }
            }
        }

        return true;
    }

    public void edit(Profile profile) {
        this.profile = profile;
    }

    private void edit(Section section) {
        this.section = section;
    }

    private boolean isEditing(Section section) {
        return section.equals(this.section);
    }

    private Profile getProfile() {
        return profile;
    }

    public enum Section {
        NAME, PHYSICAL_CHANNELS, VIRTUAL_CHANNELS, MODEL;
    }
}