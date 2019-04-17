package me.therealdan.lights.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.therealdan.lights.fixtures.Channel;
import me.therealdan.lights.fixtures.ModelDesign;
import me.therealdan.lights.fixtures.MutableProfile;
import me.therealdan.lights.fixtures.Profile;
import me.therealdan.lights.main.Lights;
import me.therealdan.lights.renderer.Renderer;
import me.therealdan.lights.renderer.Task;

public class ProfileEditor implements Visual {

    // TODO - Finish Profile editor; Channels, Models and how they interact

    private Profile profile;
    private MutableProfile mutableProfile;
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

        if (getMutableProfile() == null) return true;

        // PROFILE OPTIONS
        width = WIDTH / 4;
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
        if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
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
            width = WIDTH / 4;
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
            y = Y - cellHeight;
        }

        // VIRTUAL CHANNELS
        if (isEditing(Section.VIRTUAL_CHANNELS)) {
            width = WIDTH / 4;
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Virtual Channels: " + getMutableProfile().getVirtualChannels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (Channel channel : getMutableProfile().channels()) {
                renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, channel.getType().getName() + " - " + channel.addressOffsetsAsString());
                y -= cellHeight;
            }

            renderer.box(x, y, width, cellHeight, Lights.color.MEDIUM, Lights.color.GREEN, "Add Channel");
            if (Lights.mouse.contains(x, y, width, cellHeight) && Lights.mouse.leftClicked()) {
                // TODO TEST - 1. This button adds a channel 2. Not saving doesn't change profile 3. Saving changes profile
                getMutableProfile().addChannel(Channel.DEFAULT_TYPE);
            }
            y -= cellHeight;

            x += width;
            y = Y - cellHeight;
        }

        // MODEL
        if (isEditing(Section.MODEL)) {
            width = WIDTH / 4;
            renderer.box(x, y, width, cellHeight, Lights.color.DARK_BLUE, "Model: " + getMutableProfile().countModels(), Task.TextPosition.CENTER);
            y -= cellHeight;
            for (ModelDesign modelDesign : getMutableProfile().getModelDesigns()) {
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
                    if (getMutableProfile().getName().length() > 0)
                        getMutableProfile().rename(getMutableProfile().getName().substring(0, getMutableProfile().getName().length() - 1));
                    if (shift) getMutableProfile().rename("");
                    break;
                case Input.Keys.SPACE:
                    getMutableProfile().rename(getMutableProfile().getName() + " ");
                    break;
                default:
                    String string = Input.Keys.toString(keycode);
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".contains(string)) {
                        if (!shift) string = string.toLowerCase();
                        getMutableProfile().rename(getMutableProfile().getName() + string);
                    }
            }
        }

        return true;
    }

    public void edit(Profile profile) {
        this.profile = profile;
        this.mutableProfile = profile != null ? new MutableProfile(profile) : null;
    }

    private void edit(Section section) {
        this.section = section;
    }

    private boolean isEditing(Section section) {
        return section.equals(this.section);
    }

    public MutableProfile getMutableProfile() {
        return mutableProfile;
    }

    public enum Section {
        NAME, PHYSICAL_CHANNELS, VIRTUAL_CHANNELS, MODEL;
    }
}